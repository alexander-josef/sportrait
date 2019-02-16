#!/bin/sh

# startup postgres test-db from docker container
docker run --name funny_photographer -e POSTGRES_PASSWORD=postgres -p5431:5432 -d sportrait-postgres-testdb:latest
