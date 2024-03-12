#!/bin/bash

curl --silent -S --location \
  --request POST 'http://localhost:8042/start' \
  --header 'Content-Type: application/json' \
  --data '{
    "project": "test-project1"
  }'

