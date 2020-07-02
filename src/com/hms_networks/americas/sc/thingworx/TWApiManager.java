package com.hms_networks.americas.sc.thingworx;

import com.ewon.ewonitf.EWException;
import com.ewon.ewonitf.ScheduledActionManager;
import com.hms_networks.americas.sc.datapoint.DataPoint;
import com.hms_networks.americas.sc.fileutils.FileAccessManager;
import com.hms_networks.americas.sc.logging.Logger;
import com.hms_networks.americas.sc.thingworx.utils.TWTimeOffsetCalculator;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

/**
 * Class for managing HTTP API calls to the Thingworx API.
 *
 * @since 1.0
 * @version 1.1
 * @author HMS Networks, MU Americas Solution Center
 */
public class TWApiManager {

  /**
   * Array list of data points that are waiting to be sent to Thingworx.
   *
   * @since 1.1
   */
  private static ArrayList pendingDataPoints = new ArrayList();

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
   * Adds the specified data point to the list of data points to be sent to Thingworx.
   *
   * @param dataPoint data point to send to Thingworx
   * @since 1.1
   */
  public static synchronized void addDataPointToPending(DataPoint dataPoint) {
    pendingDataPoints.add(dataPoint);
  }

  /**
   * Sends the specified JSON string to the Thingworx Flexy TakeInfo endpoint as an HTTP POST
   * request
   *
   * @param json JSON body
   * @since 1.1
   */
  private static synchronized void sendJsonToThingworx(String json) {
    // Send to Thingworx
    // Build full POST request URL
    String addInfoEndpointFullUrl = "";
    String addInfoRequestHeader = "";
    try {
      addInfoEndpointFullUrl =
          TWConnectorMain.getConnectorConfig().getThingworxIPAddress()
              + TWConnectorConsts.THINGWORX_API_TAKE_INFO_ENDPOINT;
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
    try {
      response = httpPost(addInfoEndpointFullUrl, addInfoRequestHeader, json);
    } catch (Exception e) {
      Logger.LOG_CRITICAL(
          "An error occurred while performing an HTTP POST to Thingworx. Data may have"
              + " been lost!");
      Logger.LOG_EXCEPTION(e);
    }
    Logger.LOG_DEBUG("Thingworx HTTP POST response: " + response);
  }

  /**
   * Builds the JSON array of data points and appends it to the specified string buffer.
   *
   * @since 1.1
   * @param stringBuffer JSON string buffer
   */
  private static synchronized void buildDataPointsJson(StringBuffer stringBuffer) {
    // Add opening for data points object
    stringBuffer.append("\"datapoints\": [");

    // Append each pending data point
    Iterator pendingDataPointsIterator = pendingDataPoints.iterator();
    while (pendingDataPointsIterator.hasNext()) {
      // Get current data point
      DataPoint currentDataPoint = (DataPoint) pendingDataPointsIterator.next();
      pendingDataPointsIterator.remove();

      // Get time stamp in proper format
      long timestampLong =
          Long.valueOf(currentDataPoint.getTimeStamp()).longValue()
              * TWConnectorConsts.NUM_MILLISECONDS_PER_SECOND;
      String currentDataPointFormattedTimestamp =
          TWConnectorConsts.THINGWORX_API_DATE_FORMAT.format(new Date(timestampLong));

      // Append data point to data points list
      stringBuffer.append("{");
      stringBuffer.append("\"name\": \"").append(currentDataPoint.getTagName()).append("\",");
      stringBuffer.append("\"value\": ").append(currentDataPoint.getValueString()).append(",");
      stringBuffer
          .append("\"timestamp\": \"")
          .append(currentDataPointFormattedTimestamp)
          .append("\"");
      stringBuffer.append("}");

      // Append comma if there are more data points to be added
      if (pendingDataPointsIterator.hasNext()) {
        stringBuffer.append(",");
      }
    }

    // Add closing for data points object
    stringBuffer.append("], ");
  }

  /**
   * Builds a payload containing the data points that are pending and sends them to Thingworx. This
   * task is performed on a thread that is spawned to avoid an overload of the main thread.
   *
   * @since 1.1
   */
  public static synchronized void sendPendingToThingworx() {
    // Build runnable to send pending data points to Thingworx
    Runnable sendPendingRunnable =
        new Runnable() {
          public void run() {

            final int pendingDataPointsCount = pendingDataPoints.size();
            if (pendingDataPointsCount > 0) {

              // Create string buffer for building payload
              StringBuffer payloadBuffer = new StringBuffer();

              // Add opening JSON bracket
              payloadBuffer.append("{\"Tags\":{");

              // Add data points array
              buildDataPointsJson(payloadBuffer);

              // Add opening for info object
              payloadBuffer.append("\"info\": {");

              // Append Ewon name in info object
              payloadBuffer.append("\"ewon-name\": \"").append(getApiDeviceName()).append("\",");

              // Append Ewon time offset from UTC in milliseconds
              payloadBuffer
                  .append("\"ewon-utc-offset-millis\": \"")
                  .append(TWTimeOffsetCalculator.getTimeOffsetMilliseconds())
                  .append("\"");

              // Add closing for info object
              payloadBuffer.append("}");

              // Add closing JSON bracket
              payloadBuffer.append("}}");

              // Send to Thingworx
              Logger.LOG_DEBUG(
                  "Sending HTTP POST request to Thingworx with "
                      + pendingDataPointsCount
                      + " data points.");
              sendJsonToThingworx(payloadBuffer.toString());
            }
          }
        };

    // Create new thread and run created runnable
    Thread sendPendingThread = new Thread(sendPendingRunnable);
    sendPendingThread.start();
    Logger.LOG_DEBUG("A send pending data points thread has been created and started.");
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
    } else if (httpStatus == TWConnectorConsts.HTTPX_CODE_AUTH_ERROR) {
      Logger.LOG_SERIOUS(
          "An authentication error was encountered while performing an HTTP POST request to "
              + url
              + "! Data loss may result.");
    } else if (httpStatus == TWConnectorConsts.HTTPX_CODE_CONNECTION_ERROR) {
      Logger.LOG_SERIOUS(
          "A connection error was encountered while performing an HTTP POST request to "
              + url
              + "! Data loss may result.");
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
