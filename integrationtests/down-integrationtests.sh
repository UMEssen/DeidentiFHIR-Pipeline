#!/bin/bash

docker compose down -v
docker compose -f docker-compose-with-gpas.yml down -v
docker compose -f docker-compose-no-pseudonymization.yml down -v
docker compose -f docker-compose-fhir-collector.yml down -v
