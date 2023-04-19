# Ewon Thingworx Connector

The Ewon Thingworx Connector package provides a connector-based solution to Thingworx for linking Ewon devices using the Talk2M cloud and/or Ewon Flexy devices using a direct data path with the Flexy Java application.

There are two components that make up the Ewon Thingworx Connector, a Thingworx project and an Ewon Flexy Java application.

## Table of Contents

1. [Data Paths](#data-paths)
2. [Thingworx Project Component](#thingworx-project-component)
   1. [Required Thingworx Version](#required-thingworx-version)
   2. [Thingworx Project Installation](#thingworx-project-installation)
   3. [Thing Information](#thing-information)
      1. [ConnectorHost](#connectorhost)
         1. [ConnectorHost Properties](#connectorhost-properties)
         2. [ConnectorHost Services](#connectorhost-services)
      2. [GenericEwonDevice](#genericewondevice)
         1. [GenericEwonDevice Properties](#genericewondevice-properties)
         2. [GenericEwonDevice Services](#genericewondevice-services)
         3. [GenericEwonDeviceTalk2M](#genericewondevicetalk2m)
            1. [GenericEwonDeviceTalk2M Services](#genericewondevicetalk2m-services)
         4. [GenericEwonDeviceDirect](#genericewondevicedirect)
            1. [GenericEwonDeviceDirect Properties](#genericewondevicedirect-properties)
            2. [GenericEwonDeviceDirect Services](#genericewondevicedirect-services)
         5. [GenericEwonDeviceDirectStandalone](#genericewondevicedirectstandalone)
            1. [GenericEwonDeviceDirectStandalone Properties](#genericewondevicedirectstandalone-properties)
            2. [GenericEwonDeviceDirectStandalone Services](#genericewondevicedirectstandalone-services)
      3. [GenericEwonDeviceValueStream](#genericewondevicevaluestream)
      4. [M2Web-Live-Data](#m2web-live-data)
   4. [Creating an Application Key](#creating-an-application-key)
   5. [Common Errors](#common-errors)
      1. [Error Executing Event Handler 'runConnector' for event Type.Thing:Entity.ConnectorHost:Event.Timer](#error-executing-event-handler-runconnector-for-event-typethingentityconnectorhosteventtimer)
      2. [Upon attempting to delete/input data into a Thing: "error: Thing X does not Exist"](#upon-attempting-to-deleteinput-data-into-a-thing-error-thing-x-does-not-exist)
3. [Flexy Java Application Component](#flexy-java-application-component)
   1. [Application Component Installation](#application-component-installation)
   2. [Configuration](#configuration)
      1. [Thingworx Full URL](#thingworx-full-url)
      2. [App Key](#app-key)
      3. [Log Level](#log-level)
      4. [Queue Diagnostic Tags Enabled](#queue-diagnostic-tags-enabled)
      5. [Queue Enable String History](#queue-enable-string-history)
      6. [Queue Data Poll Size](#queue-data-poll-size)
      7. [Queue Data Poll Interval](#queue-data-poll-interval)
      8. [Payload Maximum Data Points](#payload-maximum-data-points)
      9. [Payload Send Interval (Millis)](#payload-send-interval-millis)
      10. [Thingworx Tag Update URL](#thingworx-tag-update-url)
      11. [Max Historical Buffer Fall Behind Mins](#max-historical-buffer-fall-behind-mins)
   3. [Telemetry](#telemetry)
      1. [Data Source](#data-source)
         1. [Tag Eligibility](#tag-eligibility)
         2. [Tag Data Types](#tag-data-types)
            1. [String Tag History](#string-tag-history)
   4. [Runtime](#runtime)
      1. [Application Control Tag](#application-control-tag)
      2. [Tag Updates from Thingworx](#tag-updates-from-thingworx) 
         1. [Request Trigger and Result Tags](#request-trigger-and-result-tags)
         2. [Custom Service Request Format](#custom-service-request-format)
         3. [Custom Service Response Format](#custom-service-response-format)
      3. [Log Output](#log-output)
         1. [Configured Logging Level](#configured-logging-level)
         2. [Logging Performance](#logging-performance)
         3. [Adding Log Output](#adding-log-output)
   5. [Development Environment](#development-environment)
      1. [Source Code](#source-code)
         1. [Cloning](#cloning)
         2. [Existing Thread.sleep() Invocations](#existing-threadsleep-invocations)
      2. [Javadocs](#javadocs)
      3. [Releases](#releases)
         1. [Automatic Startup (jvmrun)](#automatic-startup-jvmrun)
4. [Support](#support)
   1. [Reporting Bugs and Issues](#reporting-bugs-and-issues)
   2. [Flexy Support](#flexy-support)
   3. [Development Environment Support](#development-environment-support)

## Data Paths

The Ewon Thingworx Connector supports two data paths for receiving data from Ewon devices.

The first data path uses the Ewon Talk2M cloud and downloads data to Thingworx on a set timer interval.

The second data path uses a Flexy Java application to send data directly to Thingworx and does not require the use of any cloud platforms for data storage. It can provide the secure transfer of data on a local network.

Both data paths require the installation of the Thingworx Project Component, but the second data path also requires the installation of the Flexy Java Application Component.

## Thingworx Project Component

The Thingworx project component must be installed and configured for both data paths.

### Required Thingworx Version

The Flexy Thingworx Connector currently supports Thingworx 9.1-b10877 or later.

### Thingworx Project Installation

To install the connector components for Thingworx, navigate to the lower left-hand corner of the screen, click the "Import/Export" button, then click "Import".

![Image of Thingworx Import/Export Buttons](thingworx-import/img/importButton.PNG)

Configure following import options:
1. Check "Use default persistence provider".
2. Select Entities.xml from file system.

Click the "Import" button to complete the import.

![Image of Thingworx Import Dialog](thingworx-import/img/importingWindow.PNG)

**NOTE: Talk2M functionality has been disabled by default and must be configured prior to use.**

Navigate to the EwonThingworxConnector project by going to the "Browse" menu, then selecting the "Projects" tab.

Click the EwonThingworxConnector project to display its available entities.

![Image of Thingworx Browse Projects Window](thingworx-import/img/browseProjects.PNG)

Open the ConnectorHost timer Thing by clicking it under the list of available entities, then selecting "Edit".

![Image of ConnectorHost in Available Entities of EwonThingworxConnector Project](thingworx-import/img/connectorHostThing.PNG)

On the General Information page, perform the following configuration modifications:

1. Configure "Run As User" to be something other than Administrator.
    
   1. The selected user must posses the permissions necessary for creating and modifying Things, and creating and updating Thing properties.

2. Configure "Update Rate" to the interval at which the connector should run (in milliseconds).

   1. This value is the interval at which data is downloaded from Talk2M/DataMailbox. If you are not using Talk2M functionality, this value does not need to be changed.

![Image of ConnectorHost Run As User and Update Rate Options](thingworx-import/img/generalConfiguration.PNG)

Navigate to the "Properties and Alerts" tab in ConnectorHost and populate the following property values:

1. *disableTalk2M*: When set to true, this disables the Talk2M/DataMailbox functionality. For direct data path users, this option may be set to `true`, but for Talk2M/DataMailbox data path users, this option must be set to `false`.

2. *scriptTimeoutSeconds*: This should match the scriptTimeoutSeconds value in the Thingworx platform-settings.json file. If you have not modified it, the default value is 30.

3. *talk2MAccount*: The account name for login to Talk2M.

4. *talk2MDeveloperID*: A valid Talk2M developer ID.

5. *talk2MPassword*: The account password for login to Talk2M.

6. *talk2MToken*: An account API token for access to Talk2M.

7. *talk2MUsername*: The account username for login to Talk2M.

8. *useDataChangeTypeAlways*: When set to true, the DataChangeType field of newly created properties will be set to 'Always'. The default value is `false`, which will result in newly created properties using the DataChangeType of 'Value'.

9. *useHyphens*: When set to true, additional organization is performed on Thing properties with a prefix in the name (i.e. PREFIX-\[tagname\]).

Additional and more detailed information about the properties of ConnectorHost can be found below in Thing Information > ConnectorHost > Properties.

![Image of ConnectorHost Properties and Alerts Window](thingworx-import/img/propertiesScreen.PNG)

### Thing Information

#### ConnectorHost

ConnectorHost is a timer Thing and triggers a check for new Talk2M data on its configured interval. In addition to checking Talk2M data on a set interval, ConnectorHost also provides the service required for direct data path connections.

##### ConnectorHost Properties

1. *disableTalk2M*: A boolean property that controls the download and processing of data from the Ewon Talk2M cloud. The default value is `true`, thus this property must be changed to `false` for Talk2M functionality to work.

2. *lastTransactionId*: An integer property that the Talk2M services use to track the data that has been previously received. This property's value should not be manually changed and doing so may result in duplicate data entries or other unexpected behavior.

3. *lastUpdateTime*: A date/time property that the Talk2M and direct data path services use to track the last time data was successfully received. This property's value should not be manually changed and doing so may result in the delay of data without warning or other unexpected behavior. 

4. *scriptTimeoutSeconds*: An integer property that the Talk2M services use as a maximum time for fetching data. After the Talk2M data sync script has run for scriptTimeoutSeconds seconds, that iteration will be stopped. The value of this property should be equal to the configured scriptTimeoutSeconds value in the Thingworx platform-settings.json file. If you have not modified that file, the default is 30.

5. *talk2MAccount*: A string property that the Talk2M services use for Talk2M account authentication. The value of this property should be populated with the account name for your Talk2M account.

6. *talk2MDeveloperID*: A string property that the Talk2M services use for Talk2M developer identification. The value of this property should be populated with a valid Talk2M developer ID. If you do not have a Talk2M developer ID, you can apply for one at https://developer.ewon.biz/content/talk2m-developer-id.

7. *talk2MPassword*: A string property that the Talk2M services use for Talk2M account authentication. The value of this property should be populated with the account password for your Talk2M account.

8. *talk2MToken*: A string property that the Talk2M services use for Talk2M account authentication. The value of this property should be populated with a valid Talk2M account token from your Talk2M account. More information about Talk2M account API tokens can be found at https://onlinehelp.ewon.biz/ecatcher/6.6/pro/en/index.html?token-management.htm.

9. *talk2MUsername*: A string property that the Talk2M services use for Talk2M account authentication. The value of this property should be populated with the account username for your Talk2M account.

10. *updateTimeoutMinutes*: An integer property that the Talk2M and direct data path services use as the maximum number of minutes since last data update before showing a warning in the logs.

11. *useDataChangeTypeAlways*: A boolean property that the Talk2M and direct data path services use when creating new Thing properties. The default value is `false`. If the value is `true`, the DataChangeType field of newly created properties will be set to 'Always'.

12. *useHyphens*: A boolean property that controls the organization of Ewon devices and their tags. When tags use a hyphen prefix (i.e. PREFIX-\[tagname\]), a Thing will be created for each prefix (i.e. DEVICE-PREFIX) and each of the properties will be added. The default value is `false`.

##### ConnectorHost Services

1. *InsertDataPoint*: Used by the Talk2M and direct data paths to insert a datapoint to the respective Thing and Thing property.

2. *MainExecution*: Used by the Talk2M data path for calling the Talk2MSyncData service within the script timeout period. This service is invoked by the ConnectorHost timer on its configured interval.

3. *ProcessTimeSinceUpdate*: Used by the Talk2M and direct data path services to track the time since the last received data update. This service is invoked by the MainExecution and TakeInfo services.

4. *TakeInfo*: Used by the direct data path to ingest telemetry messages from the Flexy Java Application component. This service is invoked by the Flexy Java Application component using the Thingworx REST API.

5. *Talk2MSyncData*: Used by the Talk2M data path for downloading a transaction of data points from Talk2M/DataMailbox. It requests only data it has not previously recieved using the stored value of lastTransactionId.

6. *AddNewDirectStandaloneDevice*: Used to create a new Thing for connecting an Ewon device using the standalone direct data path. Note: This service is for convenience, and application key permissions must still be manually configured. This service returns JSON containing the generated Thing name, data connection URL, application key name and value.

#### GenericEwonDevice

GenericEwonDevice is a Thing template that applies to all Ewon device Things created by the connector and contains common properties that are used by services in the GenericEwonDeviceTalk2M, GenericEwonDeviceDirect, and GenericEwonDeviceDirectStandalone Thing Templates.

##### GenericEwonDevice Properties

1. *ewonDevicePassword*: The password of the referenced Ewon device. The default value is 'adm' and should be changed if the Ewon password is different. The value of this property is used by the services in the GenericEwonDeviceTalk2M, GenericEwonDeviceDirect, and GenericEwonDeviceDirectStandalone Thing Templates.

2. *ewonDeviceUsername*: The username of the referenced Ewon device. The default value is 'adm' and should be changed if the Ewon username is different. The value of this property is used by the services in the GenericEwonDeviceTalk2M, GenericEwonDeviceDirect, and GenericEwonDeviceDirectStandalone Thing Templates.

##### GenericEwonDevice Services

1. *WriteBooleanTagGenericService*: A generic service used by the GenericEwonDeviceTalk2M, GenericEwonDeviceDirect, and GenericEwonDeviceDirectStandalone Thing Templates for writing a value to a boolean tag on the Ewon Device.

2. *WriteDwordTagGenericService*: A generic service used by the GenericEwonDeviceTalk2M, GenericEwonDeviceDirect, and GenericEwonDeviceDirectStandalone Thing Templates for writing a value to a DWORD tag on the Ewon Device.

3. *WriteFloatTagGenericService*: A generic service used by the GenericEwonDeviceTalk2M, GenericEwonDeviceDirect, and GenericEwonDeviceDirectStandalone Thing Templates for writing a value to a float tag on the Ewon Device.

4. *WriteIntegerTagGenericService*: A generic service used by the GenericEwonDeviceTalk2M, GenericEwonDeviceDirect, and GenericEwonDeviceDirectStandalone Thing Templates for writing a value to a integer tag on the Ewon Device.

5. *WriteStringTagGenericService*: A generic service used by the GenericEwonDeviceTalk2M, GenericEwonDeviceDirect, and GenericEwonDeviceDirectStandalone Thing Templates for writing a value to a string tag on the Ewon Device.

6. *ReadAllTagsLiveGenericService*: A generic service used by the GenericEwonDeviceTalk2M, GenericEwonDeviceDirect, and GenericEwonDeviceDirectStandalone Thing Templates for reading all live tag values on the Ewon Device.

7. *ReadBooleanTagLiveGenericService*: A generic service used by the GenericEwonDeviceTalk2M, GenericEwonDeviceDirect, and GenericEwonDeviceDirectStandalone Thing Templates for reading a boolean tag's live value on the Ewon Device.

8. *ReadDwordTagLiveGenericService*: A generic service used by the GenericEwonDeviceTalk2M, GenericEwonDeviceDirect, and GenericEwonDeviceDirectStandalone Thing Templates for reading a DWORD tag's live value on the Ewon Device.

9. *ReadFloatTagLiveGenericService*: A generic service used by the GenericEwonDeviceTalk2M, GenericEwonDeviceDirect, and GenericEwonDeviceDirectStandalone Thing Templates for reading a float tag's live value on the Ewon Device.

10. *ReadIntegerTagLiveGenericService*: A generic service used by the GenericEwonDeviceTalk2M, GenericEwonDeviceDirect, and GenericEwonDeviceDirectStandalone Thing Templates for reading an integer tag's live value on the Ewon Device.

11. *ReadStringTagLiveGenericService*: A generic service used by the GenericEwonDeviceTalk2M, GenericEwonDeviceDirect, and GenericEwonDeviceDirectStandalone Thing Templates for reading a string tag's live value on the Ewon Device.

##### GenericEwonDeviceTalk2M

The GenericEwonDeviceTalk2M device template is for Ewon devices which connect to Thingworx using the Talk2M data path.

The GenericEwonDeviceTalk2M device template is based on the GenericEwonDevice device template, and inherits all of its services and properties.

###### GenericEwonDeviceTalk2M Services

1. *SendEwonOffline*: Sends the referenced Ewon device offline when using a triggered connection, such as a cellular network. More information about triggered connections can be found at https://www.ewon.biz/e-learning/library/cosy-131/remote-connection#:~:text=Triggered%20Connection%3A%20wake%20up%20%26%20put,when%20the%20user%20needs%20it.

2. *WakeEwonDevice*: Wakes up the referenced Ewon device when using a triggered connection, such as a cellular network. More information about triggered connections can be found at https://www.ewon.biz/e-learning/library/cosy-131/remote-connection#:~:text=Triggered%20Connection%3A%20wake%20up%20%26%20put,when%20the%20user%20needs%20it.

3. *WriteBooleanTag*: Writes the value of a boolean tag on the Ewon device.

4. *WriteDwordTag*: Writes the value of a DWORD tag on the Ewon device.

5. *WriteFloatTag*: Writes the value of a float tag on the Ewon device.

6. *WriteIntegerTag*: Writes the value of a integer tag on the Ewon device.

7. *WriteStringTag*: Writes the value of a string tag on the Ewon device.

8. *ReadAllTagsLive*: Reads the live values of all tags on the Ewon device.

9. *ReadBooleanTagLive*: Reads the live value of a boolean tag on the Ewon device.

10. *ReadDwordTagLive*: Reads the live value of a DWORD tag on the Ewon device.

11. *ReadFloatTagLive*: Reads the live value of a float tag on the Ewon device.

12. *ReadIntegerTagLive*: Reads the live value of an integer tag on the Ewon device.

13. *ReadStringTagLive*: Reads the live value of a string tag on the Ewon device.

##### GenericEwonDeviceDirect

The GenericEwonDeviceDirect device template is for Ewon devices which connect to Thingworx using the direct data path and ConnectorHost's TakeInfo service.

The GenericEwonDeviceDirect device template is based on the GenericEwonDevice device template, and inherits all of its services and properties.

###### GenericEwonDeviceDirect Properties

1. *talk2MDeviceName*: A string property that the GenericEwonDeviceDirect services use to identify the referenced Ewon on Talk2M. Devices connected using the direct data path may have a different name in Talk2M than what appears in Thingworx, thus it must be explicitly set for direct data path Things.

###### GenericEwonDeviceDirect Services

1. *SendEwonOffline*: Sends the referenced Ewon device offline when using a triggered connection, such as a cellular network. More information about triggered connections can be found at https://www.ewon.biz/e-learning/library/cosy-131/remote-connection#:~:text=Triggered%20Connection%3A%20wake%20up%20%26%20put,when%20the%20user%20needs%20it.

2. *WakeEwonDevice*: Wakes up the referenced Ewon device when using a triggered connection, such as a cellular network. More information about triggered connections can be found at https://www.ewon.biz/e-learning/library/cosy-131/remote-connection#:~:text=Triggered%20Connection%3A%20wake%20up%20%26%20put,when%20the%20user%20needs%20it.

3. *WriteBooleanTag*: Writes the value of a boolean tag on the Ewon device.

4. *WriteDwordTag*: Writes the value of a DWORD tag on the Ewon device.

5. *WriteFloatTag*: Writes the value of a float tag on the Ewon device.

6. *WriteIntegerTag*: Writes the value of a integer tag on the Ewon device.

7. *WriteStringTag*: Writes the value of a string tag on the Ewon device.

8. *ReadAllTagsLive*: Reads the live values of all tags on the Ewon device.

9. *ReadBooleanTagLive*: Reads the live value of a boolean tag on the Ewon device.

10. *ReadDwordTagLive*: Reads the live value of a DWORD tag on the Ewon device.

11. *ReadFloatTagLive*: Reads the live value of a float tag on the Ewon device.

12. *ReadIntegerTagLive*: Reads the live value of an integer tag on the Ewon device.

13. *ReadStringTagLive*: Reads the live value of a string tag on the Ewon device.

##### GenericEwonDeviceDirectStandalone

The GenericEwonDeviceDirectStandalone Thing Template is for Ewon devices to connect to Thingworx using the direct data path with an independent application key and service endpoint.

The GenericEwonDeviceDirectStandalone Thing Template is based on the GenericEwonDeviceDirect Thing Template, and inherits all of its services and properties.

###### GenericEwonDeviceDirectStandalone Properties

1. *lastUpdateTime*: A date/time property that is updated to the current time when a data update is received from the Ewon device.

###### GenericEwonDeviceDirectStandalone Services

1. *InsertDataPoint*: Used by the TakeInfo service to insert a datapoint to its respective property on Thingworx.

2. *ProcessTimeSinceUpdate*: Used by the TakeInfo service to update the lastUpdateTime property which tracks when a data update was last received.

3. *TakeInfo*: Used by the Ewon device to ingest telemetry messages from the Flexy Java Application component. This service is invoked by the Flexy Java Application component using the Thingworx REST API.


#### GenericEwonDeviceValueStream

GenericEwonDeviceValueStream is a generic value stream Thing that is used for storing the values of its respective logged Thing properties.

#### M2Web-Live-Data 

M2Web-Live-Data is a Thingworx Data Shape used by the GenericEwonDevice Thing Template for reading and parsing live tag data returned from the M2Web API.

### Creating an Application Key
To create an app key, from the “Browse” tab, navigate to the “Application Keys” page under “Security” in the Thingworx Composer. Click “+ New” and enter a name for the app key, then provide a user name reference to a user that has permission to contact Thingworx via the REST API (the default Administrator account for example). Click “Save” to save the new app key to Thingworx. After the save has completed, click to open the app key, and select the “Permissions” tab. Under “Visibility,” add “Everyone” to the list of Org and Org Units. Under “Run Time,” add the user you set as the user name reference to the list of Users and Groups. Click “Save” to apply the changes, then click “Done” to return to the app key’s general information page. The “Key ID” value in the app key’s general information is the app key value.

### Common Errors

#### Error Executing Event Handler 'runConnector' for event Type.Thing:Entity.ConnectorHost:Event.Timer

This error is most commonly caused by invalid permissions for the ConnectorHost Thing.
To test, go to "ConnectorHost">"General Information" and change the value of "Run As User" to Administrator.

If the error resolves, there was an issue with the previous "Run As User" permission configuration.

#### Upon attempting to delete/input data into a Thing: "error: Thing X does not Exist"

This is an issue with Thingworx not being properly synced with the database provider.
Restarting Tomcat should fix the issue. For information on how to do this visit https://stackoverflow.com/questions/12622534/tomcat-restart-webapp-from-command-line

## Flexy Java Application Component
The Flexy Java application component must be installed for the direct data path configuration.

### Application Component Installation
Using FTP, transfer the Flexy Java application \(.jar\) and jvmrun files to the /usr/ directory of the Ewon. Then, reboot the Ewon. On application startup, a configuration file will automatically be created with default values.
- The Flexy Java application \(.jar\) is located in the /target folder.
- The jvmrun file is located in the /starting-files folder.

A Thingworx application key is required to authenticate requests made by the Ewon to Thingworx, and is stored in the configuration file outlined in the [Configuration](#configuration) section. To generate an application key in Thingworx, please refer to the [Creating an Application Key](#creating-an-application-key) section.

### Configuration
At startup of the Flexy Java application component, it checks for a configuration file in the /usr/ directory of the Ewon. If one is not present, the application will automatically create one with default values.

After the Flexy Java application component has been installed and started, the configuration should be modified to contain the proper values for AppKey and Thingworx Base URL for your Thingworx instance.
#### Thingworx Base URL 
This parameter in the config file should be changed to the base URL for the target Thingworx REST API. Users should pay close attention to the scheme designated in the URL. Specifying the scheme "https" will ensure all traffic to the server is encrypted over TLS. For example, setting the ThingworxBaseUrl parameter to "https://example.com:8443" will create a connection to host "example.com" on port 8443 using TLS/SSL. It's possible to only specify the IP address of the Thingworx instance. This configuration will assume "http" scheme and communication will not be encrypted. 

After the Flexy Java application component has been installed and started, the configuration should be modified to contain the proper values for AppKey and Thingworx Full URL for your Thingworx instance.

![Configuration Example](https://github.com/hms-networks/flexy-thingworx-connector/blob/main/images/ExampleConfig.PNG?raw=true)

#### Thingworx Full URL
This parameter in the config file should be changed to the full URL for the target Thingworx REST API. Users should pay close attention to the scheme designated in the URL. Specifying the scheme "https" will ensure all traffic to the server is encrypted over TLS. For example, setting the ThingworxFullUrl parameter to "https://example.com:8443/Thingworx/Things/ConnectorHost/Services/TakeInfo" will create a connection to host "example.com" on port 8443 using TLS/SSL. It's possible to specify the IP address of the Thingworx instance instead of a domain name. This configuration will assume "http" scheme and communication will not be encrypted.

#### App Key
This should match the app key for the Thingworx instance.

#### Log Level
The Thingworx connector uses the HMS Solution Center logging library for application logging to the Ewon Flexy's realtime logs. See [Configured Log Level](#configured-logging-level) for more information.

#### Queue Diagnostic Tags Enabled
Optional parameter to enable and monitor a set of diagnostic tags for the historical data queue. These tags are automatically created and are used to monitor the health of the historical data queue by displaying a heartbeat with the number of times the queue has been accessed, a trigger to reset the queue time tracker, and the number of seconds which the queue is running behind by.

#### Queue Enable String History
Optional parameter to override the default boolean flag indicating if string history data should be retrieved from the queue. String history requires an additional EBD call in the underlying queue library, and will take extra processing time, especially in installations with large string tag counts.  If no value is specified in the configuration file, the value will be read from QUEUE_DATA_STRING_HISTORY_ENABLED_DEFAULT from "src/main/java/com/hms_networks/americas/sc/thingworx/TWConnectorConsts.java".

#### Queue Data Poll Size
Optional parameter to override the default data poll size (in minutes) of each data queue poll. Changing this will modify the amount of data checked during each poll interval. If no value is specified in the configuration file, the value will be read from QUEUE_DATA_POLL_SIZE_MINS_DEFAULT from "src/main/java/com/hms_networks/americas/sc/thingworx/TWConnectorConsts.java".

*Note: The queue data poll size is doubled when the queue is running behind to allow it to catch up faster.*

#### Queue Data Poll Interval
Optional parameter to override the default data poll interval (in milliseconds) to poll the historical data queue.  If no value is specified in the configuration file, the value will be read from QUEUE_DATA_POLL_INTERVAL_MILLIS_DEFAULT from "src/main/java/com/hms_networks/americas/sc/thingworx/TWConnectorConsts.java".

#### Payload Maximum Data Points
Optional parameter to control the maximum number of data points which can be added to data payloads which are sent to Thingworx. If no value is specified in the configuration file, the value will be read from CONNECTOR_CONFIG_DEFAULT_PAYLOAD_MAX_DATA_POINTS in "src/main/java/com/hms_networks/americas/sc/thingworx/TWConnectorConsts.java".

#### Payload Send Interval (Millis)
Optional parameter to control the interval at which data payloads are sent to Thingworx. The application will wait (at minimum) for this interval before sending each payload to allow for additional data points to be added. This value does not affect the intervals at which tag data is recorded or processed, only the interval at which processed data is sent to Thingworx. If no value is specified in the configuration file, the value will be read from CONNECTOR_CONFIG_DEFAULT_PAYLOAD_SEND_INTERVAL_MILLIS in "src/main/java/com/hms_networks/americas/sc/thingworx/TWConnectorConsts.java".

#### Thingworx Tag Update URL
Optional parameter which is used to set the URL for the custom tag update functionality service described in the [Tag Updates from Thingworx](#tag-updates-from-thingworx) section.

#### Max Historical Buffer Fall Behind Mins
Optional parameter to control the maximum number of minute(s) which the historical data queue can fall behind by. If the queue falls behind by this amount, only data points which are within the maximum fall behind time will be sent to Thingworx. If no value is specified in the configuration file, the value will be read from CONNECTOR_CONFIG_DEFAULT_MAX_HIST_BUF_FALL_BEHIND_MINS from "src/main/java/com/hms_networks/americas/sc/thingworx/TWConnectorConsts.java". This functionality may be disabled by setting the value to -1.

### Telemetry

#### Data Source
The telemetry data that is sent to Thingworx is gathered from the internal Ewon Flexy historical logs. These logs act as a first-in, first-out (FIFO) buffer, meaning that Thingworx will receive the oldest data points from the logs first. The historical logs are stored in nonvolatile memory and prevent against data point loss from connectivity issues or power loss. The historical log can store up to 900,000 data points, depending on the memory configuration, before data points are dropped.

The default configuration of the Ewon Flexy is to allocate 6 MB of memory to the historical log and 29 MB to the /usr directory on the file system. Increasing the size of the historical log will result in a proportional decrease of the size of the /usr directory. This setting can be configured on the Ewon under Setup > System > Storage > Memory Settings.

Note: *This setting should be configured prior to installing the application, as a complete format of the Ewon is necessary to apply this setting.*

![Data Source Flow Chart](https://github.com/hms-networks/flexy-thingworx-connector/blob/main/images/DataSource.PNG?raw=true)

##### Tag Eligibility
Each tag that should be sent to Thingworx must have historical logging enabled. The historical logging interval configured for a tag sets the interval it will be recorded and sent to Thingworx. For information on the Ewon’s historical logging functionality, and how to set it up, please visit [https://www.ewon.biz/technicalsupport/pages/data-services/data-logging](https://www.ewon.biz/technicalsupport/pages/data-services/data-logging).

In addition to historical logging being enabled, the Ewon Thingworx Connector application uses tag groups to determine which tags are to be sent to Thingworx. There are four tag groups, A, B, C, and D. Any tag assigned to one of the four tag groups will be sent to Thingworx, but tags that have not been assigned a tag group will be ignored.

##### Tag Data Types
The Ewon Thingworx connector supports the following Ewon tag data types:

- Integer
- Boolean
- Floating Point
- DWORD
- String

###### String Tag History
The string data type requires an additional EBD (export block descriptor) call, which requires additional processing power. It is recommended that the string data type be disabled if string tags will not be used. It can be enabled or disabled as described in the [Queue Enable String History](#queue-enable-string-history) section of the Flexy Java Application Component's [Configuration](#configuration) section.

Additionally, the default configuration of the Ewon Flexy is to exclude string tags from the historical log. String tag historization can be enabled on the Ewon under *Setup > System > Storage > Memory Settings*.

Note: *This setting should be configured prior to installing the application, as a complete format of the Ewon is necessary to apply this setting.*

### Runtime

#### Application Control Tag
The “ThingworxControl” tag allows for a user to shut down the application while the Flexy is running. This tag must be created, by a user, as a Boolean “MEM” tag on the Ewon with the name “ThingworxControl”. The application will cyclically poll the “ThingworxControl” tag value in TWConnectorMain.java and shut down the application when the value is set to one. This reduces the CPU load of the Flexy and allows for maintenance to be completed on the unit. The application can only be stopped in the telemetry portion of the application and shut down during initialization is not permitted. The name of this tag can be modified by changing the value of CONNECTOR_CONTROL_TAG_NAME in "src/main/java/com/hms_networks/americas/sc/thingworx/TWConnectorConsts.java".

#### Tag Updates from Thingworx
The connector can optionally be configured to request tag updates from a custom Thingworx service when triggered using the proper tags and configuration.

To enable this functionality, a set of trigger tags on the Ewon must be configured. The custom Thingworx tag update service URL must be properly set in the connector configuration file, as described in the [Thingworx Tag Update URL](#thingworx-tag-update-url) section.

The custom Thingworx tag update service must adhere to the outlined [Request Format](#request-format) and [Response Format](#response-format) defined below.

##### Request Trigger and Result Tags

1. _RemoteTagUpdateTriggerEnum_
   - This tag is an integer enumeration which can be used by the tag update service to return different results depending on its value.

     | Enumeration Value | Meaning                                    | 
     | :---------------: | :----------------------------------------: |
     | 0                 | No Update Triggered                        |
     | 1+                | Update Triggered (Value passed to service) |

   - Setting the value of this tag to a number which is not equal to zero (0) immediately triggers the tag update request using the enumeration value and string value specified in _RemoteTagUpdateTriggerString_.
   - The value of this tag is reset to its initial value (0) after the remote tag update request has been completed.


2. _RemoteTagUpdateTriggerString_
   - This tag is a string which can be used to pass additional information to the custom tag update service in many formats.
   - The value of this tag is reset to its initial value ("") after the remote tag update request has been completed.


3. _RemoteTagUpdateResultEnum_
   - The value of this tag is an integer enumeration which indicates the result of the most recent remote tag update. 
     
     | Enumeration Value                          | Meaning                              | 
     | :----------------------------------------: | :----------------------------------: |
     | 0                                          | Initial Value                        |
     | 1                                          | HTTP Request Started                 |
     | 2                                          | Success (Normal)                     |
     | 3                                          | Connection Failure/Error             |
     | 4                                          | Ewon Failure/Error                   |
     | 5                                          | Payload Verification Failure         |
     | 6                                          | Payload Apply Failure                |
     | 7                                          | Payload Missing Tags on Ewon Failure |
     | 8                                          | Payload Bad/Mismatched Types Failure |
     | -32700, -32600 to -32603, -32000 to -32099 | Service Error (JSON RPC 2.0)         |

     If an error message was included with the JSON RPC error code, it will be displayed in the connector application's log.
     For more information about JSON RPC error codes, please refer to [https://www.jsonrpc.org/specification#error_object](https://www.jsonrpc.org/specification#error_object).

##### Custom Service Request Format

Requests to the custom tag update service on Thingworx will be performed by the Ewon in the following format:

```json
{
   "jsonrpc": "2.0",
   "method": "[Value of RemoteTagUpdateTriggerEnum]",
   "params": {
      "stringInfo": "[Value of RemoteTagUpdateTriggerString]"
},
"id": "[Unique ID]"
}
```
- The ID field contains a unique number value which increments by one with each request. Responses received must contain a matching ID value, or they will not be processed.

##### Custom Service Response Format

Responses from the custom tag update service on Thingworx are expected by the Ewon in the following format:

```json
{
   "jsonrpc": "2.0",
   "result": {
      "restorePreviousValsOnFault": false,
      "tags": [
         {
            "name": "[Tag Name Here]",
            "type": "[Tag Type Here]",
            "value": 0
         },
         {
            "name": "[Tag Name Here]",
            "type": "[Tag Type Here]",
            "value": false
         },
         {
            "name": "[Tag Name Here]",
            "type": "[Tag Type Here]",
            "value": "[Tag Value Here]"
         }
      ]
   },
   "id": "[Unique ID]"
}
```
- The _id_ field must match the value specified in the request, or the response will not be processed by the Ewon.

- The _restorePreviousValsOnFault_ field is optional, and can be used to indicate that previous tag values should be restored if there is a failure encountered while applying a payload. When set to `true`, tag values are backed up before applying changes from the tag update service to ensure that previous values can be restored to prevent partial tag updates in the event of an error. If this field is not included, the value will default to `false`.

- The _type_ field of each tag object must be one of the following: `integer`, `float`, `string`, `boolean`, `dword`.

Each response received by the Ewon if verified to ensure the following:

1. JSON is valid
2. Tags exist on Ewon
3. Tag types match on Ewon

#### Log Output

##### Configured Logging Level
There are seven options for the configurable log level. The logging level is configured in the application configuration, detailed in Section 4. Each log level includes the output for the log levels below it (lower numerical value). For example, log level 3 (warning) includes the output for log level 2 (serious) and log level 1 (critical). All positive log levels print to the Flexy realtime logs, and negative log levels output for text files in the /usr directory of Ewon Flexy. Log text files are named logN.txt, where N is an integer.

| LogLevel         | Description                                                                      |
| :--------------: | :------------------------------------------------------------------------------- |
| 6, -6 (Trace)    | Exception stack traces                                                           |
| 5, -5 (Debug)    | Low level information about the state of the application                         |
| 4, -4 (Info)     | Application state information                                                    |
| 3, -3 (Warning)  | Issues encountered in the application that are not serious                       |
| 2, -2 (Serious)  | Errors that are serious but recoverable                                          |
| 1, -1 (Critical) | Critical application log messages (Startup, Initialization, Unrecoverable Error) |
| 0 (None)         | Logging is disabled                                                              |

##### Logging Performance
The log output has an impact on the performance of the application. For normal operation, the log level should be set to the lowest numerical value (highest when outputting to log files) that will produce satisfactory logs. For debugging issues with the application or a device, higher numerical values (lower when outputting to log files) can be used to print additional information to help diagnose.

Negative log values utilize log files in the /usr directory to store log output. This should only be enabled for short periods of time while diagnosing problems. Leaving this enabled for extended periods of time will cause excessive wear on the flash memory of the Ewon Flexy and could cause irreparable damage.

##### Adding Log Output
Log output can be added to the application by inserting calls to the logging class, Logger. Each log level has a method that will output the log to the appropriate location. For example, a call to `Logger.LOG_DEBUG(String)` will result in log output that is visible if the configured application log level is set to debug (5/-5) or trace (6/-6). A call to `Logger.LOG_CRITICAL(String)` will result in log output that is visible if the configured application log level is set to critical (1/-1) or a higher logging level. A call to `Logger.LOG_EXCEPTION(Exception)` will result in log output that is visible if the configured application log level is set to trace (6/-6).

### Development Environment
This project is based on the [Solution Center Maven Starter Project](https://github.com/hms-networks/sc-java-maven-starter-project), and uses the Maven build system for compilation, testing, and packaging.

Maven lifecycle information and other details about the development environment provided by the [Solution Center Maven Starter Project](https://github.com/hms-networks/sc-java-maven-starter-project) can be found in its README.md at [https://github.com/hms-networks/sc-java-maven-starter-project/blob/main/README.md](https://github.com/hms-networks/sc-java-maven-starter-project/blob/main/README.md).

#### Source Code
Source code and IDE project files for the Flexy Java app are made available in the [hms-networks/flexy-thingworx-connector](https://github.com/hms-networks/flexy-thingworx-connector) repository on GitHub. They are also included in Flexy Thingworx Connector release \(.zip\) files.

##### Cloning

The source code can be downloaded using Git clone. For more information about the Git clone command, please refer to the GitHub clone documentation at [https://docs.github.com/en/github/creating-cloning-and-archiving-repositories/cloning-a-repository](https://docs.github.com/en/github/creating-cloning-and-archiving-repositories/cloning-a-repository).

Using the git client of your choice, clone the https://github.com/hms-networks/flexy-thingworx-connector repository.

Using HTTPS:
```console
> git clone https://github.com/hms-networks/flexy-thingworx-connector.git --recursive
```

Using SSH:
```console
> git clone git@github.com:hms-networks/flexy-thingworx-connector.git --recursive
```

##### Existing Thread.sleep() Invocations
In many locations throughout the application, calls are made to Thread.sleep(). These calls are necessary to signal to the JVM and the Ewon Flexy that other processes can be serviced. Reducing or removing these calls to Thread.sleep() may cause stability issues with the Flexy. This behavior may manifest as a device reboot.

#### Javadocs
Developer documentation is available in Javadoc jar format in /target folder of release packages. A generated copy can also be found in the /target/apidocs folder after compiling with Maven.

#### Releases
To release a compiled version of the Ewon Thingworx Connector, two files must be supplied to the end-user, the compiled Ewon Thingworx Connector jar, and a jvmrun file. The files should be installed to the /usr directory of the Ewon Flexy. On the first run of the application, a default application configuration will be written to the Ewon’s filesystem. This can be modified to include the desired configuration, as outlined in the [Configuration](#configuration) section.

##### Automatic Startup (jvmrun)

On startup, the Ewon Flexy will look for the presence of a jvmrun file. If present, the Ewon Flexy will automatically launch the application referenced in the jvmrun script with the configured settings.

The jvmrun script, included in the /starting-files folder, configures the connector application to run with a 25 MB heap. If the heap size is reduced in the jvmrun script, the application may become unstable and could crash if unable to allocate memory.

## Support
Support for the Ewon Thingworx Connector may be available under the terms of your quote, if applicable. New or additional support can be purchased, as needed, by contacting your HMS salesperson. If you don't know your HMS salesperson, please visit the HMS contact information page at [https://www.hms-networks.com/contact](https://www.hms-networks.com/contact).

### Reporting Bugs and Issues
If you encounter a bug or issue in the Ewon Thingworx Connector, please open an issue on the GitHub repository issues page, found at [https://github.com/hms-networks/flexy-thingworx-connector/issues](https://github.com/hms-networks/flexy-thingworx-connector/issues).

### Flexy Support
Support and additional information about the Ewon Flexy can be found on the Ewon support homepage at [https://ewon.biz/technical-support/support-home](https://ewon.biz/technical-support/support-home).

### Development Environment Support
Detailed information about the development environment provided by the [Solution Center Maven Starter Project](https://github.com/hms-networks/sc-java-maven-starter-project) can be found in its README.md at [https://github.com/hms-networks/sc-java-maven-starter-project/blob/main/README.md](https://github.com/hms-networks/sc-java-maven-starter-project/blob/main/README.md).

Additional information and support about the Ewon ETK can be found on the Ewon Java programming homepage at [https://developer.ewon.biz/content/java-0](https://developer.ewon.biz/content/java-0).