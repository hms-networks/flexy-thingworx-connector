package com.hms_networks.americas.sc.thingworx;

import com.ewon.ewonitf.EWException;
import com.hms_networks.americas.sc.datapoint.DataPoint;
import com.hms_networks.americas.sc.json.JSONException;
import com.hms_networks.americas.sc.logging.Logger;
import com.hms_networks.americas.sc.thingworx.utils.TWHttpUtil;
import com.hms_networks.americas.sc.thingworx.utils.TWPropertyType;
import java.io.IOException;
import java.util.Date;

/**
 * Class for managing HTTP API calls to the Thingworx API.
 *
 * @since 1.0
 * @author HMS Networks, MU Americas Solution Center
 */
public class TWApiManager {

  /**
   * Gets the name of the Ewon Flexy as it appears/should appear in Thingworx.
   *
   * @return Thingworx device name
   */
  public static String getApiDeviceName() {
    return "FLEXY-" + TWConnectorConsts.EWON_SERIAL_NUMBER;
  }

  /**
   * Creates a device entry for the current Ewon device in the Thingworx server configured in the
   * application.
   *
   * @return true if device entry created
   * @throws JSONException if unable to get configuration information
   * @throws IOException if unable to HTTP connect to Thingworx
   * @throws EWException if unable to HTTP connect to Thingworx
   */
  public static boolean createDeviceEntry() throws JSONException, IOException, EWException {
    // Build full POST request URL
    final String createThingEndpointFullUrl =
        TWConnectorMain.getConnectorConfig().getThingworxIPAddress()
            + TWConnectorConsts.CREATE_THING_ENDPOINT;
    final String createThingRequestHeader =
        "Content-Type=application/json&appKey="
            + TWConnectorMain.getConnectorConfig().getThingworxAppKey();

    // Build POST request body
    final String createThingRequestBody =
        "{\"name\": \""
            + getApiDeviceName()
            + "\",\"thingTemplateName\": \""
            + TWConnectorConsts.THING_TEMPLATE
            + "\"}";

    // Perform POST request
    String response =
        TWHttpUtil.httpPost(
            createThingEndpointFullUrl, createThingRequestHeader, createThingRequestBody);

    // Verify response is empty (success)
    boolean successFlag = false;
    if (response.length() == 0) {
      successFlag = true;
    } else if (response.equals("Thing [" + getApiDeviceName() + "] already exists")) {
      Logger.LOG_INFO("A device entry already exists in Thingworx.");
    } else {
      Logger.LOG_WARN(
          "An unhandled response was received while creating this thing ["
              + getApiDeviceName()
              + "]: "
              + response
              + ".");
    }
    return successFlag;
  }

  /**
   * Enables the device entry for the current Ewon device.
   *
   * @return true if value changed to enabled
   * @throws JSONException if unable to get configuration information
   * @throws IOException if unable to HTTP connect to Thingworx
   * @throws EWException if unable to HTTP connect to Thingworx
   */
  public static boolean enableThing() throws JSONException, IOException, EWException {
    // Build full POST request URL
    final String enableThingEndpointFullUrl =
        TWConnectorMain.getConnectorConfig().getThingworxIPAddress()
            + TWConnectorConsts.THINGS_ENDPOINT_DIR
            + getApiDeviceName()
            + TWConnectorConsts.ENABLE_THING_ENDPOINT;
    final String enableThingRequestHeader =
        "Content-Type=application/json&appKey="
            + TWConnectorMain.getConnectorConfig().getThingworxAppKey();

    // Build POST request body
    final String enableThingRequestBody = "";

    // Perform POST request
    String response =
        TWHttpUtil.httpPost(
            enableThingEndpointFullUrl, enableThingRequestHeader, enableThingRequestBody);

    // Verify response is empty (success)
    boolean successFlag = false;
    if (response.length() == 0) {
      successFlag = true;
    } else {
      Logger.LOG_WARN(
          "An unhandled response was received while enabling this thing ["
              + getApiDeviceName()
              + "]: "
              + response);
    }
    return successFlag;
  }

  /**
   * Restarts device entry for the current Ewon device.
   *
   * @return true if restarted
   * @throws JSONException if unable to get configuration information
   * @throws IOException if unable to HTTP connect to Thingworx
   * @throws EWException if unable to HTTP connect to Thingworx
   */
  public static boolean restartThing() throws JSONException, IOException, EWException {
    // Build full POST request URL
    final String restartThingEndpointFullUrl =
        TWConnectorMain.getConnectorConfig().getThingworxIPAddress()
            + TWConnectorConsts.THINGS_ENDPOINT_DIR
            + getApiDeviceName()
            + TWConnectorConsts.RESTART_THING_ENDPOINT;
    final String restartThingRequestHeader =
        "Content-Type=application/json&appKey="
            + TWConnectorMain.getConnectorConfig().getThingworxAppKey();

    // Build POST request body
    final String restartThingRequestBody = "";

    // Perform POST request
    String response =
        TWHttpUtil.httpPost(
            restartThingEndpointFullUrl, restartThingRequestHeader, restartThingRequestBody);

    // Verify response is empty (success)
    boolean successFlag = false;
    if (response.length() == 0) {
      successFlag = true;
    } else {
      Logger.LOG_WARN(
          "An unhandled response was received while restarting this thing ["
              + getApiDeviceName()
              + "]: "
              + response);
    }
    return successFlag;
  }

  /**
   * Adds a property to the current Ewon device in Thingworx with the supplied property information.
   *
   * @param name property name
   * @param type property type
   * @throws JSONException if unable to get configuration information
   * @throws IOException if unable to HTTP connect to Thingworx
   * @throws EWException if unable to HTTP connect to Thingworx
   */
  public static void addPropertyToThing(String name, TWPropertyType type)
      throws JSONException, IOException, EWException {
    // Build full POST request URL
    final String addPropertyEndpointFullUrl =
        TWConnectorMain.getConnectorConfig().getThingworxIPAddress()
            + TWConnectorConsts.THINGS_ENDPOINT_DIR
            + getApiDeviceName()
            + TWConnectorConsts.ADD_PROPERTY_ENDPOINT;
    final String addPropertyRequestHeader =
        "Content-Type=application/json&appKey="
            + TWConnectorMain.getConnectorConfig().getThingworxAppKey();

    // Build POST request body for tag
    final String addPropertyRequestBody =
        "{"
            + "\"name\" : "
            + "\""
            + name
            + "\","
            + "\"type\" : "
            + "\""
            + type.getTypeString()
            + "\""
            + "}";

    // Build POST request body for tag timestamp
    final String addPropertyTimestampRequestBody =
        "{"
            + "\"name\" : "
            + "\""
            + name
            + TWConnectorConsts.PROPERTY_NAME_TIMESTAMP_SUFFIX
            + "\","
            + "\"type\" : "
            + "\""
            + TWPropertyType.DATETIME.getTypeString()
            + "\""
            + "}";

    // Perform POST request for tag
    String response =
        TWHttpUtil.httpPost(
            addPropertyEndpointFullUrl, addPropertyRequestHeader, addPropertyRequestBody);
    if (response.equalsIgnoreCase("Property [" + name + "] already exists")) {
      Logger.LOG_INFO("Property [" + name + "] already exists on Thingworx.");
    } else if (response.length() == 0) {
      Logger.LOG_INFO("Property [" + name + "] was created on Thingworx.");
    } else {
      Logger.LOG_WARN(
          "An unhandled response was received while creating the property ["
              + name
              + "]: "
              + response);
    }

    // Perform POST request for tag timestamp
    String timestampResponse =
        TWHttpUtil.httpPost(
            addPropertyEndpointFullUrl, addPropertyRequestHeader, addPropertyTimestampRequestBody);
    if (timestampResponse.equalsIgnoreCase(
        "Property ["
            + name
            + TWConnectorConsts.PROPERTY_NAME_TIMESTAMP_SUFFIX
            + "] already exists")) {
      Logger.LOG_INFO(
          "Property ["
              + name
              + TWConnectorConsts.PROPERTY_NAME_TIMESTAMP_SUFFIX
              + "] timestamp already exists on Thingworx.");
    } else if (timestampResponse.length() == 0) {
      Logger.LOG_INFO(
          "Property ["
              + name
              + TWConnectorConsts.PROPERTY_NAME_TIMESTAMP_SUFFIX
              + "] timestamp was created on Thingworx.");
    } else {
      Logger.LOG_WARN(
          "An unhandled response was received while creating the property ["
              + name
              + TWConnectorConsts.PROPERTY_NAME_TIMESTAMP_SUFFIX
              + "]: "
              + timestampResponse);
    }
  }

  /**
   * Pushes the specified data point to the corresponding property (must exist) on the current Ewon
   * device's Thingworx device entry.
   *
   * @param dataPoint data point to add to Thingworx
   * @throws JSONException if unable to get configuration information
   * @throws IOException if unable to HTTP connect to Thingworx
   * @throws EWException if unable to HTTP connect to Thingworx
   */
  public static void pushDataPointToThingworx(DataPoint dataPoint)
      throws JSONException, IOException, EWException {
    // Build full PUT request URL
    final String pushDataPointEndpointFullUrl =
        TWConnectorMain.getConnectorConfig().getThingworxIPAddress()
            + TWConnectorConsts.THINGS_ENDPOINT_DIR
            + getApiDeviceName()
            + TWConnectorConsts.PROPERTIES_ENDPOINT_DIRECTORY
            + dataPoint.getTagName();
    final String pushDataPointRequestHeader =
        "Content-Type=application/json&appKey="
            + TWConnectorMain.getConnectorConfig().getThingworxAppKey();

    // Build PUT request body for tag
    final String pushDataPointRequestBody =
        "{" + "\"" + dataPoint.getTagName() + "\" : " + dataPoint.getValueString() + "}";

    // Build PUT request body for tag timestamp
    final String pushDataPointTimeStampRequestBody =
        "{"
            + "\""
            + dataPoint.getTagName()
            + TWConnectorConsts.PROPERTY_NAME_TIMESTAMP_SUFFIX
            + "\" : "
            + new Date(Long.parseLong(dataPoint.getTimeStamp())).toString()
            + "}";

    // Perform PUT request for tag
    String response =
        TWHttpUtil.httpPut(
            pushDataPointEndpointFullUrl, pushDataPointRequestHeader, pushDataPointRequestBody);
    Logger.LOG_CRITICAL("RESPONSE: " + response);

    // Perform PUT request for tag timestamp
    String timestampResponse =
        TWHttpUtil.httpPut(
            pushDataPointEndpointFullUrl,
            pushDataPointRequestHeader,
            pushDataPointTimeStampRequestBody);
    Logger.LOG_CRITICAL("RESPONSE/TS: " + timestampResponse);
  }
}
