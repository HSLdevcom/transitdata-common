# HSL Transitdata Common-library

This codebase contains common code to be shared between Transitdata-projects. 

Package [transitdata.proto](src/main/java/fi/hsl/common/transitdata/proto) contains protobuf-schemas that are used to describe the 
 data payloads within the Transitdata pipeline. Default files are already in this repository but in case the schema changes
 use following script to generate the files:
 
  `cd protos && ./generate-protos.sh`   
  


## Usage:

- Build

  `mvn clean package`
  
- Deploy to local artifactory, for other projects to access it

  `mvn install`
  