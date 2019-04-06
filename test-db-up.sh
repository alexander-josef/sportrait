#!/bin/sh


#delete possible remaining instance from same container name:
docker rm /funny_photographer

# startup postgres test-db from docker container
docker run --name funny_photographer -e POSTGRES_PASSWORD=postgres -p5431:5432 -d sportrait-postgres-testdb:latest
