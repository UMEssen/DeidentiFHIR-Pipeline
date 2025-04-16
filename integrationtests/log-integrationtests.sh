#!/bin/bash

docker compose logs -f
docker compose -f docker-compose-with-gpas.yml logs -f
docker compose -f docker-compose-no-pseudonymization.yml logs -f
docker compose -f docker-compose-fhir-collector.yml logs -f
