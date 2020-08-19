# Flexy Thingworx Connector

The Flexy Thingworx Connector provides a connector-based solution development kit for linking the Ewon Flexy to a Thingworx instance.

There are two components that make up the Flexy Thingworx Connector, a Thingworx service and a Flexy Java application.

## Table of Contents
1. [Data Paths](#data-paths)
2. [Thingworx Service Component](#thingworx-service-component)
3. [Flexy Java App Component](#flexy-java-app-component)
   1. [Installation](#installation)
   2. [Configuration](#configuration)
   3. [Source Code](#source-code)
      1. [Development Environment](#development-environment)
      
## Data Paths
There are two possible data paths when using the Flexy Thingworx Connector. 

The first uses a direct path from the Flexy to Thingworx using the Flexy Java Application component. The second data path uses Talk2M and does not require the installation of the Flexy Java Application component. 

To use the first data path \(direct\), follow the Flexy Java application component installation instructions below after completing the Thingworx service component installation. To use the second data path \(Talk2M\), omit the Flexy Java application component setup.

## Thingworx Service Component
The Thingworx service component must be installed for both data path configurations (direct and Talk2M).

Information and instructions for the Thingworx service component are available in a separate document, [thingworx-import/README.md](thingworx-import/README.md).

## Flexy Java Application Component
The Flexy Java application component must be installed for the direct data path configuration.

Additional documentation for the Flexy Java application is available in the Ewon Thingworx Connector Reference Guide, available for each release at [https://github.com/hms-networks/flexy-thingworx-connector/releases](https://github.com/hms-networks/flexy-thingworx-connector/releases).

### Installation
Using FTP, transfer the Flexy Java application \(.jar\) and jvmrum files to the /usr/ directory of the Ewon. Then, reboot the Ewon.

Those files are located in the /built-flexy-java-app/ directory of the release zip, found at [https://github.com/hms-networks/flexy-thingworx-connector/releases/latest](https://github.com/hms-networks/flexy-thingworx-connector/releases/latest). When building from source code, the jvmrun file is located in /scripts/, and the Flexy Java application \(.jar\) is located in /build/.

### Configuration
At startup of the Flexy Java application component, it checks for a configuration file in the /usr/ directory of the Ewon. If one is not present, the application will automatically create one with default values.

After the Flexy Java application component has been installed and started, the configuration should be modified to contain the proper values for AppKey and ThingworxIP for your Thingworx instance.

### Source Code
Source code and an Eclipse project for the Flexy Java app are made available in the [hms-networks/flexy-thingworx-connector](https://github.com/hms-networks/flexy-thingworx-connector) repository on GitHub. It is also included in the /source-flexy-java-app/ folder of Flexy Thingworx Connector release \(.zip\) files.

#### Development Environment
The Flexy Java application component was developed using a standard Ewon Java development environment. Documentation and additional information about the Ewon Java development environment is available in the Ewon Java Toolkit User Guide \(J2SE\) at [https://developer.ewon.biz/content/java-0#dev-documents](https://developer.ewon.biz/content/java-0#dev-documents). 