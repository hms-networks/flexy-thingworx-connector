package com.hms_networks.americas.sc.thingworx;

import com.ewon.ewonitf.SysControlBlock;
import com.ewon.ewonitf.TagControl;
import com.hms_networks.americas.sc.config.exceptions.ConfigFileException;
import com.hms_networks.americas.sc.config.exceptions.ConfigFileWriteException;
import com.hms_networks.americas.sc.datapoint.DataPoint;
import com.hms_networks.americas.sc.historicaldata.HistoricalDataQueueManager;
import com.hms_networks.americas.sc.json.JSONException;
import com.hms_networks.americas.sc.logging.Logger;
import com.hms_networks.americas.sc.taginfo.TagInfo;
import com.hms_networks.americas.sc.taginfo.TagInfoManager;

import com.hms_networks.americas.sc.taginfo.TagType;
import com.hms_networks.americas.sc.thingworx.config.TWConnectorConfig;
import com.hms_networks.americas.sc.thingworx.utils.TWPropertyType;
import com.hms_networks.americas.sc.thingworx.utils.TWTimeOffsetCalculator;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Ewon Flexy Azure Connector main class.
 *
 * @author HMS Networks, MU Americas Solution Center
 * @since 1.0
 */
public class TWConnectorMain {

  /** Connector configuration object */
  private static TWConnectorConfig connectorConfig;

  /** Current available memory in bytes */
  private static long availableMemoryBytes;

  /** Boolean flag indicating if the application is running out of memory */
  private static boolean isMemoryCurrentlyLow;

  /**
   * Boolean flag tracking if the device mode warning has been shown in device modes that disable
   * sending data.
   */
  private static boolean isModeMessageShown;

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

      try {
        // Read data points from queue
        ArrayList datapontsReadFromQueue;
        if (HistoricalDataQueueManager.doesTimeTrackerExist()) {
          final boolean startNewTimeTracker = false;
          datapontsReadFromQueue =
              HistoricalDataQueueManager.getFifoNextSpanDataAllGroups(startNewTimeTracker);
        } else {
          final boolean startNewTimeTracker = true;
          datapontsReadFromQueue =
              HistoricalDataQueueManager.getFifoNextSpanDataAllGroups(startNewTimeTracker);
        }

        Logger.LOG_DEBUG(
            "Read " + datapontsReadFromQueue.size() + " data points from the historical log.");

        // Send data points to Thingworx
        for (int x = 0; x < datapontsReadFromQueue.size(); x++) {
          TWApiManager.pushDataPointToThingworx((DataPoint) datapontsReadFromQueue.get(x));
        }

        // Check if queue is behind
        try {
          long queueBehindMillis =
              HistoricalDataQueueManager.getCurrentTimeWithOffset()
                  - HistoricalDataQueueManager.getCurrentTimeTrackerValue();
          if (queueBehindMillis >= TWConnectorConsts.QUEUE_DATA_POLL_BEHIND_MILLIS_WARN) {
            Logger.LOG_WARN(
                "The historical data queue is running behind by "
                    + queueBehindMillis
                    + " milliseconds.");
          }
        } catch (IOException e) {
          Logger.LOG_SERIOUS("Unable to detect if historical data queue is running behind.");
          Logger.LOG_EXCEPTION(e);
        }

      } catch (Exception e) {
        Logger.LOG_CRITICAL("An error occurred while reading data from the historical log.");
        Logger.LOG_EXCEPTION(e);
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

  /** Prepares Thingworx for connection with the Ewon Flexy. */
  private static void prepareThingworxConnection() {
    // Create device entry in Thingworx
    try {
      Logger.LOG_INFO("Creating device entry in Thingworx.");
      boolean createdDeviceEntry = TWApiManager.createDeviceEntry();
      if (!createdDeviceEntry) {
        Logger.LOG_CRITICAL("A device entry was not created in Thingworx.");
      }
    } catch (Exception e) {
      Logger.LOG_CRITICAL("An error occurred while creating the device entry in Thingworx.");
      Logger.LOG_EXCEPTION(e);
    }

    // Enable device in Thingworx
    try {
      Logger.LOG_INFO("Enabling this device in Thingworx.");
      boolean enabledThing = TWApiManager.enableThing();
      if (!enabledThing) {
        Logger.LOG_CRITICAL("This device was not enabled in Thingworx.");
      }
    } catch (Exception e) {
      Logger.LOG_CRITICAL("An error occurred while enabling this device in Thingworx.");
      Logger.LOG_EXCEPTION(e);
    }

    // Restart device in Thingworx
    try {
      Logger.LOG_INFO("Restarting this device in Thingworx.");
      boolean restartedThing = TWApiManager.restartThing();
      if (!restartedThing) {
        Logger.LOG_CRITICAL("This device was not restarted in Thingworx.");
      }
    } catch (Exception e) {
      Logger.LOG_CRITICAL("An error occurred while restarting this device in Thingworx.");
      Logger.LOG_EXCEPTION(e);
    }

    // Register tags in Thingworx
    List tagInfoList = TagInfoManager.getTagInfoList();
    for (int x = 0; x < tagInfoList.size(); x++) {
      TagInfo currentTagInfo = (TagInfo) tagInfoList.get(x);
      TWPropertyType currentTagPropertyType;
      if (currentTagInfo.getType() == TagType.STRING) {
        currentTagPropertyType = TWPropertyType.STRING;
      } else if (currentTagInfo.getType() == TagType.BOOLEAN) {
        currentTagPropertyType = TWPropertyType.BOOLEAN;
      } else if (currentTagInfo.getType() == TagType.INTEGER) {
        currentTagPropertyType = TWPropertyType.INTEGER;
      } else {
        currentTagPropertyType = TWPropertyType.NUMBER;
      }
      try {
        TWApiManager.addPropertyToThing(currentTagInfo.getName(), currentTagPropertyType);
      } catch (Exception e) {
        Logger.LOG_CRITICAL(
            "Unable to add the property \""
                + currentTagInfo.getName()
                + "\" to this device in Thingworx.");
      }
    }
  }

  /**
   * Sets the http timeouts. Note: This changes the eWON's global HTTP timeouts and stores these
   * values in NV memory.
   */
  private static void setHttpTimeouts() {
    SysControlBlock SCB;

    final String httpSendTimeoutName = "HTTPC_SDTO";
    final String httpReadTimeoutName = "HTTPC_RDTO";

    boolean needsSave = false;
    try {
      SCB = new SysControlBlock(SysControlBlock.SYS);

      if (!SCB.getItem(httpSendTimeoutName).equals(TWConnectorConsts.HTTP_TIMEOUT_SECONDS_STRING)) {
        SCB.setItem(httpSendTimeoutName, TWConnectorConsts.HTTP_TIMEOUT_SECONDS_STRING);
        needsSave = true;
      }

      if (!SCB.getItem(httpReadTimeoutName).equals(TWConnectorConsts.HTTP_TIMEOUT_SECONDS_STRING)) {
        SCB.setItem(httpReadTimeoutName, TWConnectorConsts.HTTP_TIMEOUT_SECONDS_STRING);
        needsSave = true;
      }

      // Only save the block if the value has changed, this reduces wear on the flash memory.
      if (needsSave) {
        SCB.saveBlock(true);
      }

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

    // Show startup message
    Logger.LOG_CRITICAL(
        "Starting " + TWConnectorConsts.CONNECTOR_NAME + " " + TWConnectorConsts.CONNECTOR_VERSION);

    // Calculate local time offset and configure queue
    TWTimeOffsetCalculator.calculateTimeOffsetMilliseconds();
    final long calculatedTimeOffsetMilliseconds =
        TWTimeOffsetCalculator.getTimeOffsetMilliseconds();
    HistoricalDataQueueManager.setLocalTimeOffset(calculatedTimeOffsetMilliseconds);
    Logger.LOG_DEBUG(
        "The local time offset is " + calculatedTimeOffsetMilliseconds + " milliseconds.");

    // Populate tag info list
    try {
      Logger.LOG_DEBUG("Refreshing tag information list...");
      TagInfoManager.refreshTagList();
      Logger.LOG_DEBUG("Finished refreshing tag information list.");
    } catch (IOException e) {
      Logger.LOG_CRITICAL("Unable to populate array of tag information!");
      Logger.LOG_EXCEPTION(e);
    }

    // Prepare for connection to Thingworx
    prepareThingworxConnection();

    // Set historical log poll size
    HistoricalDataQueueManager.setQueueFifoTimeSpanMins(
        TWConnectorConsts.QUEUE_DATA_POLL_SIZE_MINS);

    // Set string history enabled status
    HistoricalDataQueueManager.setStringHistoryEnabled(
        TWConnectorConsts.QUEUE_DATA_STRING_HISTORY_ENABLED);
    if (TWConnectorConsts.QUEUE_DATA_STRING_HISTORY_ENABLED) {
      Logger.LOG_DEBUG("String history data is enabled.");
    } else {
      Logger.LOG_DEBUG("String history data is disabled.");
    }

    // Create tag control object for monitoring application control tag
    TagControl connectorControlTag = null;
    try {
      connectorControlTag = new TagControl(TWConnectorConsts.CONNECTOR_CONTROL_TAG_NAME);
    } catch (Exception e) {
      Logger.LOG_SERIOUS("Unable to create tag object to track connector control tag!");
      Logger.LOG_EXCEPTION(e);
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
      if ((currentTimestampMillis - lastUpdateTimestampMillis)
          >= TWConnectorConsts.QUEUE_DATA_POLL_INTERVAL_MILLIS) {
        runGrabData();

        // Update last update time stamp
        lastUpdateTimestampMillis = currentTimestampMillis;
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

    // Show shutdown message
    Logger.LOG_CRITICAL(
        "Finished running "
            + TWConnectorConsts.CONNECTOR_NAME
            + " "
            + TWConnectorConsts.CONNECTOR_VERSION);
  }
}
