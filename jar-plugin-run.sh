#!/bin/bash

java -Dloader.path=./plugin/target/plugin-1.0.0.jar -jar target/deidentifhir-pipeline-0.1.4-runnable.jar --spring.config.location=plugin/application.yaml
