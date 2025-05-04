#!/bin/bash

java -Dloader.path=./plugin/target/ -jar target/deidentifhir-pipeline-0.0.0-SNAPSHOT-runnable.jar --spring.config.location=plugin/application.yaml
