package com.hms_networks.americas.sc.thingworx.data;

import com.hms_networks.americas.sc.datapoint.DataPoint;
import com.hms_networks.americas.sc.thingworx.TWConnectorConsts;
import com.hms_networks.americas.sc.thingworx.utils.TWTimeOffsetCalculator;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * Payload class that stores data points and provides a method for converting to a string.
 *
 * @author HMS Networks, MU Americas Solution Center
 * @since 2.0
 */
public class TWDataPayload {

  /** The maximum number of data points that can be in one payload. */
  private static final int PAYLOAD_MAX_DATA_POINTS = 50;

  /** List of data points in payload. */
  private final List dataPoints = new ArrayList();

  /**
   * Adds the specified data point to the list of data points in the payload.
   *
   * @param datapoint data point to add
   * @return true if data point added
   */
  public synchronized boolean addDataPoint(DataPoint datapoint) {
    // Check if payload reached max size
    boolean canAddDataPoint = dataPoints.size() < PAYLOAD_MAX_DATA_POINTS;

    // Add to payload is within size
    if (canAddDataPoint) {
      dataPoints.add(datapoint);
    }

    return canAddDataPoint;
  }

  /**
   * Returns the number of data points in the payload.
   *
   * @return number of data points
   */
  public synchronized int getDataPointCount() {
    return dataPoints.size();
  }

  /**
   * Appends the data points JSON string to the specified string buffer.
   *
   * @param stringBuffer string buffer to append to
   */
  private synchronized void appendPayloadDataPointsString(StringBuffer stringBuffer) {
    // Add opening for data points object
    stringBuffer.append("\"datapoints\": [");

    // Append each pending data point
    Iterator pendingDataPointsIterator = dataPoints.iterator();
    while (pendingDataPointsIterator.hasNext()) {
      // Get current data point
      DataPoint currentDataPoint = (DataPoint) pendingDataPointsIterator.next();

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
          .append("\"type\": ")
          .append(currentDataPoint.getType().getRawDataType())
          .append(",");
      stringBuffer
          .append("\"quality\": ")
          .append(currentDataPoint.getQuality().getRawDataQuality())
          .append(",");
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
   * Gets the string representation of the payload.
   *
   * @return payload string
   */
  public synchronized String getPayloadString() {
    // Create string buffer for building payload
    StringBuffer payloadBuffer = new StringBuffer();

    // Add opening JSON bracket
    payloadBuffer.append("{\"Tags\":{");

    // Add data points array
    appendPayloadDataPointsString(payloadBuffer);

    // Add opening for info object
    payloadBuffer.append("\"info\": {");

    // Append Ewon name in info object
    payloadBuffer.append("\"ewon-name\": \"").append(TWApiManager.getApiDeviceName()).append("\",");

    // Append Ewon time offset from UTC in milliseconds
    payloadBuffer
        .append("\"ewon-utc-offset-millis\": \"")
        .append(TWTimeOffsetCalculator.getTimeOffsetMilliseconds())
        .append("\"");

    // Add closing for info object
    payloadBuffer.append("}");

    // Add closing JSON bracket
    payloadBuffer.append("}}");

    return payloadBuffer.toString();
  }
}
