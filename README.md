# HSL Transitdata Common-library

This codebase contains common code to be shared between Transitdata-projects. 

Package [transitdata.proto](src/main/java/fi/hsl/common/transitdata/proto) contains protobuf-schemas that are used to describe the 
 data payloads within the Transitdata pipeline. Default files are already in this repository but in case the schema changes
 use following script to generate the files:
 
  `cd protos && ./generate-protos.sh`   
  

## Configuration

Library contains package [config](src/main/java/fi/hsl/common/config) which has tools to configure the application. 
By default the configuration file is read from resources inside the application jar-bundle from a file named `environment.conf`.
You can override the path with env variable `CONFIG_PATH` which merges these two (if found).

This library also contains a file [common.conf](src/main/resources/common.conf) which can be used as template or even 
included as a baseline config to your application like this:

  `include "common.conf"`   
  `<your config here..>`   


More info can be found from [Lightbend's github pages](https://github.com/lightbend/config)

## Usage:

- Build

  `mvn clean package`
  
- Deploy to local artifactory, for other projects to access it

  `mvn install`


## Tests:

We're separating our unit & integration tests using [this pattern](https://www.petrikainulainen.net/programming/maven/integration-testing-with-maven/).

Unit tests:

- add test classes under ./src/test with suffix *Test.java
- `mvn clean test -P unit-test`   

Integration tests:

- add test classes under ./src/integration-test with prefix IT*.java
- `mvn clean verify -P integration-test`   
