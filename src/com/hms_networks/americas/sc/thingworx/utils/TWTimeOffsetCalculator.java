package com.hms_networks.americas.sc.thingworx.utils;

import com.ewon.ewonitf.EWException;
import com.ewon.ewonitf.ScheduledActionManager;
import com.hms_networks.americas.sc.logging.Logger;
import com.hms_networks.americas.sc.thingworx.TWConnectorConsts;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Utility class for calculating the offset between local time and UTC.
 *
 * @author HMS Networks, MU Americas Solution Center
 * @since 1.0
 * @version 1.0
 */
public class TWTimeOffsetCalculator {

  /**
   * Time offset in milliseconds between local time and UTC.
   *
   * @since 1.0
   */
  private static long timeOffsetMilliseconds = 0;

  /**
   * Gets the currently stored time offset in milliseconds.
   *
   * @return time offset in milliseconds
   * @since 1.0
   */
  public static synchronized long getTimeOffsetMilliseconds() {
    return timeOffsetMilliseconds;
  }

  /**
   * Calculate the local time offset in milliseconds and store in {@link #timeOffsetMilliseconds}.
   *
   * @since 1.0
   */
  public static synchronized void calculateTimeOffsetMilliseconds(
      String ftpUser, String ftpPassword) {
    writeLocalTimeHTMLFile();
    generateLocalTimeFile(ftpUser, ftpPassword);
    timeOffsetMilliseconds = parseOffsetFromLocalTime();
  }

  /**
   * Create an HTML file that can be used to read local time via {@link
   * com.ewon.ewonitf.ScheduledActionManager#GetHttp(String, String, String)}.
   *
   * @since 1.0
   */
  private static void writeLocalTimeHTMLFile() {
    File file = new File(TWConnectorConsts.TIME_OFFSET_HTML_FILE_NAME);
    file.getParentFile().mkdirs();
    FileWriter fr = null;
    BufferedWriter br = null;
    try {
      fr = new FileWriter(file);
      br = new BufferedWriter(fr);

      // This will be used by the ScheduledActionManager to write the current local time to a file
      br.write("<%#ExeSSI,PRINT #0,TIME$%>");

    } catch (IOException e) {
      Logger.LOG_SERIOUS(
          "Writing time offset file failed."
              + " Application will not be able to calculate the device's local time.");
      Logger.LOG_EXCEPTION(e);
    }

    try {
      if (br != null) {
        br.close();
      }
      if (fr != null) {
        fr.close();
      }
    } catch (IOException e) {
      Logger.LOG_SERIOUS(
          "Application was not able to close files used to store device's local time.");
      Logger.LOG_EXCEPTION(e);
    }
  }

  /**
   * Generates a local time offset file using an HTTP connection to the HTML file generated in
   * {@link #writeLocalTimeHTMLFile()}. The HTTP connection is authenticated using the configured
   * FTP username and password from {@link TWConnectorConsts}.
   *
   * @since 1.0
   */
  private static void generateLocalTimeFile(String ftpUser, String ftpPassword) {
    String httpUserCredentialsAndServer = ftpUser + ":" + ftpPassword + "@127.0.0.1";
    try {
      ScheduledActionManager.GetHttp(
          httpUserCredentialsAndServer,
          TWConnectorConsts.TIME_OFFSET_RESPONSE_FILE_NAME,
          TWConnectorConsts.TIME_OFFSET_HTML_FILE_NAME);
    } catch (EWException e) {
      Logger.LOG_SERIOUS(
          "Unable to generate local time offset file using supplied FTP credentials.");
      Logger.LOG_EXCEPTION(e);
    }
  }

  /**
   * Parses and returns the generated local time offset file from {@link #generateLocalTimeFile()}.
   *
   * @return local time offset
   * @since 1.0
   */
  private static long parseOffsetFromLocalTime() {
    String line = "";
    BufferedReader reader;
    try {
      reader = new BufferedReader(new FileReader(TWConnectorConsts.TIME_OFFSET_RESPONSE_FILE_NAME));
      line = reader.readLine();

      reader.close();
    } catch (IOException e) {
      Logger.LOG_SERIOUS(
          "Application was unable to read the file generated"
              + " when calculating the local time offset. Check that"
              + " the required Ewon FTP user account has been configured.");
      Logger.LOG_EXCEPTION(e);
    }

    try {
      int endIndex = line.indexOf("<BR>");
      line = line.substring(0, endIndex);
    } catch (Exception e) {
      Logger.LOG_SERIOUS(
          "Application was unable to remove line break from the file generated"
              + " when calculating the local time offset. Check that"
              + " the required Ewon FTP user account has been configured.");
      Logger.LOG_EXCEPTION(e);
    }

    SimpleDateFormat sdf = new SimpleDateFormat(TWConnectorConsts.TIME_OFFSET_DATE_FORMAT);
    java.util.Date localTimeDateObj = null;
    java.util.Date systemTimeDateObj = new Date(System.currentTimeMillis());

    try {
      localTimeDateObj = sdf.parse(line);
    } catch (ParseException e) {
      Logger.LOG_SERIOUS(
          "Application was unable to parse the file generated"
              + " when calculating the local time offset. Check that"
              + " the required Ewon FTP user account has been configured.");
      Logger.LOG_EXCEPTION(e);
    }

    long diffInMilliseconds = 0;
    if (localTimeDateObj != null) {
      diffInMilliseconds = systemTimeDateObj.getTime() - localTimeDateObj.getTime();
    }

    return diffInMilliseconds;
  }
}
