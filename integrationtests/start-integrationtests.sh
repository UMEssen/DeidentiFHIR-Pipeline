#!/bin/bash

docker_up_options=(-d --quiet-pull)
RED='\033[0;31m'
GREEN='\033[0;32m'
NC='\033[0m' # No Color

failure=0

# pseudonymization with hashmap
echo "=== Starting integrationtests with hashmap ==="
docker compose up "${docker_up_options[@]}"
#hurl --verbose --test test-transfer.hurl
hurl --test test-transfer.hurl
if [[ $? -ne 0 ]] ; then
    printf "${RED}xxx There are integrationtests with hashmap failures xxx${NC}\n\n"
    failure=1
else
    printf "${GREEN}✔✔✔ Finished integrationtests with hashmap successfully without errors ✔✔✔${NC}\n\n"
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
    printf "${GREEN}✔✔✔ Finished integrationtests with gPAS successfully without errors ✔✔✔${NC}\n\n"
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
    printf "${GREEN}✔✔✔ Finished integrationtests with no pseudonymization successfully without errors ✔✔✔${NC}\n\n"
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
    printf "${GREEN}✔✔✔ Finished integrationtests with fhir-collector and basic auth successfully without errors ✔✔✔${NC}\n\n"
fi
docker compose -f docker-compose-fhir-collector.yml down -v

# test if there were any failure
if [[ failure -ne 0 ]] ; then
    printf "${RED}\nxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx${NC}"
    printf "${RED}\nxxx There are integrationtests failures xxx${NC}"
    printf "${RED}\nxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx${NC}"
    printf "\n\n"
    exit 1
else
    printf "${GREEN}\n✔✔✔✔✔✔✔✔✔✔✔✔✔✔✔✔✔✔✔✔✔✔✔✔✔✔✔✔✔✔✔✔✔✔✔✔✔✔✔✔✔✔✔✔✔✔✔✔✔✔✔✔✔✔✔✔✔✔✔✔✔✔${NC}"
    printf "${GREEN}\n✔✔✔ Integrationtests completed successfully without errors ✔✔✔${NC}"
    printf "${GREEN}\n✔✔✔✔✔✔✔✔✔✔✔✔✔✔✔✔✔✔✔✔✔✔✔✔✔✔✔✔✔✔✔✔✔✔✔✔✔✔✔✔✔✔✔✔✔✔✔✔✔✔✔✔✔✔✔✔✔✔✔✔✔✔${NC}"
    printf "\n\n"
    exit 0
fi
