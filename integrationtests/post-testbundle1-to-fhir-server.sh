#!/bin/bash

curl -v --silent -S --location \
--request POST 'http://localhost:8082/fhir/' \
--header 'Content-Type: application/json' \
--data "@testbundle1.json"


