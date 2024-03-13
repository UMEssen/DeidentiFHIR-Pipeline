#!/bin/bash

docker compose up -d
hurl --verbose --test test-transfer.hurl
if [[ $? -ne 0 ]] ; then
    echo "There are integrationtests failures"
    docker compose down -v
    exit 1
else
    echo "Integrationtests finished successfully"
    docker compose down -v
    exit 0
fi

