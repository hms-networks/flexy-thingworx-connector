# Flexy Thingworx Connector Changelog

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