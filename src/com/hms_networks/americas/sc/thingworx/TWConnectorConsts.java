package com.hms_networks.americas.sc.thingworx;

import com.ewon.ewonitf.EWException;
import com.ewon.ewonitf.SysControlBlock;
import com.hms_networks.americas.sc.logging.Logger;
import java.text.SimpleDateFormat;

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
  public static final String CONNECTOR_VERSION = "1.2";

  // HTTP Configuration
  /**
   * The HTTP connection timeout time in seconds. This value affects the Ewon's global HTTP
   * timeouts.
   */
  public static final String HTTP_TIMEOUT_SECONDS_STRING = "2";

  // Historical Data Queue Configuration
  /**
   * The default size (in mins) of each data queue poll. Changing this will modify the amount of
   * data checked during each poll interval.
   */
  public static final long QUEUE_DATA_POLL_SIZE_MINS_DEFAULT = 1;
  /** The default interval (in milliseconds) to poll the historical data queue. */
  public static final long QUEUE_DATA_POLL_INTERVAL_MILLIS_DEFAULT = 30000;
  /** The minimum memory (in bytes) required to perform a poll of the data queue. */
  public static final int QUEUE_DATA_POLL_MIN_MEMORY_BYTES = 5000000;
  /** The time (in milliseconds) that the data queue must be behind by before warning the user. */
  public static final long QUEUE_DATA_POLL_BEHIND_MILLIS_WARN = 300000;
  /**
   * Default value of boolean flag indicating if string history data should be retrieved from the
   * queue. String history requires an additional EBD call in the underlying queue library, and may
   * take extra processing time, especially in installations with large string tag counts.
   */
  public static final boolean QUEUE_DATA_STRING_HISTORY_ENABLED_DEFAULT = false;

  // Main Loop Configuration
  /**
   * The interval (in milliseconds) at which the main loop checks available memory and polls the
   * historical data queue if necessary. This value should be lesser than {@link
   * #QUEUE_DATA_POLL_INTERVAL_MILLIS_DEFAULT}.
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
  /** The configuration file JSON key for the Thingworx base URL. */
  public static final String CONNECTOR_CONFIG_TW_URL_KEY = "ThingworxFullUrl";
  /** The configuration file JSON key for the Thingworx app key. */
  public static final String CONNECTOR_CONFIG_APP_KEY_KEY = "AppKey";
  /** The configuration file JSON key for the log level of the connector. */
  public static final String CONNECTOR_CONFIG_LOG_LEVEL_KEY = "LogLevel";
  /** The configuration file JSON key for the enable queue string history setting. */
  public static final String CONNECTOR_CONFIG_QUEUE_STING_HISTORY_KEY = "QueueEnableStringHistory";
  /** The configuration file JSON key for the queue data poll size in minutes. */
  public static final String CONNECTOR_CONFIG_QUEUE_DATA_POLL_SIZE_MINS_KEY =
      "QueueDataPollSizeMins";
  /** The configuration file JSON key for the queue data poll interval in milliseconds. */
  public static final String CONNECTOR_CONFIG_QUEUE_DATA_POLL_INTERVAL_MILLIS_KEY =
      "QueueDataPollIntervalMillis";
  /**
   * Username for accessing Ewon Flexy via FTP. This user must be configured on the Ewon Flexy, and
   * must allow FTP access.
   */
  public static final String CONNECTOR_CONFIG_FTP_USERNAME_KEY = "FtpUsername";
  /** Password for accessing Ewon Flexy via FTP. */
  public static final String CONNECTOR_CONFIG_FTP_PASSWORD_KEY = "FtpPassword";

  // Config Defaults
  /** The default value for the Thingworx URL. This is a helpful hint for customers. */
  public static final String CONNECTOR_CONFIG_DEFAULT_TW_URL =
      "https://setme.biz/Thingworx/Things/ConnectorHost/Services/TakeInfo";
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

  // Time Constants
  /** The number of milliseconds in one second. */
  public static final int NUM_MILLISECONDS_PER_SECOND = 1000;

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
  /** Date format for time stamps sent to the Thingworx API. */
  public static final SimpleDateFormat THINGWORX_API_DATE_FORMAT =
      new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

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
