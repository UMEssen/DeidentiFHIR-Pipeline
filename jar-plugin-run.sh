#!/bin/bash

java -Dloader.path=./plugin/target/plugin-1.0.0.jar -jar target/deidentifhir-pipeline-0.0.0-SNAPSHOT-runnable.jar --spring.config.location=plugin/application.yaml
