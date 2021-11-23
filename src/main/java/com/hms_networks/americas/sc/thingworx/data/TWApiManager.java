package com.hms_networks.americas.sc.thingworx.data;

import com.hms_networks.americas.sc.logging.Logger;
import com.hms_networks.americas.sc.thingworx.TWConnectorConsts;
import com.hms_networks.americas.sc.thingworx.TWConnectorMain;
import com.hms_networks.americas.sc.thingworx.utils.HttpUtils;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Class for managing HTTP API calls to the Thingworx API.
 *
 * @since 1.0
 * @version 1.1
 * @author HMS Networks, MU Americas Solution Center
 */
public class TWApiManager {

  /** The interval at which pending data payloads are sent to Thingworx. */
  private static long dataSendThreadIntervalMillis =
      TWConnectorConsts.CONNECTOR_CONFIG_DEFAULT_PAYLOAD_SEND_INTERVAL_MILLIS;

  /** Boolean indicating if the data send thread should run. */
  private static boolean runDataThread = true;

  /** Sets the run data thread flag to false to indicate that the tread should stop running. */
  public static void setDataThreadStopFlag() {
    synchronized (TWApiManager.class) {
      runDataThread = false;
    }
  }

  /**
   * Gets the name of the Ewon Flexy as it appears/should appear in Thingworx.
   *
   * @return Thingworx device name
   * @since 1.0
   */
  public static String getApiDeviceName() {
    return "FLEXY-" + TWConnectorConsts.EWON_SERIAL_NUMBER;
  }

  /** Starts the thread which sends pending data payloads to Thingworx. */
  public static void startDataSendThread() {
    // Get data payload send interval (millis) from config file
    try {
      dataSendThreadIntervalMillis =
          TWConnectorMain.getConnectorConfig().getDataPayloadSendIntervalMillis();
    } catch (Exception e) {
      Logger.LOG_SERIOUS(
          "An error occurred while reading the data payload send interval (in milliseconds) from"
              + " the configuration file! Using default value of "
              + dataSendThreadIntervalMillis
              + ".");
      Logger.LOG_EXCEPTION(e);
    }

    // Build runnable to send pending payloads to Thingworx
    Runnable dataSendThreadRunnable =
        new Runnable() {
          public void run() {
            // Loop until stopped
            boolean stayInLoop = true;
            while (stayInLoop) {
              // Copy pending payloads from data manager
              List pendingPayloads = new ArrayList(TWDataManager.getPayloadsToSend());

              // Iterate through each pending payload and send
              Iterator pendingPayloadsIterator = pendingPayloads.iterator();
              while (pendingPayloadsIterator.hasNext()) {
                TWDataPayload dataPayload = (TWDataPayload) pendingPayloadsIterator.next();

                // Send to Thingworx
                boolean isSuccessful = sendJsonToThingworx(dataPayload.getPayloadString());

                // If successful, remove from pending payloads
                if (isSuccessful) {
                  Logger.LOG_DEBUG(
                      "Successfully sent a payload to Thingworx with "
                          + dataPayload.getDataPointCount()
                          + " data points.");
                  TWDataManager.removedPendingPayload(dataPayload);
                } else {
                  Logger.LOG_SERIOUS(
                      "A payload containing "
                          + dataPayload.getDataPointCount()
                          + " data points failed to send to Thingworx.");
                }
              }

              // Update keepRunning
              synchronized (TWApiManager.class) {
                stayInLoop = runDataThread;
              }

              // Delay until next interval
              try {
                Thread.sleep(dataSendThreadIntervalMillis);
              } catch (Exception e) {
                Logger.LOG_WARN(
                    "An error occurred while sleeping the data send thread until its next run"
                        + " interval!");
                Logger.LOG_EXCEPTION(e);
              }
            }
          }
        };

    // Create new thread object and run
    Thread dataSendThread = new Thread(dataSendThreadRunnable);
    dataSendThread.start();
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
      response = HttpUtils.httpPost(addInfoEndpointFullUrl, addInfoRequestHeader, json);
      if (response != null
          && (response.equals(HttpUtils.EWON_ERROR_STRING_RESPONSE)
              || response.equals(HttpUtils.AUTH_ERROR_STRING_RESPONSE)
              || response.equals(HttpUtils.CONNECTION_ERROR_STRING_RESPONSE))) {
        isSuccessful = false;
      }
    } catch (Exception e) {
      Logger.LOG_CRITICAL(
          "An error occurred while performing an HTTP POST to Thingworx. Data may have"
              + " been lost!");
      Logger.LOG_EXCEPTION(e);
      isSuccessful = false;
    }
    Logger.LOG_DEBUG("Thingworx HTTP POST response (send telemetry data): " + response);
    return isSuccessful;
  }
}
