package com.hms_networks.americas.sc.thingworx.utils;

/**
 * Class providing utilities for working with and manipulating {@link String} objects.
 *
 * @author HMS Networks, MU Americas Solution Center
 * @since 1.0
 * @version 1.0
 */
public class StringUtils {

  /**
   * Method to replace all instances of <code>before</code> with <code>after</code> in <code>string
   * </code> and return the result.
   *
   * @param string string to modify
   * @param before to replace
   * @param after replacement
   * @return string with replacement completed
   * @since 1.0
   */
  public static String replaceAll(String string, String before, String after) {
    // Create String buffer for building the modified string
    StringBuffer stringBuffer = new StringBuffer();

    // Loop through each character in -string-
    for (int x = 0; x < string.length(); ) {
      // Search for an instance of -before- in -string-
      int indexOfBefore = string.indexOf(before, x);

      // Perform replacement if at start index
      if (x == indexOfBefore) {
        stringBuffer.append(after);
        x += before.length();
      } else {
        // Append character and move to next
        stringBuffer.append(string.charAt(x++));
      }
    }

    // Return modified string
    return stringBuffer.toString();
  }
}
