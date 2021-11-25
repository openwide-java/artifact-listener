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
        -e JPDA_ADDRESS=8000 \
        -e JPDA_TRANSPORT=dt_socket \
        --publish 8080:8080 \
        --publish 8000:8000 \
        --link maven-artifact-notifier-postgres \
        --volume $DIR/../maven-artifact-notifier-webapp/target/maven-artifact-notifier.war:/usr/local/tomcat/webapps/ROOT.war \
        --volume $DIR/configuration-docker.properties:/etc/maven-artifact-notifier/configuration.properties \
        --volume $DIR/log4j-docker.properties:/etc/maven-artifact-notifier/log4j.properties \
        tomcat:7 \
        /usr/local/tomcat/bin/catalina.sh jpda run
fi

echo "To tail Tomcat logs, run 'docker logs --follow maven-artifact-notifier-tomcat'"
