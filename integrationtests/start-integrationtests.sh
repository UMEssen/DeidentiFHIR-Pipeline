#!/bin/bash

failure=0

# pseudonymization with hashmap
echo "=== Starting integrationtests with hashmap ==="
docker compose up -d
#hurl --verbose --test test-transfer.hurl
hurl --test test-transfer.hurl
if [[ $? -ne 0 ]] ; then
    echo "xxx There are integrationtests with hashmap failures xxx"
    failure=1
else
    echo "=== Finished integrationtests with hashmap successfully without errors ==="
fi
docker compose down -v

# pseudonymization with gpas
echo "=== Starting integrationtests with gPAS ==="
docker compose -f docker-compose-with-gpas.yml up -d
#hurl --verbose --test test-transfer.hurl
hurl --test test-transfer.hurl
if [[ $? -ne 0 ]] ; then
    echo "xxx There are integrationtests with gPAS failures xxx"
    failure=1
else
    echo "=== Finished integrationtests with gPAS successfully without errors ==="
fi
docker compose -f docker-compose-with-gpas.yml down -v

# no pseudonymization
echo "=== Starting integrationtests with no pseudonymization ==="
docker compose -f docker-compose-no-pseudonymization.yml up -d
#hurl --verbose --test test-transfer-no-pseudonymization.hurl
hurl --test test-transfer-no-pseudonymization.hurl
if [[ $? -ne 0 ]] ; then
    echo "xxx There are integrationtests with no pseudonymization failures xxx"
    failure=1
else
    echo "=== Finished integrationtests with no pseudonymization successfully without errors ==="
fi
docker compose -f docker-compose-no-pseudonymization.yml down -v

# test if there was any failure
if [[ failure -ne 0 ]] ; then
    echo "xxx There are integrationtests failures xxx"
    exit 1
else
    echo "=== Integrationtests completed successfully without errors ==="
    exit 0
fi
