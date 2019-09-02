#!/usr/bin/env bash

# Check to see if the Postgres container already exists
cid=$(docker ps --all --quiet --filter "name=maven-artifact-notifier-tomcat")

if [ -n "$cid" ]; then
    echo "Starting existing docker container $cid"
    docker start $cid
else
    # Run an instance of Tomcat, mounting the WAR file to be deployed
    DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
    echo "Running Tomcat in a docker container, stop with 'docker-stop-tomcat.sh'"
    docker run \
        --name maven-artifact-notifier-tomcat \
        --detach \
        --publish 8080:8080 \
        --link maven-artifact-notifier-postgres \
        --volume $DIR/../maven-artifact-notifier-webapp/target/maven-artifact-notifier.war:/usr/local/tomcat/webapps/maven-artifact-notifier.war \
        --volume $DIR/configuration-docker.properties:/etc/maven-artifact-notifier/configuration.properties \
        tomcat
fi

echo "To tail Tomcat logs, run 'docker logs --follow maven-artifact-notifier-tomcat'"
