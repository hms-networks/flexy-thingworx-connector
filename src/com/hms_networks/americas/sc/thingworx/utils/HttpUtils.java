package com.hms_networks.americas.sc.thingworx.utils;

import com.ewon.ewonitf.EWException;
import com.ewon.ewonitf.ScheduledActionManager;
import com.hms_networks.americas.sc.fileutils.FileAccessManager;
import com.hms_networks.americas.sc.logging.Logger;
import com.hms_networks.americas.sc.thingworx.TWConnectorConsts;

import java.io.File;
import java.io.IOException;

/**
 * Class providing utilities for performing HTTP requests.
 *
 * @author HMS Networks, MU Americas Solution Center
 * @since 2.0.1
 */
public class HttpUtils {

  /**
   * String constant returned by the {@link #httpPost(String, String, String)} method to indicate an
   * Ewon error occurred.
   */
  public static final String EWON_ERROR_STRING_RESPONSE = "EwonError";

  /**
   * String constant returned by the {@link #httpPost(String, String, String)} method to indicate an
   * authentication error occurred.
   */
  public static final String AUTH_ERROR_STRING_RESPONSE = "AuthError";

  /**
   * String constant returned by the {@link #httpPost(String, String, String)} method to indicate a
   * connection error occurred.
   */
  public static final String CONNECTION_ERROR_STRING_RESPONSE = "ConnectionError";

  /** Counter used to uniquely identify temporary files used to store HTTP responses. */
  private static int tempResponseFileNameCounter = 0;

  /**
   * Performs an HTTP POST requests to the specified URL using the specified request header and
   * body.
   *
   * @param url URL to make request
   * @param header request header
   * @param body request body
   * @throws EWException if unable to make POST request
   */
  public static String httpPost(String url, String header, String body)
      throws EWException, IOException {
    // Create file for storing response
    final File responseFile =
        new File("/usr/http/response" + tempResponseFileNameCounter++ + ".post");
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
    responseFile.delete();
    return responseFileString;
  }
}
