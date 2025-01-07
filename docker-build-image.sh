#!/bin/bash

./jar-build.sh
deidentifhir_pipeline_version=$(mvn help:evaluate -f pom.xml -Dexpression=project.version -q -DforceStdout)
echo ${deidentifhir_pipeline_version}
DOCKER_BUILDKIT=1 docker build -t deidentifhir-pipeline:"$deidentifhir_pipeline_version" -t deidentifhir-pipeline:latest --build-arg deidentifhir_pipeline_version="$deidentifhir_pipeline_version" .
