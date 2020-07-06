
# Ewon Flexy Thingworx connector
This application receives data from a Ewon and stores that data in Thingworx.

## Table of Contents

1. [Installation](#Installation)
2. [Connector Information](#Connector-Information)
	1. [Subscriptions](#Subscriptions)
	2. [Services](#Services)
		1. [CleanAllData](#CleanAllData)
		2. [InsertDataTableItem](#InsertDataTableItem)
		3. [InsertStreamItem](#InsertStreamItem)
		4. [MainExecution](#MainExecution)
		5. [PurgeDataStreams](#PurgeDataStreams)
		6. [TakeInfo](#TakeInfo)
		7. [Talk2MDelete](#Talk2MDelete)
		8. [Talk2MGetData](#Talk2MGetData)
		9. [Talk2MSyncData](#Talk2MSyncData)
		10. [Talk2MTurnOnOrOff](#Talk2MTurnOnOrOff)
		11. [Talk2MUpdateTagForm](#Talk2MUpdateTagForm)
	3. [Properties](#Properties)
3. [Common Errors](#Common-Errors)

# Installation
To install the Thingworx connector components, navigate to the upper right-hand corner of the screen and click the Import/Export button, then click Import.

![img of import](/import/img/importButton.PNG)

Configure the import options as follows:
1. Check "Use default persistence provider".
2. Select Entities.xml from file system.

![img of import window](/import/img/importingWindow.PNG)

**NOTE: the connector is imported in an "off" state and won't do anything until it is turned on**

Click the search bar in the upper left-hand corner of the window and search "ConnectorHost".

Select the ConnectorHost Thing.

![img of search](/import/img/findSearchBar.PNG)

Go to "ConnectorHost">"General Information", this is the default window if ConnectorHost was just selected.

1. Configure "Run As User" to be something other than Administrator.

2. Configure "Update Rate" to the interval at which the connector should run (in milliseconds).

![img of general screen](/import/img/generalConfiguration.PNG)

Go to "ConnectorHost">"Properties and Alerts".

Fill in the following credentials:
* talk2MAccount: Account name for login to Talk2M
* talk2MDeviceID: Device ID associated with Talk2M account.
* talk2MPassword: Password for login to Talk2M account.
* talk2MUsername: Username for login to Talk2M account.

Change "daysToKeep" to be the number of days before data will be purged from Thingworx.

If scriptTimeout was modified in the platform-settings.json file of Thingworx, then update the scriptTimeout property here.
If no change was made, then the scriptTimeout property already holds the default timeout for Thingworx (30 seconds).

![img of properites](/import/img/propertiesScreen.PNG)

Navigate to "ConnectorHost">"Subscriptions".

Select runConnector. Checking the "enable" box will start the connector

# Connector Information

## Subscriptions

### runConnector
runConnector will run the MainExecution service each time the timer event is fired.
The timer event fires according to the update rate as configured under "ConnectorHost">"General Information"
Ex: An update rate of 40000 means that the timer event will be fired once every 40 seconds, and thus runConnector will call MainExecution once every 40 seconds

## Services

### CleanAllData
Running this script will destroy all data created by the connector.
In particular, CleanAllData deletes all entities that are located in the FlexyConnector project and
contain "-StreamData" or "-TableData" in the name.

### InsertDataTableItem
This inserts a tag into a data table based on the name of the tag and the Ewon that the tag came from. If the data table does not exist, then a new data table will be created.
If useHyphensDataTables is false, then all tags will be will be sorted by Ewon and put on a respective datatable called  'ewonName'-'GeneralDataTableName'-TableData where ewonName is passed as a parameter
and GeneralDataTableName is the GeneralDataTableName as configured in Properties and Alerts.

If useHypenDataTables is true, then all tags will be organized according to hyphen location within the name.
More information can be found about this under useHypenDataTables.

Parameters:
String tagName: The name of the tag being inserted.
String ewonName: The name of the Ewon that provided this tag.
String tagValue: The value of the tag being inserted.

### InsertStreamItem
Inserts a tag with time sensitive data into its proper stream. The proper stream is based on tag name. If the stream does not exist, then a stream will be created.
The stream name is always 'ewonName'-'tagName'-StreamData. Every tag with historically logged data needs its own stream.

Parameters:
String tagName: The name of the tag to be inserted.
String ewonName: The name of the Ewon that provided this tag.
String tagValue: The value of the tag to be inserted. Although this is taken as a string,
	the string will be converted to a float value.
String timestamp: The timestamp of the tag to be inserted in ISO 8601 format.


### MainExecution
This is the main service of the Connector.
It executes the following tasks in the specified order:

* Calls PurgeDataStreams
* Calls Talk2MGetData
* Calls Talk2MSyncData until no data left or until there is risk of a timeout exception
* Calls Talk2MDelete to delete all data that has already been stored in Thingworx from DataMailbox


### PurgeDataStreams
Purges all tags that are older than 'daysToKeep' (found under [Properties](#Properties)) days old from the streams created by this connector in the FlexyConnector project.

### TakeInfo
This service is an alternate way of providing data to Thingworx without using DataMailbox.
Calling this service through Thingworx's REST API will input all information as if it was retrieved by Talk2MSyncData or Talk2MGetData.

Parameters:
String Tags: a single JSON object, passed as a string, because Thingworx 8.5 currently has a bug where JSON objects passed as parameters to services can be mutated if the JSON contains an array.
	ExpectedFormat:
```javascript
{
	"datapoints": [
		{
			"name": "made-up-tag",
			"value": 32,
			"timestamp": "XXXXX"
		},
		{
			"name": "made-up-tag",
			"value": 29,
			"timestamp": "AAAAAX"
		},
		{
			"name": "seperate-made-up-tag",
			"value": 56,
			"timestamp": "AAAAAX"
		}
		//...etc
	],
	"info": {
		"ewon-name": "FLEXY-1234-5678-91",
		"ewon-utc-offset-millis": "123456789"
	}
}
```
dataPoints is an array of logged data. It can include duplicates of the same tag at different timestamps.
info is a JSON object of non-logged data.


### Talk2MDelete
Deletes all data that was sent before or with the provided transactionId.

Parameters:
String transactionId: The transactionId to delete from.

### Talk2MGetData
Retrieves all non-logged data from DataMailbox. All of this data is then sent through InsertDataTableItem.

### Talk2MSyncData
Sends a SyncData request to DataMailbox which will retrieve only logged data. All logged data is then sent through InsertStreamItem.

Parameters:
Integer initialTransactionId: the transactionId to use in this syncData request, if none is provided then a value of -1 is used. Using a value of -1 is the same as making a syncData call with no initial transactionId.

Output result: a JSON object as follows:

	{
	"oldTransactionId" : "the TransactionId that was just used and is ready to be deleted."
	"nextTransactionId" : "the next TransactionId that should be called."
	"done": "if another SyncData request is needed to get all of the data from DataMailbox then this is true, otherwise it is false."
	}

### Talk2MTurnOnOrOff
Checks to see if an Ewon is online of offline. If the Ewon is online then the sendOffline command is sent. If the Ewon is offline then the wake-up command is sent.

Parameters:
String name: the name of the Ewon to send offline/online.

### Talk2MUpdateTagForm
Updates a single tag in a particular Ewon to a new value, then checks to ensure the change was successful by verifying with a tag read.

Parameters:
tagName: The name of the tag to update.
ewonName: The Ewon that contains the tag in question.
tagValue: The new value of the tag.
ewonUsername: The username used to login to the Ewon.
ewonPassword: The password used to login to the Ewon.


## Properties


<b>dataTableToCopy</b>: The data table that InsertDataTableItem will copy when creating a new datatable.

<b>daysToKeep</b>: The maximum number of days to keep data before PurgeDataStreams will purge it.

<b>generalDataTableName</b>: Part of the name of the generalDataTable that will be created for tags that do not have hyphens in their name or for when useHyphensDataTables is off.

<b>scriptTimeout</b>: This should match the scriptTimeout as set in the platform-settings.json file of Thingworx. If this is not an accurate value then MainExecution will incorrectly estimate how much time MainExecution can run for leading to inefficiencies or outright errors. If the value is not changed in platform-settings.json then the default value is 30 seconds.

<b>streamToCopy</b>: The stream that InsertStreamItem will copy when creating a new stream.

<b>talk2MAccount</b>: Account name for login to Talk2M
<b>talk2MDeviceID</b>: Device ID associated with Talk2M account.
<b>talk2MPassword</b>: Password for login to Talk2M account.
<b>talk2MUsername</b>: Username for login to Talk2M account.


<b>useHyphensDataTables</b>: Changes how non-logged data is stored in Thingworx. If this is true, instead of dumping all non-logged data points into the same table, tags will be organized according to the placement of a hyphen.

The string to the left of the first hyphen is used as a referenceName and the dataTableName is created as follows:
"dataTableName = 'ewonName'-'referenceName'-TableData".
Example:
Take the ewonName 'ExampleEwon'

Take the tag names:
CandyMachine-color,
CandyMachine-LastMaintenanceDate,
CandyMachine-flavor,
BottleCapperMaxBottles,
CandyMachine-pwr,
LowPowerMode

In this case BottleCapperMaxBottles and lowPowerMode would get stored in the generic ExampleEwon-InfoDataTable-TableData because they contain no hyphens.

CandyMachine-pwr, CandyMachine-flavor, CandyMachine-LastMaintenanceDate, and CandyMachine-color are all stored in a data table called ExampleEwon-CandyMachine-TableData.

NOTE: When the candyMachine tags are stored, the 'CandyMachine' part of their name is not dropped, so CandyMachine-flavor is still stored as a tag called CandyMachine-flavor within ExampleEwon-CandyMachine-TableData.


# Common Errors

#### Error Executing Event Handler 'runConnector' for event Type.Thing:Entity.ConnectorHost:Event.Timer

This error is most commonly caused by invalid permissions for the ConnectorHost.
To test this, go to "ConnectorHost">"General Information">"Run As User" and change that value to Administrator.

If the error goes away, there was an issue with the permission configuration from before.


#### Upon attempting to delete/input data into a thing: "error: Thing X does not Exist"

This is an issue with Thingworx not being properly synced with the database provider.
Restarting Tomcat should fix the issue. For information on how to do this visit https://stackoverflow.com/questions/12622534/tomcat-restart-webapp-from-command-line
