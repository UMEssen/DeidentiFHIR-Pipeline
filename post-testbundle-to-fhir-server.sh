#!/bin/bash

curl --silent -S --location \
--request POST 'http://localhost:8082/fhir/' \
--header 'Content-Type: application/json' \
--data "@testdata/testbundle.json"


