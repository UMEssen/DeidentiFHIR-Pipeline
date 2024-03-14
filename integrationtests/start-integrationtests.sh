#!/bin/bash

failure=0
docker compose up -d
hurl --verbose --test test-transfer.hurl
if [[ $? -ne 0 ]] ; then
    echo "There are integrationtests with hashmap failures"
    failure=1
fi
docker compose down -v

docker compose -f docker-compose-with-gpas.yml up -d
hurl --verbose --test test-transfer.hurl
if [[ $? -ne 0 ]] ; then
    echo "There are integrationtests with gPAS failures"
    failure=1
fi
docker compose -f docker-compose-with-gpas.yml down -v
if [[ failure -ne 0 ]] ; then
    echo "There are integrationtests failures"
    exit 1
else
    echo "Integrationtests completed successfully"
    exit 0
fi
