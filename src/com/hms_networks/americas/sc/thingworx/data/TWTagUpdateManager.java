package com.hms_networks.americas.sc.thingworx.data;

import com.ewon.ewonitf.*;
import com.hms_networks.americas.sc.json.JSONArray;
import com.hms_networks.americas.sc.json.JSONException;
import com.hms_networks.americas.sc.json.JSONObject;
import com.hms_networks.americas.sc.json.JSONTokener;
import com.hms_networks.americas.sc.logging.Logger;
import com.hms_networks.americas.sc.taginfo.TagInfo;
import com.hms_networks.americas.sc.taginfo.TagInfoManager;
import com.hms_networks.americas.sc.taginfo.TagType;
import com.hms_networks.americas.sc.thingworx.TWConnectorMain;
import com.hms_networks.americas.sc.thingworx.utils.HttpUtils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

/**
 * Class for managing triggered checks for updated tag values from the Thingworx tag update endpoint
 * URL, if configured.
 *
 * @author HMS Networks, MU Americas Solution Center
 * @since 2.0.1
 */
public class TWTagUpdateManager {

  /**
   * Constant integer value used to indicate a non-found or missing tag ID when checking if a tag is
   * present.
   */
  private static final int TAG_UPDATE_MESSAGE_TAG_ID_EMPTY = -1;

  /** Constant integer value used to indicate when no tag update is triggered. */
  private static final int TAG_UPDATE_TRIGGER_VALUE_NONE = 0;

  /**
   * Constant string value used to indicate empty or no additional string content to be sent a with
   * triggered tag update.
   */
  private static final String TAG_UPDATE_TRIGGER_INFO_STRING_VALUE_NONE = "";

  /** Constant integer value for the tag update result enumeration tag used as the initial value. */
  private static final int TAG_UPDATE_RESULT_VALUE_INITIAL = 0;

  /**
   * Constant integer value for the tag update result enumeration tag used to indicate that the HTTP
   * tag update request has started.
   */
  private static final int TAG_UPDATE_RESULT_VALUE_HTTP_STARTED = 1;

  /**
   * Constant integer value for the tag update result enumeration tag used to indicate that the
   * request was successful.
   */
  private static final int TAG_UPDATE_RESULT_VALUE_SUCCESS = 2;

  /**
   * Constant integer value for the tag update result enumeration tag used to indicate that a
   * connection error was encountered.
   */
  private static final int TAG_UPDATE_RESULT_VALUE_CONNECTION_ERROR = 3;

  /**
   * Constant integer value for the tag update result enumeration tag used to indicate that an Ewon
   * error was encountered.
   */
  private static final int TAG_UPDATE_RESULT_VALUE_EWON_ERROR = 4;

  /**
   * Constant integer value for the tag update result enumeration tag used to indicate that an error
   * was encountered while verifying the tag update response.
   */
  private static final int TAG_UPDATE_RESULT_VALUE_PAYLOAD_VERIFY_FAIL = 5;

  /**
   * Constant integer value for the tag update result enumeration tag used to indicate that an error
   * was encountered while applying the values in the tag update response.
   */
  private static final int TAG_UPDATE_RESULT_VALUE_PAYLOAD_APPLY_FAIL = 6;

  /**
   * Constant integer value for the tag update result enumeration tag used to indicate that the tag
   * update response contained tags which are missing from the Ewon.
   */
  private static final int TAG_UPDATE_RESULT_VALUE_PAYLOAD_MISSING_TAGS = 7;

  /**
   * Constant integer value for the tag update result enumeration tag used to indicate that the tag
   * update response contained tags which have mismatched types on the Ewon.
   */
  private static final int TAG_UPDATE_RESULT_VALUE_PAYLOAD_MISMATCHED_TAG_TYPES = 8;

  /** Constant string value used to access the message ID field in tag update messages. */
  private static final String TAG_UPDATE_MESSAGE_ID_KEY = "id";

  /** Constant string value used to access the JSON RPC field in tag update messages. */
  private static final String TAG_UPDATE_MESSAGE_JSON_RPC_KEY = "jsonrpc";

  /**
   * Constant string value used to indicate the supported and expected JSON RPC version for tag
   * update messages.
   */
  private static final String TAG_UPDATE_MESSAGE_JSON_RPC_VERSION = "2.0";

  /** Constant string value used to access the method field in tag update messages. */
  private static final String TAG_UPDATE_MESSAGE_METHOD_KEY = "method";

  /** Constant string value used to access the parameters field in tag update messages. */
  private static final String TAG_UPDATE_MESSAGE_PARAMS_KEY = "params";

  /** Constant string value used to access the string information field in tag update messages. */
  private static final String TAG_UPDATE_MESSAGE_STRING_INFO_KEY = "stringInfo";

  /** Constant string value used to access the error field in tag update messages. */
  private static final String TAG_UPDATE_MESSAGE_ERROR_KEY = "error";

  /** Constant string value used to access the result field in tag update messages. */
  private static final String TAG_UPDATE_MESSAGE_RESULT_KEY = "result";

  /**
   * Constant string value used to access the restore previous values on fault field in tag update
   * messages.
   */
  private static final String TAG_UPDATE_MESSAGE_RESTORE_PREVIOUS_ON_FAULT_KEY =
      "restorePreviousValsOnFault";

  /** Constant string value used to access the tags field in tag update messages. */
  private static final String TAG_UPDATE_MESSAGE_TAGS_KEY = "tags";

  /** Constant string value used to access the error code field in tag update messages. */
  private static final String TAG_UPDATE_MESSAGE_ERROR_CODE_KEY = "code";

  /** Constant string value used to access the error message field in tag update messages. */
  private static final String TAG_UPDATE_MESSAGE_ERROR_MESSAGE_KEY = "message";

  /** Constant string value used to access the tag name field in tag update messages. */
  private static final String TAG_UPDATE_MESSAGE_TAG_NAME_KEY = "name";

  /** Constant string value used to access the tag type field in tag update messages. */
  private static final String TAG_UPDATE_MESSAGE_TAG_TYPE_KEY = "type";

  /** Constant string value used to access the tag value field in tag update messages. */
  private static final String TAG_UPDATE_MESSAGE_TAG_VALUE_KEY = "value";

  /**
   * Default boolean value of the restore previous values on fault field in tag update messages, if
   * not present.
   */
  private static final boolean TAG_UPDATE_MESSAGE_RESTORE_PREVIOUS_ON_FAULT_DEFAULT = false;

  /** The HTTP content type used for sending and receiving tag update messages. */
  private static final String TAG_UPDATE_REQUEST_CONTENT_TYPE = "application/json";

  /**
   * The API version of tag update messages implemented by this {@link TWTagUpdateManager}. This
   * should be changed when structural or content changes are made to tag update messages to allow
   * for receivers to properly identify and decode payloads.
   */
  private static final String TAG_UPDATE_REQUEST_FLEXY_API_VERSION = "2021-4-29";

  /** Constant string value used to indicate the integer tag type. */
  private static final String TAG_UPDATE_MESSAGE_TAG_TYPE_INTEGER_STRING = "integer";

  /** Constant string value used to indicate the float tag type. */
  private static final String TAG_UPDATE_MESSAGE_TAG_TYPE_FLOAT_STRING = "float";

  /** Constant string value used to indicate the string tag type. */
  private static final String TAG_UPDATE_MESSAGE_TAG_TYPE_STRING_STRING = "string";

  /** Constant string value used to indicate the boolean tag type. */
  private static final String TAG_UPDATE_MESSAGE_TAG_TYPE_BOOLEAN_STRING = "boolean";

  /** Constant string value used to indicate the DWORD tag type. */
  private static final String TAG_UPDATE_MESSAGE_TAG_TYPE_DWORD_STRING = "dword";

  /** The name (expected) of the remote tag update trigger tag (integer enumeration). */
  private static final String TAG_UPDATE_TRIGGER_TAG_NAME = "RemoteTagUpdateTriggerEnum";

  /**
   * The name (expected) of the remote tag update trigger information tag (additional string info).
   */
  private static final String TAG_UPDATE_TRIGGER_INFO_STRING_TAG_NAME =
      "RemoteTagUpdateTriggerString";

  /** The name (expected) of the remote tag update result tag (integer enumeration). */
  private static final String TAG_UPDATE_RESULT_TAG_NAME = "RemoteTagUpdateResultEnum";

  /**
   * Listener for value changes on the trigger tags, {@link #TAG_UPDATE_TRIGGER_TAG_NAME} and {@link
   * #TAG_UPDATE_TRIGGER_INFO_STRING_TAG_NAME}.
   */
  private static final EvtTagValueListener TAG_UPDATE_TRIGGER_TAG_VALUE_LISTENER =
      new EvtTagValueListener() {
        public void callTagChanged() {
          try {
            // Check if trigger tag value changed
            String changedTagName = getTagName();
            if (changedTagName.equals(TAG_UPDATE_TRIGGER_TAG_NAME)) {
              // Check if new value is a trigger value
              int integerTagValue = getTagValueAsInt();
              boolean newIsTagUpdateRequested = (integerTagValue != TAG_UPDATE_TRIGGER_VALUE_NONE);
              if (newIsTagUpdateRequested) {
                setTagUpdateResultTagValue(TAG_UPDATE_RESULT_VALUE_INITIAL);
              }

              // Get trigger info string
              if (newIsTagUpdateRequested) {
                String triggerInfoString = "";
                try {
                  TagControl triggerInfoStringTagControl =
                      new TagControl(TAG_UPDATE_TRIGGER_INFO_STRING_TAG_NAME);
                  triggerInfoString = triggerInfoStringTagControl.getTagValueAsString();
                } catch (EWException e) {
                  Logger.LOG_SERIOUS(
                      "Unable to get the value of "
                          + TAG_UPDATE_TRIGGER_INFO_STRING_TAG_NAME
                          + " to send with triggered tag update request.");
                  Logger.LOG_EXCEPTION(e);
                }

                // Send tag update request
                sendTagUpdateRequest(String.valueOf(integerTagValue), triggerInfoString);
              }
            }
          } catch (Exception e) {
            Logger.LOG_SERIOUS(
                "An exception occurred while processing a tag update trigger tag value change!");
            Logger.LOG_EXCEPTION(e);
          }
        }
      };

  /** Integer counter for identifiers which are specified with each remote tag update payload. */
  private static int tagUpdateIdCounter = 0;

  /**
   * Configures a tag value listener to trigger the remote tag update functionality when the trigger
   * tag ({@link #TAG_UPDATE_TRIGGER_TAG_NAME}) has been changed to an applicable enumeration value.
   */
  public static void setupTagUpdateTriggerTag() {
    try {
      // Reset trigger tag values
      resetTagUpdateTriggerTags();

      // Configure tag value listener
      TAG_UPDATE_TRIGGER_TAG_VALUE_LISTENER.setTagName(TAG_UPDATE_TRIGGER_TAG_NAME);
      DefaultEventHandler.addTagValueListener(TAG_UPDATE_TRIGGER_TAG_VALUE_LISTENER);

      // Start thread for default event manager
      boolean autorun = false;
      EventHandlerThread eventHandler = new EventHandlerThread(autorun);
      eventHandler.runEventManagerInThread();
    } catch (Exception e) {
      Logger.LOG_WARN(
          "Unable to set up tag update functionality because the trigger tag listener "
              + "could not be created!");
      Logger.LOG_EXCEPTION(e);
    }
  }

  /**
   * Sets the remote tag update result enumeration tag to the specified integer enumeration value
   *
   * @param tagUpdateResultTagValue remote tag update result integer enumeration value
   */
  private static void setTagUpdateResultTagValue(int tagUpdateResultTagValue) {
    try {
      TagControl resultTag = new TagControl(TAG_UPDATE_RESULT_TAG_NAME);
      resultTag.setTagValueAsInt(tagUpdateResultTagValue);
    } catch (EWException e) {
      Logger.LOG_SERIOUS(
          "Unable to set the value of "
              + TAG_UPDATE_RESULT_TAG_NAME
              + " to "
              + tagUpdateResultTagValue
              + "!");
      Logger.LOG_EXCEPTION(e);
    }
  }

  /**
   * Resets the remote tag update trigger tags (integer enumeration and information string) to their
   * default values.
   */
  private static void resetTagUpdateTriggerTags() {
    // Reset value of integer enumeration trigger tag
    try {
      TagControl triggerTag = new TagControl(TAG_UPDATE_TRIGGER_TAG_NAME);
      triggerTag.setTagValueAsInt(TAG_UPDATE_TRIGGER_VALUE_NONE);
    } catch (EWException e) {
      Logger.LOG_SERIOUS(
          "Unable to reset the value of "
              + TAG_UPDATE_TRIGGER_TAG_NAME
              + " to default value of "
              + TAG_UPDATE_TRIGGER_VALUE_NONE
              + "!");
      Logger.LOG_EXCEPTION(e);
    }

    // Reset value of information string trigger tag
    try {
      TagControl triggerInfoStringTag = new TagControl(TAG_UPDATE_TRIGGER_INFO_STRING_TAG_NAME);
      triggerInfoStringTag.setTagValueAsString(TAG_UPDATE_TRIGGER_INFO_STRING_VALUE_NONE);
    } catch (EWException e) {
      Logger.LOG_SERIOUS(
          "Unable to reset the value of "
              + TAG_UPDATE_TRIGGER_INFO_STRING_TAG_NAME
              + " to default value of "
              + TAG_UPDATE_TRIGGER_INFO_STRING_VALUE_NONE
              + "!");
      Logger.LOG_EXCEPTION(e);
    }
  }

  /**
   * Applies the tag values from the specified {@link JSONArray} of tag values, and restores the
   * original tag values if a failure occurs as specified by the parameter <code>
   * restorePreviousValsOnFault</code>.
   *
   * @param tagValuesArray array of tag values to update/apply
   * @param restorePreviousValsOnFault boolean indicating if original tag values should be restored
   *     on error
   * @return integer enumeration indicating result (apply failure, type mismatch, etc)
   */
  private static int applyTagValuesFromJsonArray(
      JSONArray tagValuesArray, boolean restorePreviousValsOnFault) {
    // Create map to store previous values in case of restore, if enabled
    HashMap previousValsMap = null;
    if (restorePreviousValsOnFault) {
      previousValsMap = new HashMap();
    }

    // Loop through each tag in array
    int tagUpdateResult = TAG_UPDATE_RESULT_VALUE_SUCCESS;
    String currTagName = "";
    String currTagValueString = "";
    for (int tagIndex = 0; tagIndex < tagValuesArray.length(); tagIndex++) {
      try {
        // Get tag object
        JSONObject tagObject = tagValuesArray.getJSONObject(tagIndex);
        String currTagType = tagObject.getString(TAG_UPDATE_MESSAGE_TAG_TYPE_KEY);
        currTagName = tagObject.getString(TAG_UPDATE_MESSAGE_TAG_NAME_KEY);

        // Create tag control object
        TagControl currTagControl = new TagControl();
        currTagControl.setTagName(currTagName);

        // Backup previous value (if enabled) and set new value with proper data type
        if (currTagType.equals(TAG_UPDATE_MESSAGE_TAG_TYPE_INTEGER_STRING)) {
          // Backup previous value
          if (restorePreviousValsOnFault) {
            previousValsMap.put(currTagName, new Integer(currTagControl.getTagValueAsInt()));
          }

          // Update tag value
          int currTagValue = tagObject.getInt(TAG_UPDATE_MESSAGE_TAG_VALUE_KEY);
          currTagValueString = String.valueOf(currTagValue);
          currTagControl.setTagValueAsInt(currTagValue);
        } else if (currTagType.equals(TAG_UPDATE_MESSAGE_TAG_TYPE_FLOAT_STRING)) {
          // Backup previous value
          if (restorePreviousValsOnFault) {
            previousValsMap.put(currTagName, new Double(currTagControl.getTagValueAsDouble()));
          }

          // Update tag value
          double currTagValue = tagObject.getDouble(TAG_UPDATE_MESSAGE_TAG_VALUE_KEY);
          currTagValueString = String.valueOf(currTagValue);
          currTagControl.setTagValueAsDouble(currTagValue);
        } else if (currTagType.equals(TAG_UPDATE_MESSAGE_TAG_TYPE_STRING_STRING)) {
          // Backup previous value
          if (restorePreviousValsOnFault) {
            previousValsMap.put(currTagName, currTagControl.getTagValueAsString());
          }

          // Update tag value
          String currTagValue = tagObject.getString(TAG_UPDATE_MESSAGE_TAG_VALUE_KEY);
          currTagValueString = String.valueOf(currTagValue);
          currTagControl.setTagValueAsString(currTagValue);
        } else if (currTagType.equals(TAG_UPDATE_MESSAGE_TAG_TYPE_BOOLEAN_STRING)) {
          // Backup previous value
          if (restorePreviousValsOnFault) {
            previousValsMap.put(currTagName, new Integer(currTagControl.getTagValueAsInt()));
          }

          // Update tag value
          boolean currTagValue = tagObject.getBoolean(TAG_UPDATE_MESSAGE_TAG_VALUE_KEY);
          int currTagValueBooleanInt = currTagValue ? 1 : 0;
          currTagValueString = String.valueOf(currTagValueBooleanInt);
          currTagControl.setTagValueAsInt(currTagValueBooleanInt);
        } else if (currTagType.equals(TAG_UPDATE_MESSAGE_TAG_TYPE_DWORD_STRING)) {
          // Backup previous value
          if (restorePreviousValsOnFault) {
            previousValsMap.put(currTagName, new Long(currTagControl.getTagValueAsLong()));
          }

          // Update tag value
          long currTagValue = tagObject.getLong(TAG_UPDATE_MESSAGE_TAG_VALUE_KEY);
          currTagValueString = String.valueOf(currTagValue);
          currTagControl.setTagValueAsLong(currTagValue);
        }
      } catch (Exception e) {
        Logger.LOG_SERIOUS(
            "The value of the tag ["
                + currTagName
                + "] could not be updated to ["
                + currTagValueString
                + "] as specified in a tag update request response!");
        Logger.LOG_EXCEPTION(e);
        tagUpdateResult = TAG_UPDATE_RESULT_VALUE_PAYLOAD_APPLY_FAIL;

        // Break out of loop to prevent more values that will be restored from being set
        if (restorePreviousValsOnFault) {
          break;
        }
      }
    }

    // Check if error occurred during apply
    if (tagUpdateResult != TAG_UPDATE_RESULT_VALUE_SUCCESS && restorePreviousValsOnFault) {
      // Log restore start
      Logger.LOG_SERIOUS(
          "Tag values are being restored due to previous failure while applying tag "
              + "values from a tag update request response...");

      // Perform restore of each previous value from map
      boolean isPartialRestore = false;
      final Iterator previousValsEntryIterator = previousValsMap.entrySet().iterator();
      while (previousValsEntryIterator.hasNext()) {
        // Get previous tag name and value
        final Entry previousValEntry = (Entry) previousValsEntryIterator.next();
        final String previousTagName = (String) previousValEntry.getKey();
        final Object previousTagValue = previousValEntry.getValue();

        // Create tag control object
        TagControl previousTagControl = new TagControl();
        previousTagControl.setTagName(previousTagName);

        // Restore previous tag value according to type (note: boolean is stored as integer 0/1)
        try {
          if (previousTagValue instanceof Integer) {
            Integer previousTagValueInteger = (Integer) previousTagValue;
            previousTagControl.setTagValueAsInt(previousTagValueInteger.intValue());
          } else if (previousTagValue instanceof Double) {
            Double previousTagValueDouble = (Double) previousTagValue;
            previousTagControl.setTagValueAsDouble(previousTagValueDouble.doubleValue());
          } else if (previousTagValue instanceof String) {
            String previousTagValueString = (String) previousTagValue;
            previousTagControl.setTagValueAsString(previousTagValueString);
          } else if (previousTagValue instanceof Long) {
            Long previousTagValueLong = (Long) previousTagValue;
            previousTagControl.setTagValueAsLong(previousTagValueLong.longValue());
          } else {
            Logger.LOG_SERIOUS(
                "An unknown data type was encountered while restoring the previous value of ["
                    + previousTagName
                    + "] after tag update failure. Value not restored!");
            isPartialRestore = true;
          }
        } catch (Exception e) {
          Logger.LOG_SERIOUS(
              "An error occurred while restoring the previous value of ["
                  + previousTagName
                  + "] to ["
                  + previousTagValue.toString()
                  + "] after tag update failure.");
          Logger.LOG_EXCEPTION(e);
          isPartialRestore = true;
        }
      }

      // Log completion
      if (isPartialRestore) {
        Logger.LOG_SERIOUS(
            "Tag values have been partially restored! See previous logs for specific "
                + "tag details.");
      } else {
        Logger.LOG_SERIOUS("Tag values have been restored successfully!");
      }
    }

    return tagUpdateResult;
  }

  /**
   * Check if the tag with the specified tag ID on the Ewon matches the specified expected tag type.
   *
   * @param tagId Ewon tag ID
   * @param expectedTagType expected tag type
   * @return true/false indicating if tag type matches
   */
  private static boolean doesTagTypeMatch(int tagId, String expectedTagType) {
    int tagInfoListIDOffset = TagInfoManager.getLowestTagIdSeen();
    final TagInfo[] tagInfoArray = TagInfoManager.getTagInfoArray();
    boolean matches = true;
    if (expectedTagType.equals(TAG_UPDATE_MESSAGE_TAG_TYPE_INTEGER_STRING)
        && tagInfoArray[tagId - tagInfoListIDOffset].getType() != TagType.INTEGER) {
      matches = false;
    } else if (expectedTagType.equals(TAG_UPDATE_MESSAGE_TAG_TYPE_FLOAT_STRING)
        && tagInfoArray[tagId - tagInfoListIDOffset].getType() != TagType.FLOAT) {
      matches = false;
    } else if (expectedTagType.equals(TAG_UPDATE_MESSAGE_TAG_TYPE_STRING_STRING)
        && tagInfoArray[tagId - tagInfoListIDOffset].getType() != TagType.STRING) {
      matches = false;
    } else if (expectedTagType.equals(TAG_UPDATE_MESSAGE_TAG_TYPE_BOOLEAN_STRING)
        && tagInfoArray[tagId - tagInfoListIDOffset].getType() != TagType.BOOLEAN) {
      matches = false;
    } else if (expectedTagType.equals(TAG_UPDATE_MESSAGE_TAG_TYPE_DWORD_STRING)
        && tagInfoArray[tagId - tagInfoListIDOffset].getType() != TagType.DWORD) {
      matches = false;
    }
    return matches;
  }

  /**
   * Processes the specified response body of a remote tag update message, and verifies that it
   * matches the expected ID.
   *
   * @param responseBody remote tag update message response body
   * @param expectedId expected ID of remote tag update message response
   * @return integer enumeration indicating result (apply failure, type mismatch, etc)
   * @throws JSONException if unable to parse tag update message response body
   */
  private static int processTagUpdateRequestResponse(String responseBody, String expectedId)
      throws JSONException {
    // Create variable to store result
    int tagUpdateResult = TAG_UPDATE_RESULT_VALUE_SUCCESS;

    // Log tag update request response
    Logger.LOG_DEBUG("Tag Update Request Response Body: " + responseBody);

    // Get JSON object from response body
    JSONTokener jsonTokener = new JSONTokener(responseBody);
    JSONObject responseBodyJson = new JSONObject(jsonTokener);

    // Check if message ID matches
    boolean messageIdMatches =
        responseBodyJson.has(TAG_UPDATE_MESSAGE_ID_KEY)
            && responseBodyJson.getString(TAG_UPDATE_MESSAGE_ID_KEY).equals(expectedId);
    if (!messageIdMatches) {
      Logger.LOG_SERIOUS(
          "A tag update request response was received with a missing or mismatched "
              + "identifier (ID) and will not be processed. Expected: "
              + expectedId
              + " Got: "
              + responseBodyJson.getString(TAG_UPDATE_MESSAGE_ID_KEY));
      tagUpdateResult = TAG_UPDATE_RESULT_VALUE_PAYLOAD_VERIFY_FAIL;
    }

    // Check that JSON RPC version matches expected
    boolean jsonRpcVersionMatches =
        responseBodyJson.has(TAG_UPDATE_MESSAGE_JSON_RPC_KEY)
            && responseBodyJson
                .getString(TAG_UPDATE_MESSAGE_JSON_RPC_KEY)
                .equals(TAG_UPDATE_MESSAGE_JSON_RPC_VERSION);
    if (tagUpdateResult == TAG_UPDATE_RESULT_VALUE_SUCCESS && !jsonRpcVersionMatches) {
      Logger.LOG_SERIOUS(
          "A tag update request response was received with a missing or mismatched JSON "
              + "RPC version and will not be processed. Expected: "
              + TAG_UPDATE_MESSAGE_JSON_RPC_VERSION
              + " Got: "
              + responseBodyJson.getString(TAG_UPDATE_MESSAGE_JSON_RPC_KEY));
      tagUpdateResult = TAG_UPDATE_RESULT_VALUE_PAYLOAD_VERIFY_FAIL;
    }

    // Check if a result or error was returned as response
    if (responseBodyJson.has(TAG_UPDATE_MESSAGE_RESULT_KEY)) {
      // Get result JSON object
      JSONObject resultJsonObject = responseBodyJson.getJSONObject(TAG_UPDATE_MESSAGE_RESULT_KEY);

      // Check for presence of restore previous values behavior flag
      boolean restorePreviousValsOnFault = TAG_UPDATE_MESSAGE_RESTORE_PREVIOUS_ON_FAULT_DEFAULT;
      if (resultJsonObject.has(TAG_UPDATE_MESSAGE_RESTORE_PREVIOUS_ON_FAULT_KEY)) {
        restorePreviousValsOnFault =
            resultJsonObject.getBoolean(TAG_UPDATE_MESSAGE_RESTORE_PREVIOUS_ON_FAULT_KEY);
      }

      // Check for presence of tags object
      if (resultJsonObject.has(TAG_UPDATE_MESSAGE_TAGS_KEY)) {
        // Get tags array
        JSONArray tagsArray = resultJsonObject.getJSONArray(TAG_UPDATE_MESSAGE_TAGS_KEY);

        // Check that all tags are present and correct type
        for (int tagIndex = 0; tagIndex < tagsArray.length(); tagIndex++) {
          // Get tag object
          JSONObject tagObject = tagsArray.getJSONObject(tagIndex);

          if (tagObject.has(TAG_UPDATE_MESSAGE_TAG_NAME_KEY)
              && tagObject.has(TAG_UPDATE_MESSAGE_TAG_TYPE_KEY)
              && tagObject.has(TAG_UPDATE_MESSAGE_TAG_VALUE_KEY)) {
            String tagName = tagObject.getString(TAG_UPDATE_MESSAGE_TAG_NAME_KEY);
            String tagType = tagObject.getString(TAG_UPDATE_MESSAGE_TAG_TYPE_KEY);
            int tagId = TAG_UPDATE_MESSAGE_TAG_ID_EMPTY;

            // Create tag control to check if tag exists and get ID for type lookup
            try {
              TagControl tagControl = new TagControl();
              tagControl.setTagName(tagName);
              tagId = tagControl.getTagId();
            } catch (Exception e) {
              Logger.LOG_SERIOUS(
                  "A tag update request response was received with a tag ("
                      + tagName
                      + ") that does not "
                      + "exist and will not be processed.");
              tagUpdateResult = TAG_UPDATE_RESULT_VALUE_PAYLOAD_MISSING_TAGS;
            }

            // If tag exists, check that tag type matches
            if (tagId != TAG_UPDATE_MESSAGE_TAG_ID_EMPTY) {
              if (!doesTagTypeMatch(tagId, tagType)) {
                Logger.LOG_SERIOUS(
                    "A tag update request response was received with a tag ("
                        + tagName
                        + ") that has mismatched types and will not be processed.");
                tagUpdateResult = TAG_UPDATE_RESULT_VALUE_PAYLOAD_MISMATCHED_TAG_TYPES;
              }
            }
          } else {
            Logger.LOG_SERIOUS(
                "A tag update request response was received with an improperly formatted "
                    + "tag entry. Format is unknown and will not be processed.");
            tagUpdateResult = TAG_UPDATE_RESULT_VALUE_PAYLOAD_VERIFY_FAIL;
          }
        }

        // Apply tag values if no previous errors
        if (tagUpdateResult == TAG_UPDATE_RESULT_VALUE_SUCCESS) {
          tagUpdateResult = applyTagValuesFromJsonArray(tagsArray, restorePreviousValsOnFault);
        }
      } else {
        Logger.LOG_SERIOUS(
            "A tag update request response was received without any tags or an error. "
                + "Format is unknown and will not be processed.");
        tagUpdateResult = TAG_UPDATE_RESULT_VALUE_PAYLOAD_VERIFY_FAIL;
      }
    } else if (responseBodyJson.has(TAG_UPDATE_MESSAGE_ERROR_KEY)) {
      // Get error JSON object
      JSONObject errorJsonObject = responseBodyJson.getJSONObject(TAG_UPDATE_MESSAGE_ERROR_KEY);

      // Check for error code
      if (errorJsonObject.has(TAG_UPDATE_MESSAGE_ERROR_CODE_KEY)) {
        tagUpdateResult = errorJsonObject.getInt(TAG_UPDATE_MESSAGE_ERROR_CODE_KEY);
      } else {
        Logger.LOG_SERIOUS(
            "A tag update request response returned an error but did not "
                + "include an error code!");
      }

      // Check for error message
      String errorMessage = "";
      if (errorJsonObject.has(TAG_UPDATE_MESSAGE_ERROR_MESSAGE_KEY)) {
        errorMessage = errorJsonObject.getString(TAG_UPDATE_MESSAGE_ERROR_MESSAGE_KEY);
      }

      Logger.LOG_SERIOUS(
          "A tag update request response returned the following error ("
              + tagUpdateResult
              + "): "
              + errorMessage);
    } else {
      Logger.LOG_SERIOUS(
          "A tag update request response was received without a result or an error. Format "
              + "is unknown and will not be processed.");
      tagUpdateResult = TAG_UPDATE_RESULT_VALUE_PAYLOAD_VERIFY_FAIL;
    }

    return tagUpdateResult;
  }

  /**
   * Sends a remote tag update request to the configure tag update endpoint on Thingworx with the
   * specified trigger value and information string.
   *
   * @param triggerValue trigger value (integer enumeration)
   * @param triggerInfoString trigger information string
   */
  public static void sendTagUpdateRequest(String triggerValue, String triggerInfoString) {
    // Set result tag to HTTP started
    setTagUpdateResultTagValue(TAG_UPDATE_RESULT_VALUE_HTTP_STARTED);

    // Get message ID
    String messageId = String.valueOf(tagUpdateIdCounter++);

    // Build JSON request body
    int tagUpdateResult = TAG_UPDATE_RESULT_VALUE_SUCCESS;
    String json = "{}";
    try {
      JSONObject requestRootObject = new JSONObject();
      JSONObject requestParamsObject = new JSONObject();
      requestParamsObject.put(TAG_UPDATE_MESSAGE_STRING_INFO_KEY, triggerInfoString);
      requestRootObject.put(TAG_UPDATE_MESSAGE_JSON_RPC_KEY, TAG_UPDATE_MESSAGE_JSON_RPC_VERSION);
      requestRootObject.put(TAG_UPDATE_MESSAGE_METHOD_KEY, triggerValue);
      requestRootObject.put(TAG_UPDATE_MESSAGE_PARAMS_KEY, requestParamsObject);
      requestRootObject.put(TAG_UPDATE_MESSAGE_ID_KEY, messageId);
      json = requestRootObject.toString();
    } catch (JSONException e) {
      Logger.LOG_SERIOUS("An error occurred while building a tag update request message!");
      Logger.LOG_EXCEPTION(e);
      tagUpdateResult = TAG_UPDATE_RESULT_VALUE_EWON_ERROR;
    }

    // Build HTTP request if no error
    String tagUpdateRequestEndpointFullUrl = "";
    String tagUpdateRequestHeader = "";
    if (tagUpdateResult == TAG_UPDATE_RESULT_VALUE_SUCCESS) {
      try {
        tagUpdateRequestEndpointFullUrl =
            TWConnectorMain.getConnectorConfig().getThingworxTagUpdateFullUrl();
        tagUpdateRequestHeader =
            "Accept="
                + TAG_UPDATE_REQUEST_CONTENT_TYPE
                + "&Content-Type="
                + TAG_UPDATE_REQUEST_CONTENT_TYPE
                + "&flexy-api-version="
                + TAG_UPDATE_REQUEST_FLEXY_API_VERSION
                + "&appKey="
                + TWConnectorMain.getConnectorConfig().getThingworxAppKey();
      } catch (Exception e) {
        Logger.LOG_CRITICAL("Unable to get configuration information for tag update request!");
        Logger.LOG_EXCEPTION(e);
        tagUpdateResult = TAG_UPDATE_RESULT_VALUE_EWON_ERROR;
      }
    }

    // Perform HTTP request if no error
    String response = null;
    if (tagUpdateResult == TAG_UPDATE_RESULT_VALUE_SUCCESS) {
      try {
        response =
            HttpUtils.httpPost(tagUpdateRequestEndpointFullUrl, tagUpdateRequestHeader, json);
        if (response != null) {
          if (response.equals(HttpUtils.EWON_ERROR_STRING_RESPONSE)) {
            tagUpdateResult = TAG_UPDATE_RESULT_VALUE_EWON_ERROR;
          } else if (response.equals(HttpUtils.AUTH_ERROR_STRING_RESPONSE)
              || response.equals(HttpUtils.CONNECTION_ERROR_STRING_RESPONSE)) {
            tagUpdateResult = TAG_UPDATE_RESULT_VALUE_CONNECTION_ERROR;
          }
        }
      } catch (Exception e) {
        Logger.LOG_CRITICAL(
            "An error occurred while performing an HTTP POST to Thingworx. Tag update "
                + "request may not be completed!");
        Logger.LOG_EXCEPTION(e);
        tagUpdateResult = TAG_UPDATE_RESULT_VALUE_EWON_ERROR;
      }
      Logger.LOG_DEBUG("Thingworx HTTP POST response (tag update request): " + response);
    }

    // Process response if successful
    if (tagUpdateResult == TAG_UPDATE_RESULT_VALUE_SUCCESS) {
      try {
        tagUpdateResult = processTagUpdateRequestResponse(response, messageId);
      } catch (JSONException e) {
        Logger.LOG_SERIOUS(
            "Unable to process tag update request response as JSON. Check "
                + "that your tag update endpoint and associated app key are configured correctly!");
        Logger.LOG_EXCEPTION(e);
        tagUpdateResult = TAG_UPDATE_RESULT_VALUE_PAYLOAD_VERIFY_FAIL;
      }
    }

    // Reset trigger tags and set result tag
    resetTagUpdateTriggerTags();
    setTagUpdateResultTagValue(tagUpdateResult);
  }
}
