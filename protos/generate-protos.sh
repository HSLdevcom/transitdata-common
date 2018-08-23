#!/bin/bash

# This scripts generates java classes based on the .proto files.

# gtfs-realtime.proto
protoc gtfs-realtime.proto --java_out=../src/main/java/

# pubtrans-tables.proto
protoc pubtrans-tables.proto --java_out=../src/main/java/
