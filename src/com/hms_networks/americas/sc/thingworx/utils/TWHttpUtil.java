package com.hms_networks.americas.sc.thingworx.utils;

import com.ewon.ewonitf.EWException;
import com.ewon.ewonitf.ScheduledActionManager;
import com.hms_networks.americas.sc.fileutils.FileAccessManager;
import com.hms_networks.americas.sc.thingworx.TWConnectorConsts;
import java.io.File;
import java.io.IOException;

/**
 * Class for managing HTTP connections to the Thingworx HTTP API.
 *
 * @author HMS Networks, MU Americas Solution Center
 * @since 1.0
 */
public class TWHttpUtil {

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
    final File responseFile = new File("/usr/http/response.post");
    responseFile.getParentFile().mkdirs();

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
    } else {
      responseFileString = String.valueOf(httpStatus);
    }
    return responseFileString;
  }

  /**
   * Performs an HTTP GET requests to the specified URL using the specified request header and body.
   *
   * @param url URL to make request
   * @param header request header
   * @param body request body
   * @throws EWException if unable to make GET request
   */
  public static String httpGet(String url, String header, String body)
      throws EWException, IOException {
    // Create file for storing response
    final File responseFile = new File("/usr/http/response.get");
    responseFile.getParentFile().mkdirs();

    // Perform GET request to specified URL
    int httpStatus =
        ScheduledActionManager.RequestHttpX(
            url,
            TWConnectorConsts.HTTP_GET_STRING,
            header,
            body,
            "",
            responseFile.getAbsolutePath());

    // Read response contents and return
    String responseFileString = "";
    if (httpStatus == TWConnectorConsts.HTTPX_CODE_NO_ERROR) {
      responseFileString = FileAccessManager.readFileToString(responseFile.getAbsolutePath());
    } else {
      responseFileString = String.valueOf(httpStatus);
    }
    return responseFileString;
  }

  /**
   * Performs an HTTP PUT requests to the specified URL using the specified request header and body.
   *
   * @param url URL to make request
   * @param header request header
   * @param body request body
   * @throws EWException if unable to make PUT request
   */
  public static String httpPut(String url, String header, String body)
      throws EWException, IOException {
    // Create file for storing response
    final File responseFile = new File("/usr/http/response.put");
    responseFile.getParentFile().mkdirs();

    // Perform PUT request to specified URL
    int httpStatus =
        ScheduledActionManager.RequestHttpX(
            url,
            TWConnectorConsts.HTTP_PUT_STRING,
            header,
            body,
            "",
            responseFile.getAbsolutePath());

    // Read response contents and return
    String responseFileString = "";
    if (httpStatus == TWConnectorConsts.HTTPX_CODE_NO_ERROR) {
      responseFileString = FileAccessManager.readFileToString(responseFile.getAbsolutePath());
    } else {
      responseFileString = String.valueOf(httpStatus);
    }
    return responseFileString;
  }
}
