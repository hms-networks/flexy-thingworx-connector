package com.hms_networks.americas.sc.thingworx.utils;

/**
 * An enum-like class that outlines the available types of Thingworx properties for use with the
 * Ewon Thingworx Connector.
 *
 * @since 1.0
 * @author HMS Network, MU Americas Solution Center
 */
public class TWPropertyType {

  /** String name constant for the string property type. */
  private static final String STRING_TYPE_STRING = "STRING";

  /** String name constant for the boolean property type. */
  private static final String BOOLEAN_TYPE_STRING = "BOOLEAN";

  /** String name constant for the integer property type. */
  private static final String INTEGER_TYPE_STRING = "INTEGER";

  /** String name constant for the number property type. */
  private static final String NUMBER_TYPE_STRING = "NUMBER";

  /** String name constant for the date/time property type. */
  private static final String DATETIME_TYPE_STRING = "DATETIME";

  /** Enum-like constant for the string property type. */
  public static final TWPropertyType STRING = new TWPropertyType(STRING_TYPE_STRING);

  /** Enum-like constant for the boolean property type. */
  public static final TWPropertyType BOOLEAN = new TWPropertyType(BOOLEAN_TYPE_STRING);

  /** Enum-like constant for the integer property type. */
  public static final TWPropertyType INTEGER = new TWPropertyType(INTEGER_TYPE_STRING);

  /** Enum-like constant for the number property type. */
  public static final TWPropertyType NUMBER = new TWPropertyType(NUMBER_TYPE_STRING);

  /** Enum-like constant for the date/time property type. */
  public static final TWPropertyType DATETIME = new TWPropertyType(DATETIME_TYPE_STRING);

  /** String representing the property type. */
  private final String typeString;

  /**
   * Constructor for enum-like Thingworx property type object.
   *
   * @param typeString Thingworx property type string
   */
  public TWPropertyType(String typeString) {
    this.typeString = typeString;
  }

  /**
   * Gets the property type string.
   *
   * @return property type string
   */
  public String getTypeString() {
    return typeString;
  }
}
