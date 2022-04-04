# Ewon Flexy Thingworx Connector Changelog

## Version 3.2.1-pre1
### Features
- N/A
### Bug Fixes
- Added missing files to release zip files
### Other
- N/A

## Version 3.2.0
### Features
- Moved to extension library 1.6
- Added configurable max historical buffer read limit feature
- Added memory check when double poll is enabled
- Added historical FIFO time start advance in exception handler
- Added next run command feature to automatically restart application
- Added external Flexy properties file
### Other
- Formatting and Javadoc updates

## Version 3.1.0
### Features
- Added support for queue diagnostic tags (force reset trigger, running behind status, and poll count/heartbeat)
- Removed dependency on device FTP user account
- Improved behavior when historical data queue is running behind
  - Data poll size is doubled when queue is running behind to speed up processing
  - Log output is displayed in more easily readable format (days, hours, minutes, and seconds)
### Bug Fixes
- Fixed issue which could cause data points to use the wrong time zone
### Other
- Clean up misspellings in code

## Version 3.0.0
### Features
- Update project to use Maven builder
### Bug Fixes
- Fixed a bug where HTTP files were using excessive storage space
### Other
- N/A

## Version 2.0.2
### Features
- N/A
### Bug Fixes
- Fixed a bug which could occur with concurrent HTTP requests
### Other
- Added exception catching to triggered remote tag update handler

## Version 2.0.1
### Features
- Added data payload configuration options to config file
- Added triggered remote tag update via custom-JSON service
  - Formatting specified in README.md
### Bug Fixes
- N/A
### Other
- Updated time offset calculation library

## Version 2.0
### Features
- Added option to specify full Thingworx URL
  - Allows for individual device endpoints and app keys for stronger security
- Added services to read live/realtime Ewon tag values using M2Web
- Added additional configuration file options
  - FTP username/password
  - String tag historization
  - Historical log polling
- Improved error detection on connection failure
### Bug Fixes
- Fixed a bug that caused devices to be assigned the incorrect project in Thingworx
- Fixed a bug that prevented string tags from working with direct data path devices
- Fixed a bug that prevented certain data types from being written via Thingworx M2Web services
### Other
- Combined user documentation to repository markdown file(s)

## Version 1.2.1
### Bug Fixes
- Fixed a bug which prevented the jvmrun file from being executed

## Version 1.2
## Other
- Changed config file parameter "ThingworxIP" to ThingworxBaseUrl"
- Added example config file
- Added note to README about specifying scheme in URL and how this can enable the use of TLS

## Version 1.1.1
### Features
- N/A
### Bug Fixes
- Fixed a bug that caused tag data types to be read as undefined when using the Talk2M data path
### Other
- Fix a spelling error in README.md

## Version 1.1
### Features
- Data is added as Thing properties
- Updated screenshots and documentation to Thingworx 9
- Tag quality data is included in data from the Talk2M and direct data paths
- Added option for created properties to use the 'Always' data change type
### Bug Fixes
- Fixed a bug that may have caused data to not be assigned to the proper data type
- Fixed a bug that could have prevented Talk2M/DataMailbox data from being loaded without the 'useHyphens' option enabled
### Other
- Separate thingworx-import README has been merged with main README
- Talk2M data retrieval process now tracks previous transaction ID

## Version 1.0
### Features
- Initial Connector Release
### Bug Fixes
- N/A
### Other
- N/A