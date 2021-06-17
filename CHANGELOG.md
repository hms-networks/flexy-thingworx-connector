# Flexy Thingworx Connector Changelog

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