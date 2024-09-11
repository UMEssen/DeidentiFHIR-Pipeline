#!/bin/bash

docker_up_options=(-d --quiet-pull)

failure=0

# pseudonymization with hashmap
echo "=== Starting integrationtests with hashmap ==="
docker compose up "${docker_up_options[@]}"
#hurl --verbose --test test-transfer.hurl
hurl --test test-transfer.hurl
if [[ $? -ne 0 ]] ; then
    printf "xxx There are integrationtests with hashmap failures xxx\n\n"
    failure=1
else
    printf "✔✔✔ Finished integrationtests with hashmap successfully without errors ✔✔✔\n\n"
fi
docker compose down -v

# pseudonymization with gpas
echo "=== Starting integrationtests with gPAS ==="
docker compose -f docker-compose-with-gpas.yml up "${docker_up_options[@]}"
#hurl --verbose --test test-transfer.hurl
hurl --test test-transfer.hurl
if [[ $? -ne 0 ]] ; then
    printf "xxx There are integrationtests with gPAS failures xxx\n\n"
    failure=1
else
    printf "✔✔✔ Finished integrationtests with gPAS successfully without errors ✔✔✔\n\n"
fi
docker compose -f docker-compose-with-gpas.yml down -v

# no pseudonymization
echo "=== Starting integrationtests with no pseudonymization ==="
docker compose -f docker-compose-no-pseudonymization.yml up "${docker_up_options[@]}"
#hurl --verbose --test test-transfer-no-pseudonymization.hurl
hurl --test test-transfer-no-pseudonymization.hurl
if [[ $? -ne 0 ]] ; then
    printf "xxx There are integrationtests with no pseudonymization failures xxx\n\n"
    failure=1
else
    printf "✔✔✔ Finished integrationtests with no pseudonymization successfully without errors ✔✔✔\n\n"
fi
docker compose -f docker-compose-no-pseudonymization.yml down -v

# fhir-collector
echo "=== Starting integrationtests with fhir-collector and basic auth ==="
docker compose -f docker-compose-fhir-collector.yml up "${docker_up_options[@]}"
hurl --test test-transfer.hurl
if [[ $? -ne 0 ]] ; then
    printf "xxx There are integrationtests with fhir-collector and basic auth failures xxx\n\n"
    failure=1
else
    printf "✔✔✔ Finished integrationtests with fhir-collector and basic auth successfully without errors ✔✔✔\n\n"
fi
docker compose -f docker-compose-fhir-collector.yml down -v

# test if there were any failure
if [[ failure -ne 0 ]] ; then
    printf "xxx There are integrationtests failures xxx\n\n"
    exit 1
else
    printf "✔✔✔ Integrationtests completed successfully without errors ✔✔✔\n\n"
    exit 0
fi
