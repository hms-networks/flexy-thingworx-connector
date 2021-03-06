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
   * Get the configured Thingworx IP address from the configuration.
   *
   * @return Thingworx URL
   * @throws JSONException if unable to get Thingworx IP address from configuration
   */
  public String getThingworxIPAddress() throws JSONException {
    String twUrl = configurationObject.getString(TWConnectorConsts.CONNECTOR_CONFIG_TW_URL_KEY);
    if (twUrl.equals(TWConnectorConsts.CONNECTOR_CONFIG_DEFAULT_TW_URL)) {
      Logger.LOG_WARN(
          "The configured Thingworx Base URL is the default value. "
              + "Please change this value to the URL of your Thingworx Foundation instance.");
    }
    return twUrl;
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
