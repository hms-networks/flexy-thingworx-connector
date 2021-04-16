package com.hms_networks.americas.sc.thingworx.config;

import com.hms_networks.americas.sc.config.ConfigFile;
import com.hms_networks.americas.sc.json.JSONException;
import com.hms_networks.americas.sc.json.JSONObject;
import com.hms_networks.americas.sc.logging.Logger;
import com.hms_networks.americas.sc.thingworx.TWConnectorConsts;

/**
 * Configuration class containing configuration fields for the Ewon Thingworx Connector.
 *
 * @author HMS Networks, MU Americas Solution Center
 * @since 1.0
 */
public class TWConnectorConfig extends ConfigFile {

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
    if (configurationObject.has(TWConnectorConsts.CONNECTOR_CONFIG_QUEUE_STING_HISTORY_KEY)) {
      queueDataStringEnabled =
          configurationObject.getBoolean(
              TWConnectorConsts.CONNECTOR_CONFIG_QUEUE_STING_HISTORY_KEY);
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
   * Get the FTP user from the configuration.
   *
   * @return FTP user
   * @throws JSONException if unable to get the FTP user from the configuration file
   */
  public String getFtpUser() throws JSONException {
    String ftpUser;
    if (configurationObject.has(TWConnectorConsts.CONNECTOR_CONFIG_FTP_USERNAME_KEY)) {
      ftpUser = configurationObject.getString(TWConnectorConsts.CONNECTOR_CONFIG_FTP_USERNAME_KEY);
    } else {
      Logger.LOG_WARN(
          "The FTP user was not set. The Ewon must be set to UTC time if no FTP user is"
              + " configured.");
      ftpUser = "";
    }

    return ftpUser;
  }

  /**
   * Get the FTP password from the configuration.
   *
   * @return FTP password
   * @throws JSONException if unable to get the FTP password from the configuration file
   */
  public String getFtpPassword() throws JSONException {
    String ftpPassword;
    if (configurationObject.has(TWConnectorConsts.CONNECTOR_CONFIG_FTP_PASSWORD_KEY)) {
      ftpPassword =
          configurationObject.getString(TWConnectorConsts.CONNECTOR_CONFIG_FTP_PASSWORD_KEY);
    } else {
      Logger.LOG_WARN(
          "The FTP password was not set. The Ewon must be set to UTC time if no FTP user is"
              + " configured.");
      ftpPassword = "";
    }

    return ftpPassword;
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
