package com.hms_networks.americas.sc.thingworx.data;

import com.hms_networks.americas.sc.extensions.datapoint.DataPoint;
import com.hms_networks.americas.sc.extensions.logging.Logger;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Class for managing payloads of data points from the historical data queue that will be sent to
 * Thingworx using {@link TWApiManager}.
 *
 * @author HMS Networks, MU Americas Solution Center
 * @since 2.0
 */
public class TWDataManager {

  /**
   * List for storing payloads that are in progress
   *
   * @since 2.0
   */
  private static final List pendingPayloads = new ArrayList();

  /**
   * Removes the specified payload from the list of payloads that are ready to be sent to Thingworx.
   *
   * @since 2.0
   */
  public static synchronized void removedPendingPayload(TWDataPayload payload) {
    pendingPayloads.remove(payload);
  }

  /**
   * Adds all data points in the specified list to a pending payload that will be sent to Thingworx.
   *
   * @param dataPoints list of data points to add to payload
   * @since 2.0
   */
  public static synchronized void addDataPointsToPending(List dataPoints) {
    Iterator dataPointsIterator = dataPoints.iterator();
    while (dataPointsIterator.hasNext()) {
      DataPoint currentDataPoint = (DataPoint) dataPointsIterator.next();
      addDataPointToPending(currentDataPoint);
    }
  }

  /**
   * Adds the specified data point to a pending payload that will be sent to Thingworx.
   *
   * @param dataPoint data point to add to payload
   */
  public static synchronized void addDataPointToPending(DataPoint dataPoint) {
    // Add data point to first payload that is not ready to send already
    boolean didAddToExistingPayload = false;
    Iterator pendingPayloadsIterator = pendingPayloads.iterator();
    while (pendingPayloadsIterator.hasNext()) {
      TWDataPayload currentPayload = (TWDataPayload) pendingPayloadsIterator.next();
      didAddToExistingPayload = currentPayload.addDataPoint(dataPoint);
      if (didAddToExistingPayload) {
        break;
      }
    }

    // If unable to find payload to add data point to, create new
    if (!didAddToExistingPayload) {
      TWDataPayload newPayload = new TWDataPayload();
      boolean added = newPayload.addDataPoint(dataPoint);
      if (added) {
        pendingPayloads.add(newPayload);
      } else {
        Logger.LOG_SERIOUS("Unable to add data point to a new payload.");
      }
    }
  }

  /**
   * Returns a list of payloads that are ready to send to Azure. Note: payloads returned by this
   * method are removed and cannot be retrieved again if lost.
   *
   * @return list of payloads to send to Azure
   */
  public static synchronized List getPayloadsToSend() {
    return pendingPayloads;
  }
}
