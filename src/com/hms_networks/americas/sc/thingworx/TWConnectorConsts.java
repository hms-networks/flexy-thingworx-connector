package com.hms_networks.americas.sc.thingworx;

import com.ewon.ewonitf.EWException;
import com.ewon.ewonitf.SysControlBlock;
import com.hms_networks.americas.sc.logging.Logger;

/**
 * Class of constants for the Ewon Thingworx Connector.
 *
 * @author HMS Networks, MU Americas Solution Center
 * @since 1.0
 */
public class TWConnectorConsts {
  // Connector Information
  /** Name of the connector application */
  public static final String CONNECTOR_NAME = "Ewon Thingworx Connector";

  /** Version of the connector application */
  public static final String CONNECTOR_VERSION = "1.0";

  // HTTP Configuration
  /**
   * The HTTP connection timeout time in seconds. This value affects the Ewon's global HTTP
   * timeouts.
   */
  public static final String HTTP_TIMEOUT_SECONDS_STRING = "2";

  // Historical Data Queue Configuration
  /**
   * The size (in mins) of each data queue poll. Changing this will modify the amount of data
   * checked during each poll interval.
   */
  public static final long QUEUE_DATA_POLL_SIZE_MINS = 3;
  /** The interval (in milliseconds) to poll the historical data queue. */
  public static final long QUEUE_DATA_POLL_INTERVAL_MILLIS = 10000;
  /** The minimum memory (in bytes) required to perform a poll of the data queue. */
  public static final int QUEUE_DATA_POLL_MIN_MEMORY_BYTES = 5000000;
  /** The time (in milliseconds) that the data queue must be behind by before warning the user. */
  public static final long QUEUE_DATA_POLL_BEHIND_MILLIS_WARN = 300000;
  /**
   * Boolean flag indicating if string history data should be retrieved from the queue. String
   * history requires an additional EBD call in the underlying queue library, and may take extra
   * processing time, especially in installations with large string tag counts.
   */
  public static final boolean QUEUE_DATA_STRING_HISTORY_ENABLED = true;

  // Main Loop Configuration
  /**
   * The interval (in milliseconds) at which the main loop checks available memory and polls the
   * historical data queue if necessary. This value should be lesser than {@link
   * #QUEUE_DATA_POLL_INTERVAL_MILLIS}.
   */
  public static final long MAIN_LOOP_CYCLE_TIME_MILLIS = 1000;

  // Connector Control Tag
  /** The name of the tag that is used to control the connector execution. */
  public static final String CONNECTOR_CONTROL_TAG_NAME = "ThingworxControl";
  /** The value of the tag that permits the application to execute. */
  public static final int CONNECTOR_CONTROL_TAG_RUN_VALUE = 0;

  // Configuration config
  /** The folder for storing configuration files and data. */
  public static final String CONNECTOR_CONFIG_FOLDER = "/usr";
  /** The name of the connector configuration file. */
  public static final String CONNECTOR_CONFIG_FILE_NAME = "ThingworxConnectorConfig.json";
  /** The indent factor used in the JSON configuration file. */
  public static final int CONNECTOR_CONFIG_JSON_INDENT_FACTOR = 3;

  // Config Keys
  /** The configuration file JSON key for the Thingworx IP address. */
  public static final String CONNECTOR_CONFIG_TW_IP_KEY = "ThingworxIP";
  /** The configuration file JSON key for the Thingworx app key. */
  public static final String CONNECTOR_CONFIG_APP_KEY_KEY = "AppKey";
  /** The configuration file JSON key for the log level of the connector. */
  public static final String CONNECTOR_CONFIG_LOG_LEVEL_KEY = "LogLevel";

  // Config Defaults
  /**
   * The default value for the Thingworx IP address in the configuration file. Note: This default
   * value is an IP address within a test-net IPv4 address range and should not be present on any
   * standard network.
   */
  public static final String CONNECTOR_CONFIG_DEFAULT_TW_IP_KEY = "203.0.113.1";
  /** The default value for the app key in the configuration file. */
  public static final String CONNECTOR_CONFIG_DEFAULT_APP_KEY = "Set-This-Value";
  /** The default value for the log level in the configuration file. */
  public static final int CONNECTOR_CONFIG_DEFAULT_LOG_LEVEL = 1;

  // RequestHTTPX Response Codes
  /** Ewon RequestHTTPX response code for no error. */
  public static final int HTTPX_CODE_NO_ERROR = 0;
  /** Ewon RequestHTTPX response code for an Ewon error. */
  public static final int HTTPX_CODE_EWON_ERROR = 1;
  /** Ewon RequestHTTPX response code for an authentication error. */
  public static final int HTTPX_CODE_AUTH_ERROR = 2;
  /** Ewon RequestHTTPX response code for a connection error. */
  public static final int HTTPX_CODE_CONNECTION_ERROR = 32601;

  // HTTP Connection Method Constants
  /** The string value used to identify HTTP POST requests. */
  public static final String HTTP_POST_STRING = "POST";
  /** The string value used to identify HTTP GET requests. */
  public static final String HTTP_GET_STRING = "GET";
  /** The string value used to identify HTTP PUT requests. */
  public static final String HTTP_PUT_STRING = "PUT";

  // Var Lst Constants
  /** String to replace in {@link #VAR_LST_ADD_TAG_TEMPLATE} with the tag name. */
  public static final String VAR_LST_ADD_TAG_NAME_REPLACE = "%NAME%";
  /** String to replace in {@link #VAR_LST_ADD_TAG_TEMPLATE} with the tag type. */
  public static final String VAR_LST_ADD_TAG_TYPE_REPLACE = "%TYPE%";
  /** Template for the body of a var_lst.txt file that adds one tag to the Ewon. */
  public static final String VAR_LST_ADD_TAG_TEMPLATE =
      "Name;ServerName;Type\r\n"
          + VAR_LST_ADD_TAG_NAME_REPLACE
          + ";MEM;"
          + VAR_LST_ADD_TAG_TYPE_REPLACE
          + "\r\n";
  /** The value of `type` for boolean tags in the Ewon var_lst.txt file. */
  public static final String VAR_LST_BOOLEAN_TAG_TYPE_VALUE = "0";
  /** The path of the var_lst.txt file on the Ewon file system. */
  public static final String VAR_LST_FILE_PATH = "/var_lst.txt";

  // FTP Constants
  /**
   * Username for accessing Ewon Flexy via FTP. This user must be configured on the Ewon Flexy, and
   * must allow FTP access.
   */
  public static final String FTP_USERNAME = "FtpUser";
  /** Password for accessing Ewon Flexy via FTP. */
  public static final String FTP_PASSWORD = "FtpPassword";

  // Time Offset Constants
  /** Date format used when parsing a local time offset file. */
  public static final String TIME_OFFSET_DATE_FORMAT = "dd/MM/yyyy HH:mm:ss";
  /** Folder on Ewon Flexy file system for storing local time offset files. */
  public static final String TIME_OFFSET_FOLDER = CONNECTOR_CONFIG_FOLDER + "/TimeOffset";
  /** File name of HTML file used to generate a local time offset file. */
  public static final String TIME_OFFSET_HTML_FILE_NAME = TIME_OFFSET_FOLDER + "/localTime.shtm";
  /** File name of generated local time offset file. */
  public static final String TIME_OFFSET_RESPONSE_FILE_NAME =
      TIME_OFFSET_FOLDER + "/localTimeResponse.txt";

  // Thingworx API Constants
  /** REST API endpoint for creating a device entry. */
  public static final String CREATE_THING_ENDPOINT =
      "/Thingworx/Resources/EntityServices/Services/CreateThing";
  /** REST API folder containing endpoints for Thingworx things. */
  public static final String THINGS_ENDPOINT_DIR = "/Thingworx/Things/";
  /** REST API endpoint for enabling a thing. */
  public static final String ENABLE_THING_ENDPOINT = "/Services/EnableThing";
  /** REST API endpoint for restarting a thing. */
  public static final String RESTART_THING_ENDPOINT = "/Services/RestartThing";
  /** REST API endpoint for adding a property definition. */
  public static final String ADD_PROPERTY_ENDPOINT = "/Services/AddPropertyDefinition";
  /** REST API folder containing endpoints for Thingworx thing properties. */
  public static final String PROPERTIES_ENDPOINT_DIRECTORY = "/Properties/";
  /** Template to use when registering a device entry to Thingworx. */
  public static final String THING_TEMPLATE = "GenericThing";
  /** Suffix to append to the property containing the timestamp of the corresponding property. */
  public static final String PROPERTY_NAME_TIMESTAMP_SUFFIX = "_Timestamp";

  // SysControlBlock Constants
  /** SysControlBlock serial number key. */
  public static final String SCB_SERIAL_NUMBER_KEY = "SERNUM";

  // Ewon Serial Number
  /**
   * Ewon serial number constant. 'invalid' is a placeholder that is populated with the actual
   * serial number at runtime.
   */
  public static String EWON_SERIAL_NUMBER = "invalid";

  // Populate Ewon Serial Number
  static {
    try {
      EWON_SERIAL_NUMBER = new SysControlBlock(SysControlBlock.INF).getItem(SCB_SERIAL_NUMBER_KEY);
    } catch (EWException e) {
      Logger.LOG_SERIOUS("An error occurred while detecting this Ewon's serial number!");
      Logger.LOG_EXCEPTION(e);
    }
  }
}
