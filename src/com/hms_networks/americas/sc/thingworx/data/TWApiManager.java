package com.hms_networks.americas.sc.thingworx.data;

import com.ewon.ewonitf.EWException;
import com.ewon.ewonitf.ScheduledActionManager;
import com.hms_networks.americas.sc.fileutils.FileAccessManager;
import com.hms_networks.americas.sc.logging.Logger;
import com.hms_networks.americas.sc.thingworx.TWConnectorConsts;
import com.hms_networks.americas.sc.thingworx.TWConnectorMain;
import java.io.File;
import java.io.IOException;

/**
 * Class for managing HTTP API calls to the Thingworx API.
 *
 * @since 1.0
 * @version 1.1
 * @author HMS Networks, MU Americas Solution Center
 */
public class TWApiManager {

  /**
   * String constant returned by the {@link #httpPost(String, String, String)} method to indicate an
   * Ewon error occurred.
   */
  private static final String EWON_ERROR_STRING_RESPONSE = "EwonError";

  /**
   * String constant returned by the {@link #httpPost(String, String, String)} method to indicate an
   * authentication error occurred.
   */
  private static final String AUTH_ERROR_STRING_RESPONSE = "AuthError";

  /**
   * String constant returned by the {@link #httpPost(String, String, String)} method to indicate a
   * connection error occurred.
   */
  private static final String CONNECTION_ERROR_STRING_RESPONSE = "ConnectionError";

  /**
   * Gets the name of the Ewon Flexy as it appears/should appear in Thingworx.
   *
   * @return Thingworx device name
   * @since 1.0
   */
  public static String getApiDeviceName() {
    return "FLEXY-" + TWConnectorConsts.EWON_SERIAL_NUMBER;
  }

  /**
   * Sends the specified JSON string to the configured Thingworx Full URL endpoint as an HTTP POST
   * request
   *
   * @param json JSON body
   * @since 1.1
   */
  private static boolean sendJsonToThingworx(String json) {
    // Send to Thingworx
    // Build full POST request URL
    String addInfoEndpointFullUrl = "";
    String addInfoRequestHeader = "";
    try {
      addInfoEndpointFullUrl = TWConnectorMain.getConnectorConfig().getThingworxFullUrl();
      addInfoRequestHeader =
          "Content-Type=application/json&appKey="
              + TWConnectorMain.getConnectorConfig().getThingworxAppKey();
    } catch (Exception e) {
      Logger.LOG_CRITICAL(
          "Unable to get configuration information for sending data to Thingworx. Data"
              + " may be lost!");
      Logger.LOG_EXCEPTION(e);
    }

    String response = null;
    boolean isSuccessful = true;
    try {
      response = httpPost(addInfoEndpointFullUrl, addInfoRequestHeader, json);
      if (response != null
          && (response.equals(EWON_ERROR_STRING_RESPONSE)
              || response.equals(AUTH_ERROR_STRING_RESPONSE)
              || response.equals(CONNECTION_ERROR_STRING_RESPONSE))) {
        isSuccessful = false;
      }
    } catch (Exception e) {
      Logger.LOG_CRITICAL(
          "An error occurred while performing an HTTP POST to Thingworx. Data may have"
              + " been lost!");
      Logger.LOG_EXCEPTION(e);
      isSuccessful = false;
    }
    Logger.LOG_DEBUG("Thingworx HTTP POST response: " + response);
    return isSuccessful;
  }

  /**
   * Performs an HTTP POST requests to the specified URL using the specified request header and
   * body.
   *
   * @param url URL to make request
   * @param header request header
   * @param body request body
   * @throws EWException if unable to make POST request
   * @since 1.1
   */
  public static String httpPost(String url, String header, String body)
      throws EWException, IOException {
    // Create file for storing response
    final File responseFile = new File("/usr/http/response.post");
    responseFile.getParentFile().mkdirs();
    responseFile.delete();

    // Perform POST request to specified URL
    int httpStatus =
        ScheduledActionManager.RequestHttpX(
            url,
            TWConnectorConsts.HTTP_POST_STRING,
            header,
            body,
            "",
            responseFile.getAbsolutePath());

    // Read response contents and return
    String responseFileString = "";
    if (httpStatus == TWConnectorConsts.HTTPX_CODE_NO_ERROR) {
      responseFileString = FileAccessManager.readFileToString(responseFile.getAbsolutePath());
    } else if (httpStatus == TWConnectorConsts.HTTPX_CODE_EWON_ERROR) {
      Logger.LOG_SERIOUS(
          "An Ewon error was encountered while performing an HTTP POST request to "
              + url
              + "! Data loss may result.");
      responseFileString = EWON_ERROR_STRING_RESPONSE;
    } else if (httpStatus == TWConnectorConsts.HTTPX_CODE_AUTH_ERROR) {
      Logger.LOG_SERIOUS(
          "An authentication error was encountered while performing an HTTP POST request to "
              + url
              + "! Data loss may result.");
      responseFileString = AUTH_ERROR_STRING_RESPONSE;
    } else if (httpStatus == TWConnectorConsts.HTTPX_CODE_CONNECTION_ERROR) {
      Logger.LOG_SERIOUS(
          "A connection error was encountered while performing an HTTP POST request to "
              + url
              + "! Data loss may result.");
      responseFileString = CONNECTION_ERROR_STRING_RESPONSE;
    } else {
      Logger.LOG_SERIOUS(
          "An unknown error ("
              + httpStatus
              + ") was encountered while performing an HTTP POST request to "
              + url
              + "! Data loss may result.");
      responseFileString = String.valueOf(httpStatus);
    }
    return responseFileString;
  }
}
