package com.hms_networks.americas.sc.thingworx.config;

import com.hms_networks.americas.sc.extensions.config.ConfigFile;
import com.hms_networks.americas.sc.extensions.json.JSONException;
import com.hms_networks.americas.sc.extensions.json.JSONObject;
import com.hms_networks.americas.sc.extensions.logging.Logger;
import com.hms_networks.americas.sc.thingworx.TWConnectorConsts;

/**
 * Configuration class containing configuration fields for the Ewon Thingworx Connector.
 *
 * @author HMS Networks, MU Americas Solution Center
 * @since 1.0
 */
public class TWConnectorConfig extends ConfigFile {

  /**
   * Boolean flag used to indicate if the default max data points per payload value message has been
   * shown.
   */
  private boolean maxDataPointsPayloadDefaultMessageShown = false;

  /**
   * Get the configured connector log level from the configuration.
   *
   * @return connector log level
   * @throws JSONException if unable to get connector log level from configuration
   */
  public int getConnectorLogLevel() throws JSONException {
    return configurationObject.getInt(TWConnectorConsts.CONNECTOR_CONFIG_LOG_LEVEL_KEY);
  }

  /**
   * Get the full URL of the Thingworx endpoint from the configuration.
   *
   * @return Thingworx full URL
   * @throws JSONException if unable to get Thingworx URL from configuration
   */
  public String getThingworxFullUrl() throws JSONException {
    String twUrl = configurationObject.getString(TWConnectorConsts.CONNECTOR_CONFIG_TW_URL_KEY);
    if (twUrl.equals(TWConnectorConsts.CONNECTOR_CONFIG_DEFAULT_TW_URL)) {
      Logger.LOG_WARN(
          "The configured Thingworx Base URL is the default value. "
              + "Please change this value to the URL of your Thingworx Foundation instance.");
    }
    return twUrl;
  }

  /**
   * Get the queue data string enabled setting from the configuration.
   *
   * @return queue data string enabled setting
   * @throws JSONException if unable to get queue data string enabled setting from configuration
   */
  public boolean getQueueDataStringEnabled() throws JSONException {
    boolean queueDataStringEnabled;
    if (configurationObject.has(TWConnectorConsts.CONNECTOR_CONFIG_QUEUE_STRING_HISTORY_KEY)) {
      queueDataStringEnabled =
          configurationObject.getBoolean(
              TWConnectorConsts.CONNECTOR_CONFIG_QUEUE_STRING_HISTORY_KEY);
    } else {
      String defaultStringEnabledStr =
          String.valueOf(TWConnectorConsts.QUEUE_DATA_STRING_HISTORY_ENABLED_DEFAULT);
      Logger.LOG_WARN(
          "The queue data string enabled setting was not set. Using default value of "
              + defaultStringEnabledStr
              + ".");
      queueDataStringEnabled = TWConnectorConsts.QUEUE_DATA_STRING_HISTORY_ENABLED_DEFAULT;
    }

    return queueDataStringEnabled;
  }

  /**
   * Get the queue diagnostic tags enabled setting from the configuration.
   *
   * @return queue diagnostic tags enabled setting
   */
  public boolean getQueueDiagnosticTagsEnabled() {
    boolean queueDiagnosticTagsEnabled;
    if (configurationObject.has(TWConnectorConsts.CONNECTOR_CONFIG_QUEUE_DIAGNOSTIC_TAGS_KEY)) {
      try {
        queueDiagnosticTagsEnabled =
            configurationObject.getBoolean(
                TWConnectorConsts.CONNECTOR_CONFIG_QUEUE_DIAGNOSTIC_TAGS_KEY);
      } catch (JSONException e) {
        queueDiagnosticTagsEnabled =
            TWConnectorConsts.CONNECTOR_CONFIG_DEFAULT_QUEUE_DIAGNOSTIC_TAGS;
        Logger.LOG_WARN(
            "The queue diagnostic tags enabled setting could not be read from the configuration"
                + " file. Using default value of "
                + TWConnectorConsts.CONNECTOR_CONFIG_DEFAULT_QUEUE_DIAGNOSTIC_TAGS
                + ".");
        Logger.LOG_EXCEPTION(e);
      }
    } else {
      queueDiagnosticTagsEnabled = TWConnectorConsts.CONNECTOR_CONFIG_DEFAULT_QUEUE_DIAGNOSTIC_TAGS;
    }

    return queueDiagnosticTagsEnabled;
  }

  /**
   * Get the queue data poll size in minutes from the configuration.
   *
   * @return queue data poll size in minutes
   * @throws JSONException if unable to get queue data string enabled setting from configuration
   */
  public long getQueueDataPollSizeMinutes() throws JSONException {
    long queueDataStringEnabled;
    if (configurationObject.has(TWConnectorConsts.CONNECTOR_CONFIG_QUEUE_DATA_POLL_SIZE_MINS_KEY)) {
      queueDataStringEnabled =
          configurationObject.getLong(
              TWConnectorConsts.CONNECTOR_CONFIG_QUEUE_DATA_POLL_SIZE_MINS_KEY);
    } else {
      String defaultPollSizeStr =
          String.valueOf(TWConnectorConsts.QUEUE_DATA_POLL_SIZE_MINS_DEFAULT);
      Logger.LOG_WARN(
          "The queue data poll size setting was not set. Using default value of "
              + defaultPollSizeStr
              + " minutes.");
      queueDataStringEnabled = TWConnectorConsts.QUEUE_DATA_POLL_SIZE_MINS_DEFAULT;
    }

    return queueDataStringEnabled;
  }

  /**
   * Get the queue data poll interval in milliseconds from the configuration.
   *
   * @return queue data poll interval in milliseconds
   * @throws JSONException if unable to get queue data poll interval from the configuration file
   */
  public long getQueueDataPollIntervalMillis() throws JSONException {
    long queueDataPollIntervalMillis;
    if (configurationObject.has(
        TWConnectorConsts.CONNECTOR_CONFIG_QUEUE_DATA_POLL_INTERVAL_MILLIS_KEY)) {
      queueDataPollIntervalMillis =
          configurationObject.getLong(
              TWConnectorConsts.CONNECTOR_CONFIG_QUEUE_DATA_POLL_INTERVAL_MILLIS_KEY);
    } else {
      String defaultPollInterval =
          String.valueOf(TWConnectorConsts.QUEUE_DATA_POLL_INTERVAL_MILLIS_DEFAULT);
      Logger.LOG_WARN(
          "The queue data poll size setting was not set. Using default value of "
              + defaultPollInterval
              + " Milliseconds.");
      queueDataPollIntervalMillis = TWConnectorConsts.QUEUE_DATA_POLL_INTERVAL_MILLIS_DEFAULT;
    }

    return queueDataPollIntervalMillis;
  }

  /**
   * Get the configured Thingworx app key from the configuration.
   *
   * @return Thingworx app key
   * @throws JSONException if unable to get Thingworx app key from configuration
   */
  public String getThingworxAppKey() throws JSONException {
    String twAppKey = configurationObject.getString(TWConnectorConsts.CONNECTOR_CONFIG_APP_KEY_KEY);
    if (twAppKey.equals(TWConnectorConsts.CONNECTOR_CONFIG_DEFAULT_APP_KEY)) {
      Logger.LOG_WARN(
          "The Thingworx app key has not been configured. Please modify the configuration"
              + "to include a Thingworx app key with all run time permissions enabled.");
    }
    return twAppKey;
  }

  /**
   * Get the number of maximum data points in a payload from the configuration.
   *
   * @return maximum data points in a payload
   * @throws JSONException if unable to parse maximum payload data points field from the
   *     configuration file
   */
  public int getPayloadMaxDataPoints() throws JSONException {
    int payloadMaxDataPoints;
    if (configurationObject.has(TWConnectorConsts.CONNECTOR_CONFIG_PAYLOAD_MAX_DATA_POINTS_KEY)) {
      payloadMaxDataPoints =
          configurationObject.getInt(
              TWConnectorConsts.CONNECTOR_CONFIG_PAYLOAD_MAX_DATA_POINTS_KEY);
    } else {
      payloadMaxDataPoints = TWConnectorConsts.CONNECTOR_CONFIG_DEFAULT_PAYLOAD_MAX_DATA_POINTS;
      if (!maxDataPointsPayloadDefaultMessageShown) {
        Logger.LOG_WARN(
            "The maximum data points in a payload setting was not configured. Using default value"
                + " of "
                + payloadMaxDataPoints
                + ".");
        maxDataPointsPayloadDefaultMessageShown = true;
      }
    }

    return payloadMaxDataPoints;
  }

  /**
   * Get the interval at which completed data payloads are sent to Thingworx (in milliseconds).
   *
   * @return completed data payload send interval (in milliseconds)
   * @throws JSONException if unable to parse completed data payload send interval (in milliseconds)
   *     field from the configuration file
   */
  public long getDataPayloadSendIntervalMillis() throws JSONException {
    long dataPayloadSendIntervalMillis;
    if (configurationObject.has(TWConnectorConsts.CONNECTOR_CONFIG_PAYLOAD_MAX_DATA_POINTS_KEY)) {
      dataPayloadSendIntervalMillis =
          configurationObject.getLong(
              TWConnectorConsts.CONNECTOR_CONFIG_PAYLOAD_SEND_INTERVAL_MILLIS_KEY);
    } else {
      dataPayloadSendIntervalMillis =
          TWConnectorConsts.CONNECTOR_CONFIG_DEFAULT_PAYLOAD_SEND_INTERVAL_MILLIS;
      Logger.LOG_WARN(
          "The data payload send interval (in milliseconds) was not configured. Using default"
              + " value of "
              + dataPayloadSendIntervalMillis
              + ".");
    }

    return dataPayloadSendIntervalMillis;
  }

  /**
   * Get the full URL of the Thingworx tag update endpoint from the configuration.
   *
   * @return Thingworx tag update full URL
   * @throws JSONException if unable to get Thingworx tag update URL from configuration
   */
  public String getThingworxTagUpdateFullUrl() throws JSONException {
    String thingworxTagUpdateFullUrl;
    if (configurationObject.has(TWConnectorConsts.CONNECTOR_CONFIG_TW_TAG_UPDATE_URL_KEY)) {
      thingworxTagUpdateFullUrl =
          configurationObject.getString(TWConnectorConsts.CONNECTOR_CONFIG_TW_TAG_UPDATE_URL_KEY);
    } else {
      Logger.LOG_INFO(
          "The Thingworx tag update URL has not been configured. This URL must be set "
              + "before remote tag update checks can be triggered!");
      thingworxTagUpdateFullUrl = "";
    }

    return thingworxTagUpdateFullUrl;
  }

  /**
   * Saves the configuration to the file system and catches any exceptions generated while saving.
   */
  void trySave() {
    try {
      save();
      Logger.LOG_DEBUG("Saved application configuration changes to file.");
    } catch (Exception e) {
      Logger.LOG_SERIOUS("Unable to save application configuration to file.");
      Logger.LOG_EXCEPTION(e);
    }
  }

  /**
   * Gets the file path for reading and saving the configuration to disk.
   *
   * @return configuration file path
   */
  public String getConfigFilePath() {
    return TWConnectorConsts.CONNECTOR_CONFIG_FOLDER
        + "/"
        + TWConnectorConsts.CONNECTOR_CONFIG_FILE_NAME;
  }

  /**
   * Gets the indent factor used when saving the configuration to file.
   *
   * @return JSON file indent factor
   */
  public int getJSONIndentFactor() {
    return TWConnectorConsts.CONNECTOR_CONFIG_JSON_INDENT_FACTOR;
  }

  /**
   * Creates a configuration JSON object containing fields and their default values.
   *
   * @return configuration object with defaults
   */
  public JSONObject getDefaultConfigurationObject() throws JSONException {
    JSONObject defaultConfigObject = new JSONObject();
    defaultConfigObject.put(
        TWConnectorConsts.CONNECTOR_CONFIG_LOG_LEVEL_KEY,
        TWConnectorConsts.CONNECTOR_CONFIG_DEFAULT_LOG_LEVEL);
    defaultConfigObject.put(
        TWConnectorConsts.CONNECTOR_CONFIG_APP_KEY_KEY,
        TWConnectorConsts.CONNECTOR_CONFIG_DEFAULT_APP_KEY);
    defaultConfigObject.put(
        TWConnectorConsts.CONNECTOR_CONFIG_TW_URL_KEY,
        TWConnectorConsts.CONNECTOR_CONFIG_DEFAULT_TW_URL);
    return defaultConfigObject;
  }
}
