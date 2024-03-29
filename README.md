[![Build status](https://github.com/HSLdevcom/transitdata-common/actions/workflows/test-and-build.yml/badge.svg)](https://github.com/HSLdevcom/transitdata-common/actions/workflows/test-and-build.yml)
[![GitHub tag (latest by date)](https://img.shields.io/github/v/tag/hsldevcom/transitdata-common)](https://github.com/HSLdevcom/transitdata-common/tags)

# HSL Transitdata Common-library

This repository contains code and constants to be shared between [Transitdata-projects](https://github.com/HSLdevcom/transitdata).


## Including the Library

Project is published as fat-jar via [GitHub Packages](https://github.com/orgs/HSLdevcom/packages?repo_name=transitdata-common).
Add the dependency to the project by adding this snippet to your pom.xml file:

```
    <repositories>
        <repository>
            <id>github</id>
            <url>https://maven.pkg.github.com/HSLdevcom/*</url>
        </repository>
    </repositories>
    <dependencies>
       <dependency>
         <groupId>fi.hsl</groupId>
         <artifactId>transitdata-common</artifactId>
         <version>${common.version}</version>
       </dependency>
    </dependencies>
```   

To access GitHub Packages, you also need to create an access token and include it in Maven settings. See [GitHub documentation](https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-apache-maven-registry#authenticating-to-github-packages) for more details.

Note that older versions of transitdata-common are automatically removed from GitHub Packages to encourage keeping the package up-to-date. If the build of a project that depends on transitdata-common stops working, update transitdata-common to the [latest version](https://github.com/HSLdevcom/transitdata-common/packages/665985).

It is also possible to compile the project yourself and use it via local maven repository.

## Protobuf

Project contains the [protobuf files](https://developers.google.com/protocol-buffers/)
which are used within the Pulsar cluster and also to define the final
[GTFS-RT message](https://developers.google.com/transit/gtfs-realtime/gtfs-realtime-proto) payload.

The protobuf schemas are configured in [protos-folder](/protos).
Default files are already included in the Java-package [transitdata.proto](src/main/java/fi/hsl/common/transitdata/proto)
in this repository but you can also generate the files yourself:

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
