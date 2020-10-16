#!/usr/bin/python

# Builds a release zip folder

import sys
import os
import zipfile

PROJECT_NAME = "flexy-thingworx-connector"

#Path that the release file gets saved to
RELEASE_PATH = "../releases/"

#Path in the release directory that pre-built Flexy java app files should be placed
BUILT_JAVA_APP_PATH = "built-flexy-java-app/"

#Path in the release directory that Flexy java app source files should be placed
SOURCE_JAVA_APP_PATH = "source-flexy-java-app/"

#Path in the release directory that Thignworx import files should be placed
THINGWORX_IMPORT_PATH = "thingworx-import/"

#File and folder names for release
README_FILENAME            = "README.md"
CHANGELOG_FILENAME         = "CHANGELOG.md"
JAR_FILENAME               = "flexy-thingworx-connector.jar"
JVMRUN_FILENAME            = "jvmrun"
BUILD_XML_FILENAME         = "build.xml"
ECLIPSE_CLASSPATH_FILENAME = ".classpath"
ECLIPSE_PROJECT_FILENAME   = ".project"

#Paths for release files and folders
README_PATH            = "../"
CHANGELOG_PATH         = "../"
JAR_PATH               = "../build/"
TW_IMPORT_PATH         = "../thingworx-import/"
JVMRUN_PATH            = "../scripts/"
JAVADOC_PATH           = "../javadocs/"
SRC_PATH               = "../src/"
LIB_PATH               = "../lib/"
BUILD_XML_PATH         = "../"
ECLIPSE_CLASSPATH_PATH = "../"
ECLIPSE_PROJECT_PATH   = "../"

def CreateRelease(version):

   #Create releases directory if it does not already exist
   if not os.path.exists(RELEASE_PATH):
      os.makedirs(RELEASE_PATH)

   releaseFilename = RELEASE_PATH + PROJECT_NAME + "-" + version

   #Create the release zip folder
   zf = zipfile.ZipFile("%s.zip" % releaseFilename, "w", zipfile.ZIP_DEFLATED)

   #Add "release" files to the zip
   zf.write(os.path.abspath(os.path.join(README_PATH,README_FILENAME)), README_FILENAME)
   zf.write(os.path.abspath(os.path.join(CHANGELOG_PATH,CHANGELOG_FILENAME)), CHANGELOG_FILENAME)
   zf.write(os.path.abspath(os.path.join(JAR_PATH,JAR_FILENAME)), BUILT_JAVA_APP_PATH + JAR_FILENAME)
   zf.write(os.path.abspath(os.path.join(JVMRUN_PATH,JVMRUN_FILENAME)), BUILT_JAVA_APP_PATH + JVMRUN_FILENAME)
   zf.write(os.path.abspath(os.path.join(BUILD_XML_PATH,BUILD_XML_FILENAME)), SOURCE_JAVA_APP_PATH + BUILD_XML_FILENAME)
   zf.write(os.path.abspath(os.path.join(ECLIPSE_PROJECT_PATH,ECLIPSE_PROJECT_FILENAME)), SOURCE_JAVA_APP_PATH + ECLIPSE_PROJECT_FILENAME)
   zf.write(os.path.abspath(os.path.join(ECLIPSE_CLASSPATH_PATH,ECLIPSE_CLASSPATH_FILENAME)), SOURCE_JAVA_APP_PATH + ECLIPSE_CLASSPATH_FILENAME)

   #Add Thingworx import folder to the zip
   for folderName, subfolders, filenames in os.walk(TW_IMPORT_PATH):
      for filename in filenames:
         #create complete filepath of file in directory
         filepath = os.path.join(folderName, filename)
         # Add file to zip
         zf.write(filepath, THINGWORX_IMPORT_PATH + filepath.replace(TW_IMPORT_PATH,''))

   #Add javadocs folder to the zip
   for folderName, subfolders, filenames in os.walk(JAVADOC_PATH):
      for filename in filenames:
         #create complete filepath of file in directory
         filepath = os.path.join(folderName, filename)
         # Add file to zip
         zf.write(filepath, SOURCE_JAVA_APP_PATH + filepath.replace('../',''))

   #Add src folder to the zip
   for folderName, subfolders, filenames in os.walk(SRC_PATH):
      for filename in filenames:
         #create complete filepath of file in directory
         filepath = os.path.join(folderName, filename)
         # Add file to zip
         zf.write(filepath, SOURCE_JAVA_APP_PATH + filepath.replace('../',''))

   #Add lib folder to the zip
   for folderName, subfolders, filenames in os.walk(LIB_PATH):
      for filename in filenames:
         #create complete filepath of file in directory
         filepath = os.path.join(folderName, filename)
         # Add file to zip
         zf.write(filepath, SOURCE_JAVA_APP_PATH + filepath.replace('../',''))

   #Close the release zip folder
   zf.close()

   print("\nSuccessfully made release: " + releaseFilename + ".zip")

if __name__ == '__main__':

   if len(sys.argv) != 2:
      print("Usage: MakeRelease.py versionNumber")
      sys.exit(0)

   CreateRelease(sys.argv[1])
