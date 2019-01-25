#!/bin/bash

# This scripts generates java classes based on the .proto files.

protoc gtfs-realtime.proto --java_out=../src/main/java/

protoc pubtrans-tables.proto --java_out=../src/main/java/

protoc internal-messages.proto --java_out=../src/main/java/

protoc mqtt.proto --java_out=../src/main/java/
