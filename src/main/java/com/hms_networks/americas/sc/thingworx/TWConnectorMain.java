package com.hms_networks.americas.sc.thingworx;

import com.ewon.ewonitf.EWException;
import com.ewon.ewonitf.TagControl;
import com.hms_networks.americas.sc.extensions.config.exceptions.ConfigFileException;
import com.hms_networks.americas.sc.extensions.config.exceptions.ConfigFileWriteException;
import com.hms_networks.americas.sc.extensions.historicaldata.HistoricalDataQueueManager;
import com.hms_networks.americas.sc.extensions.json.JSONException;
import com.hms_networks.americas.sc.extensions.logging.Logger;
import com.hms_networks.americas.sc.extensions.system.http.SCHttpUtility;
import com.hms_networks.americas.sc.extensions.system.tags.SCTagUtils;
import com.hms_networks.americas.sc.extensions.system.time.SCTimeUnit;
import com.hms_networks.americas.sc.extensions.system.time.SCTimeUtils;
import com.hms_networks.americas.sc.extensions.taginfo.TagInfoManager;
import com.hms_networks.americas.sc.thingworx.config.TWConnectorConfig;
import com.hms_networks.americas.sc.thingworx.data.TWApiManager;
import com.hms_networks.americas.sc.thingworx.data.TWDataManager;
import com.hms_networks.americas.sc.thingworx.data.TWTagUpdateManager;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

/**
 * Ewon Flexy Thingworx Connector main class.
 *
 * @author HMS Networks, MU Americas Solution Center
 * @since 1.0
 */
public class TWConnectorMain {

  /** String used to identify a number or count of days. */
  private static final String DAYS_STRING = "days";

  /** String used to identify a number or count of hours. */
  private static final String HOURS_STRING = "hours";

  /** String used to identify a number or count of minutes. */
  private static final String MINUTES_STRING = "minutes";

  /** String used to identify a number or count of seconds. */
  private static final String SECONDS_STRING = "seconds";

  /** Connector configuration object */
  private static TWConnectorConfig connectorConfig;

  /** Current available memory in bytes */
  private static long availableMemoryBytes;

  /** Boolean flag indicating if the application is running out of memory */
  private static boolean isMemoryCurrentlyLow;

  /** Count of the number of times the application has polled the queue for data. */
  private static long connectorQueuePollCount = 0;

  /**
   * Tag control object used for updating the value of the queue diagnostic tag for the amount of
   * time running behind in seconds.
   */
  private static TagControl queueDiagnosticRunningBehindSecondsTag = null;

  /**
   * Tag control object used for getting the value of the queue diagnostic tag for forcing a reset
   * of the time tracker.
   */
  private static TagControl queueDiagnosticForceResetTag = null;

  /**
   * Tag control object used for updating the value of the queue diagnostic tag for the queue poll
   * count.
   */
  private static TagControl queueDiagnosticPollCountTag = null;

  /** Boolean used to track if the queue data rate has been doubled. */
  private static boolean queueDoubleDataRateEnabled = false;

  /**
   * Gets the connector configuration object.
   *
   * @return connector configuration
   */
  public static TWConnectorConfig getConnectorConfig() {
    return connectorConfig;
  }

  /**
   * Read data from the historical log queue and sends to payload manager. If memory is low, show a
   * warning and skip reading data.
   */
  private static void runGrabData() {
    // Check if memory is within permissible range to poll data queue
    if (availableMemoryBytes < TWConnectorConsts.QUEUE_DATA_POLL_MIN_MEMORY_BYTES) {
      // Show low memory warning
      Logger.LOG_WARN("Low memory on device, " + (availableMemoryBytes / 1000) + " MB left!");

      // If low memory flag not set, set it and request garbage collection
      if (!isMemoryCurrentlyLow) {
        // Set low memory flag
        isMemoryCurrentlyLow = true;

        // Tell the JVM that it should garbage collect soon
        System.gc();
      }
    } else {
      // There is enough memory to run, reset memory state variable.
      if (isMemoryCurrentlyLow) {
        isMemoryCurrentlyLow = false;
      }

      // Increment poll count, and reset to 0 if too large
      connectorQueuePollCount++;
      if (connectorQueuePollCount == Long.MAX_VALUE) {
        connectorQueuePollCount = 0;
      }
      if (queueDiagnosticPollCountTag != null) {
        try {
          queueDiagnosticPollCountTag.setTagValueAsLong(connectorQueuePollCount);
        } catch (EWException e) {
          Logger.LOG_CRITICAL("Unable to set queue diagnostic poll count tag value!");
          Logger.LOG_EXCEPTION(e);
        }
      }

      try {
        // Check if a queue force reset has been requested
        boolean forceReset = false;
        if (queueDiagnosticForceResetTag != null) {
          forceReset =
              queueDiagnosticForceResetTag.getTagValueAsInt()
                  == TWConnectorConsts.QUEUE_DIAGNOSTIC_TAG_FORCE_RESET_TRUE_VALUE;

          if (forceReset) {
            Logger.LOG_SERIOUS(
                "A force reset of the queue has been requested using the tag `"
                    + TWConnectorConsts.QUEUE_DIAGNOSTIC_TAG_FORCE_RESET_NAME
                    + "`. A new queue time tracker will be created at the current time!");
          }
        }

        // Read data points from queue
        ArrayList datapointsReadFromQueue;
        if (HistoricalDataQueueManager.doesTimeTrackerExist() && !forceReset) {
          final boolean startNewTimeTracker = false;
          datapointsReadFromQueue =
              HistoricalDataQueueManager.getFifoNextSpanDataAllGroups(startNewTimeTracker);
        } else {
          final boolean startNewTimeTracker = true;
          datapointsReadFromQueue =
              HistoricalDataQueueManager.getFifoNextSpanDataAllGroups(startNewTimeTracker);
        }

        // Reset queue force reset tag value to false, if true
        if (queueDiagnosticForceResetTag != null && forceReset) {
          queueDiagnosticForceResetTag.setTagValueAsInt(
              TWConnectorConsts.QUEUE_DIAGNOSTIC_TAG_FORCE_RESET_FALSE_VALUE);
        }

        Logger.LOG_DEBUG(
            "Read " + datapointsReadFromQueue.size() + " data points from the historical log.");

        // Send data to Thingworx
        TWDataManager.addDataPointsToPending(datapointsReadFromQueue);

        // Check if queue is behind
        try {
          long queueBehindMillis = HistoricalDataQueueManager.getQueueTimeBehindMillis();
          if (queueBehindMillis >= TWConnectorConsts.QUEUE_DATA_POLL_BEHIND_MILLIS_WARN) {
            String timeBehindString =
                SCTimeUtils.getDayHourMinSecsForMillis(
                    (int) queueBehindMillis,
                    DAYS_STRING,
                    HOURS_STRING,
                    MINUTES_STRING,
                    SECONDS_STRING);
            Logger.LOG_WARN(
                "The historical data queue is running behind by " + timeBehindString + ".");
          } else {
            // Queue is not past running behind threshold, reset to 0 for updating debug tag
            queueBehindMillis = 0;
          }

          // Update available memory variable
          availableMemoryBytes = Runtime.getRuntime().freeMemory();

          // Enable queue double data rate if running behind and memory is available
          if (!queueDoubleDataRateEnabled
              && queueBehindMillis > 0
              && availableMemoryBytes > TWConnectorConsts.QUEUE_DATA_POLL_MIN_MEMORY_BYTES) {
            long doubleQueueDataPollSizeMins = connectorConfig.getQueueDataPollSizeMinutes() * 2;
            HistoricalDataQueueManager.setQueueFifoTimeSpanMins(doubleQueueDataPollSizeMins);
            Logger.LOG_SERIOUS(
                "The size of data polled on each interval has been doubled to "
                    + doubleQueueDataPollSizeMins
                    + " minutes while the queue is running behind!");
            queueDoubleDataRateEnabled = true;
          }
          // Disable queue double data rate if no longer running behind
          else if (queueDoubleDataRateEnabled && queueBehindMillis == 0) {
            long queueDataPollSizeMins = connectorConfig.getQueueDataPollSizeMinutes();
            HistoricalDataQueueManager.setQueueFifoTimeSpanMins(queueDataPollSizeMins);
            Logger.LOG_SERIOUS(
                "The size of data polled on each interval has been restored to "
                    + queueDataPollSizeMins
                    + " minutes.");
            queueDoubleDataRateEnabled = false;
          }

          // Update queue debug tag
          if (queueDiagnosticRunningBehindSecondsTag != null) {
            queueDiagnosticRunningBehindSecondsTag.setTagValueAsLong(
                SCTimeUnit.MILLISECONDS.toSeconds(queueBehindMillis));
          }

        } catch (IOException e) {
          Logger.LOG_SERIOUS("Unable to detect if historical data queue is running behind.");
          Logger.LOG_EXCEPTION(e);
        }

      } catch (Exception e) {
        Logger.LOG_CRITICAL("An error occurred while reading data from the historical log.");
        Logger.LOG_EXCEPTION(e);
        // Chances are that trying the exact same read next time will result in the same exception,
        // so advance the start time tracking
        try {
          HistoricalDataQueueManager.advanceTrackingStartTime();
          Logger.LOG_WARN(
              "Advancing historical start time, because an exception was thrown. This will"
                  + " result in historical data loss.");
        } catch (Exception e1) {
          Logger.LOG_CRITICAL("An error occurred while attempting to advance the historical log.");
          Logger.LOG_EXCEPTION(e1);
        }
      }
    }
  }

  /** Detects the provisioning mode and reads the configuration appropriately from file. */
  private static void loadConfiguration() {
    // Load connector configuration
    connectorConfig = new TWConnectorConfig();

    // If configuration exists on disk, read from disk, otherwise write new default configuration
    if (connectorConfig.fileExists()) {
      try {
        connectorConfig.read();
      } catch (ConfigFileException e) {
        Logger.LOG_CRITICAL(
            "Unable to read configuration file at "
                + connectorConfig.getConfigFilePath()
                + ". Check that it is properly formatted.");
        Logger.LOG_EXCEPTION(e);
      }
    } else {
      try {
        connectorConfig.loadAndSaveDefaultConfiguration();
      } catch (ConfigFileWriteException e) {
        Logger.LOG_CRITICAL("Unable to write default configuration file.");
        Logger.LOG_EXCEPTION(e);
      }
    }
  }

  /** Configures the logger to the logging level specified in the configuration. */
  private static void configLogger() {
    // Configure logger to desired log level
    int loglevel;
    try {
      loglevel = connectorConfig.getConnectorLogLevel();
    } catch (JSONException e) {
      Logger.LOG_SERIOUS(
          "Unable to read log level from connector configuration. Using default ("
              + TWConnectorConsts.CONNECTOR_CONFIG_DEFAULT_LOG_LEVEL
              + ").");
      Logger.LOG_EXCEPTION(e);
      loglevel = TWConnectorConsts.CONNECTOR_CONFIG_DEFAULT_LOG_LEVEL;
    }
    Logger.SET_LOG_LEVEL(loglevel);
  }

  /**
   * Configures the maximum duration in the historical buffer that the connector will try to sync.
   */
  private static void configHistoricalDataQueueMaxFallBehind() {
    try {
      HistoricalDataQueueManager.setQueueMaxBehindMins(
          connectorConfig.getConnectorHistoBuffFallBehindMins());
    } catch (JSONException e) {
      Logger.LOG_SERIOUS(
          "Unable to read max historical buffer fall back mins from connector configuration. Using"
              + " default ("
              + TWConnectorConsts.CONNECTOR_CONFIG_MAX_HIST_BUF_FALL_BEHIND_MINS
              + ") minutes.");
      Logger.LOG_EXCEPTION(e);

      HistoricalDataQueueManager.setQueueMaxBehindMins(
          TWConnectorConsts.CONNECTOR_CONFIG_MAX_HIST_BUF_FALL_BEHIND_MINS);
    }
  }

  /**
   * Configures the queue diagnostic tags if the {@link
   * TWConnectorConfig#getQueueDiagnosticTagsEnabled()} setting is enabled.
   */
  private static void configureQueueDiagnosticTags() {
    if (connectorConfig.getQueueDiagnosticTagsEnabled()) {
      // Configure queue running behind diagnostic tag
      try {
        queueDiagnosticRunningBehindSecondsTag =
            new TagControl(TWConnectorConsts.QUEUE_DIAGNOSTIC_TAG_RUNNING_BEHIND_SECONDS_NAME);
      } catch (Exception e1) {
        Logger.LOG_INFO(
            "Unable to create tag object to update diagnostic tag for the queue running behind"
                + " time! Attempting to create `"
                + TWConnectorConsts.QUEUE_DIAGNOSTIC_TAG_RUNNING_BEHIND_SECONDS_NAME
                + "` tag.");
        Logger.LOG_EXCEPTION(e1);
        try {
          SCTagUtils.createTag(
              TWConnectorConsts.QUEUE_DIAGNOSTIC_TAG_RUNNING_BEHIND_SECONDS_NAME,
              TWConnectorConsts.QUEUE_DIAGNOSTIC_TAG_RUNNING_BEHIND_SECONDS_DESC,
              TWConnectorConsts.QUEUE_DIAGNOSTIC_TAG_IO_SERVER,
              TWConnectorConsts.QUEUE_DIAGNOSTIC_TAG_RUNNING_BEHIND_SECONDS_TYPE);
          queueDiagnosticRunningBehindSecondsTag =
              new TagControl(TWConnectorConsts.QUEUE_DIAGNOSTIC_TAG_RUNNING_BEHIND_SECONDS_NAME);
        } catch (Exception e2) {
          Logger.LOG_WARN(
              "Unable to create tag `"
                  + TWConnectorConsts.QUEUE_DIAGNOSTIC_TAG_RUNNING_BEHIND_SECONDS_NAME
                  + "`! To see the queue running behind time in seconds, please create a tag with"
                  + " the name `"
                  + TWConnectorConsts.QUEUE_DIAGNOSTIC_TAG_RUNNING_BEHIND_SECONDS_NAME
                  + "`.");
          Logger.LOG_EXCEPTION(e2);
        }
      }

      // Configure queue reset tag
      try {
        queueDiagnosticForceResetTag =
            new TagControl(TWConnectorConsts.QUEUE_DIAGNOSTIC_TAG_FORCE_RESET_NAME);
      } catch (Exception e1) {
        Logger.LOG_INFO(
            "Unable to create tag object to check for a request to force reset the connector queue!"
                + " Attempting to create `"
                + TWConnectorConsts.QUEUE_DIAGNOSTIC_TAG_FORCE_RESET_NAME
                + "` tag.");
        Logger.LOG_EXCEPTION(e1);
        try {
          SCTagUtils.createTag(
              TWConnectorConsts.QUEUE_DIAGNOSTIC_TAG_FORCE_RESET_NAME,
              TWConnectorConsts.QUEUE_DIAGNOSTIC_TAG_FORCE_RESET_DESC,
              TWConnectorConsts.QUEUE_DIAGNOSTIC_TAG_IO_SERVER,
              TWConnectorConsts.QUEUE_DIAGNOSTIC_TAG_FORCE_RESET_TYPE);
          queueDiagnosticForceResetTag =
              new TagControl(TWConnectorConsts.QUEUE_DIAGNOSTIC_TAG_FORCE_RESET_NAME);
        } catch (Exception e2) {
          Logger.LOG_WARN(
              "Unable to create tag `"
                  + TWConnectorConsts.QUEUE_DIAGNOSTIC_TAG_FORCE_RESET_NAME
                  + "`! To request a force reset of the connector queue, please create a tag with"
                  + " the name `"
                  + TWConnectorConsts.QUEUE_DIAGNOSTIC_TAG_FORCE_RESET_NAME
                  + "`.");
          Logger.LOG_EXCEPTION(e2);
        }
      }

      // Configure queue poll count tag
      try {
        queueDiagnosticPollCountTag =
            new TagControl(TWConnectorConsts.QUEUE_DIAGNOSTIC_TAG_POLL_COUNT_NAME);
      } catch (Exception e1) {
        Logger.LOG_INFO(
            "Unable to create tag object to update diagnostic tag for the queue poll count!"
                + " Attempting to create `"
                + TWConnectorConsts.QUEUE_DIAGNOSTIC_TAG_POLL_COUNT_NAME
                + "` tag.");
        Logger.LOG_EXCEPTION(e1);
        try {
          SCTagUtils.createTag(
              TWConnectorConsts.QUEUE_DIAGNOSTIC_TAG_POLL_COUNT_NAME,
              TWConnectorConsts.QUEUE_DIAGNOSTIC_TAG_POLL_COUNT_DESC,
              TWConnectorConsts.QUEUE_DIAGNOSTIC_TAG_IO_SERVER,
              TWConnectorConsts.QUEUE_DIAGNOSTIC_TAG_POLL_COUNT_TYPE);
          queueDiagnosticPollCountTag =
              new TagControl(TWConnectorConsts.QUEUE_DIAGNOSTIC_TAG_POLL_COUNT_NAME);
        } catch (Exception e2) {
          Logger.LOG_WARN(
              "Unable to create tag `"
                  + TWConnectorConsts.QUEUE_DIAGNOSTIC_TAG_POLL_COUNT_NAME
                  + "`! To see the queue poll count, please create a tag with"
                  + " the name `"
                  + TWConnectorConsts.QUEUE_DIAGNOSTIC_TAG_POLL_COUNT_NAME
                  + "`.");
          Logger.LOG_EXCEPTION(e2);
        }
      }
    }
  }

  /**
   * Sets the HTTP timeouts. Note: This changes the Ewon's global HTTP timeouts and stores these
   * values in NV memory.
   */
  private static void setHttpTimeouts() {
    try {
      SCHttpUtility.setHttpTimeouts(TWConnectorConsts.HTTP_TIMEOUT_SECONDS_STRING);
    } catch (Exception e) {
      Logger.LOG_SERIOUS("Setting HTTP timeouts failed.");
      Logger.LOG_EXCEPTION(e);
    }
  }

  /**
   * Main method for the Ewon Flexy Thingworx Connector. This is the primary application entry
   * point, and will run the Thingworx Connector application.
   *
   * @param args program arguments (ignored)
   */
  public static void main(String[] args) {
    // Load configuration
    loadConfiguration();

    // Configure logger to desired log level
    configLogger();

    // Configure Ewon HTTP timeouts
    setHttpTimeouts();

    // Configure max historical fall behind mins value
    configHistoricalDataQueueMaxFallBehind();

    // Show startup message
    Logger.LOG_CRITICAL(
        "Starting " + TWConnectorConsts.CONNECTOR_NAME + " " + TWConnectorConsts.CONNECTOR_VERSION);

    // Inject local time in to the JVM
    try {
      SCTimeUtils.injectJvmLocalTime();

      final Date currentTime = new Date();
      final String currentLocalTime = SCTimeUtils.getIso8601LocalTimeFormat().format(currentTime);
      final String currentUtcTime = SCTimeUtils.getIso8601UtcTimeFormat().format(currentTime);
      Logger.LOG_DEBUG(
          "The local time zone is "
              + SCTimeUtils.getTimeZoneName()
              + " with an identifier of "
              + SCTimeUtils.getLocalTimeZoneDesignator()
              + ". The current local time is "
              + currentLocalTime
              + ", and the current UTC time is "
              + currentUtcTime
              + ".");
    } catch (Exception e) {
      Logger.LOG_CRITICAL("Unable to inject local time in to the JVM!");
      Logger.LOG_EXCEPTION(e);
    }

    // Populate tag info list
    try {
      Logger.LOG_DEBUG("Refreshing tag information list...");
      TagInfoManager.refreshTagList();
      Logger.LOG_DEBUG("Finished refreshing tag information list.");
    } catch (Exception e) {
      Logger.LOG_CRITICAL("Unable to populate array of tag information!");
      Logger.LOG_EXCEPTION(e);
    }

    // Set historical log poll size
    try {
      HistoricalDataQueueManager.setQueueFifoTimeSpanMins(
          connectorConfig.getQueueDataPollSizeMinutes());
    } catch (JSONException e) {
      Logger.LOG_CRITICAL("Unable to get the queue poll size from the configuration file!");
      Logger.LOG_EXCEPTION(e);
    }

    // Set string history enabled status
    try {
      HistoricalDataQueueManager.setStringHistoryEnabled(
          connectorConfig.getQueueDataStringEnabled());
      if (connectorConfig.getQueueDataStringEnabled()) {
        Logger.LOG_DEBUG("String history data is enabled.");
      } else {
        Logger.LOG_DEBUG("String history data is disabled.");
      }
    } catch (JSONException e) {
      Logger.LOG_CRITICAL("Unable get queue data string enabled setting!");
      Logger.LOG_EXCEPTION(e);
    }

    // Configure queue diagnostic tags (if enabled)
    configureQueueDiagnosticTags();

    // Start data send thread
    TWApiManager.startDataSendThread();

    // Set up tag update request trigger tag
    TWTagUpdateManager.setupTagUpdateTriggerTag();

    // Create tag control object for monitoring application control tag
    TagControl connectorControlTag = null;
    try {
      connectorControlTag = new TagControl(TWConnectorConsts.CONNECTOR_CONTROL_TAG_NAME);
    } catch (Exception e1) {
      Logger.LOG_INFO(
          "Unable to create tag object to track connector control tag! Attempting to create `"
              + TWConnectorConsts.CONNECTOR_CONTROL_TAG_NAME
              + "` tag.");
      Logger.LOG_EXCEPTION(e1);
      try {
        SCTagUtils.createTag(
            TWConnectorConsts.CONNECTOR_CONTROL_TAG_NAME,
            TWConnectorConsts.CONNECTOR_CONTROL_TAG_DESCRIPTION,
            TWConnectorConsts.CONNECTOR_CONTROL_TAG_IO_SERVER_NAME,
            TWConnectorConsts.CONNECTOR_CONTROL_TAG_TYPE);
        connectorControlTag = new TagControl(TWConnectorConsts.CONNECTOR_CONTROL_TAG_NAME);
      } catch (Exception e2) {
        Logger.LOG_WARN(
            "Unable to create tag `"
                + TWConnectorConsts.CONNECTOR_CONTROL_TAG_NAME
                + "`! To control this connector, create a boolean tag with the name `"
                + TWConnectorConsts.CONNECTOR_CONTROL_TAG_NAME
                + "`.");
        Logger.LOG_EXCEPTION(e2);
      }
    }

    // Run the application until stopped via application control tag
    boolean isRunning = true;
    long lastUpdateTimestampMillis = 0;
    long currentTimestampMillis;
    while (isRunning) {
      // Store current timestamp
      currentTimestampMillis = System.currentTimeMillis();

      // Update available memory variable
      availableMemoryBytes = Runtime.getRuntime().freeMemory();

      // Refresh data if within time window
      try {
        if ((currentTimestampMillis - lastUpdateTimestampMillis)
            >= connectorConfig.getQueueDataPollIntervalMillis()) {
          runGrabData();

          // Update last update time stamp
          lastUpdateTimestampMillis = currentTimestampMillis;
        }
      } catch (JSONException e) {
        Logger.LOG_WARN(
            "An error occured retrieving the queue data poll interval from the configuration"
                + " file.");
        Logger.LOG_EXCEPTION(e);
      }

      // Sleep for main loop cycle time
      try {
        Thread.sleep(TWConnectorConsts.MAIN_LOOP_CYCLE_TIME_MILLIS);
      } catch (InterruptedException e) {
        Logger.LOG_SERIOUS("Unable to sleep main loop for specified cycle time.");
        Logger.LOG_EXCEPTION(e);
      }

      // Update isRunning to match connector control tag
      if (connectorControlTag != null) {
        isRunning =
            (connectorControlTag.getTagValueAsInt()
                == TWConnectorConsts.CONNECTOR_CONTROL_TAG_RUN_VALUE);
      }
    }

    // Cleanup data send thread
    TWApiManager.setDataThreadStopFlag();

    // Show shutdown message
    Logger.LOG_CRITICAL(
        "Finished running "
            + TWConnectorConsts.CONNECTOR_NAME
            + " "
            + TWConnectorConsts.CONNECTOR_VERSION);
  }
}
