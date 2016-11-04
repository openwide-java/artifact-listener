#!/usr/bin/env bash

# Check to see if the Postgres container already exists
cid=$(docker ps --all --quiet --filter "name=maven_artifact_notifier_postgres")

if [ -n "$cid" ]; then
    echo "Starting existing docker container $cid"
    docker start $cid
else
    # Build a docker image
    DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
    docker build --tag artifact-listener-postgres $DIR 
    
    # Run an instance of Postgres with settings matching maven-artifact-notifier-core's development.properties
    echo "Running Postgres docker container, stop with 'docker-stop-postgres.sh'"
    docker run \
        --name maven-artifact-notifier-postgres \
        --env POSTGRES_USER=maven_artifact_notifier \
        --env POSTGRES_PASSWORD=maven_artifact_notifier \
        --env POSTGRES_DB=maven_artifact_notifier \
        --publish 5432:5432 \
        --detach \
        artifact-listener-postgres
fi

echo "To tail Postgres logs, run 'docker logs --follow maven-artifact-notifier-postgres'"
