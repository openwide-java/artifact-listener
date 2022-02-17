artifact-listener.org
=====================

[![No Maintenance Intended](https://unmaintained.tech/badge.svg)](https://unmaintained.tech/) [![Build Status](https://travis-ci.org/openwide-java/artifact-listener.svg?branch=master)](https://travis-ci.org/openwide-java/artifact-listener)

Unmaintained
------------

Unfortunately, we don't have the resources to maintain this project anymore.

This means:
- **this GitHub repository is currently unmaintained**
- the corresponding website <https://www.artifact-listener.org/> will **not be maintained either**:
  - https certificate may expire as soon as 2022/05/18
  - hosting server may be shut down at any time (before or after 2022/05/18)

If anyone wishes to host the service, you can get in touch with us by creating an issue on the project. 

About
-----

This is the code behind https://www.artifact-listener.org/.

Sources are licensed under the Apache 2 license.

This service is mostly developed by Smile (https://www.smile.eu). External contributions, ideas... are highly welcome.

This is still a work in progress as we plan to clean up the code/add feature/... in the future.

Contact us
----------

You can contact us via twitter https://twitter.com/Art_Listener or via email /contact at artifact dash listener dot org/.

Architecture
------------

Artifact Listener is a Spring based application using JavaConfig.

It uses:
- Hibernate, QueryDSL and Hibernate Search for persistence
- Wicket, wiQuery, jQuery and jQuery UI for the presentation layer
- a PostgreSQL database

How to start
------------

Using Eclipse and m2eclipse, just clone the repository in Eclipse.

### Start Postgres

#### Native PostgreSQL

You need to create a PostgreSQL database called maven_artifact_notifier:
```
createuser -U postgres maven_artifact_notifier
createdb -U postgres -O maven_artifact_notifier maven_artifact_notifier
psql -U postgres -c "alter user maven_artifact_notifier with password 'maven_artifact_notifier';"
```

#### Docker

To run PostgreSQL in a [docker](https://www.docker.com/) container, run the included `docker/postgres-start.sh`.
Shut it down with `postgres-stop.sh`. These commands assume you have Docker installed on your
development machine and that `docker` is on your path. The docker container exposes the default Postgres port of 5432,
so that port must not be in use unless you modify the startup script to expose a different port.

### Initialize Artifact Listener Database

- Run eclipse/processor all.launch
- Ensure that target/generated-sources/apt is a source folder in your eclipse project; if not please add it manually
(source detection depends on your m2e eclipse installed plugins and options)
- Create /data/services/maven-artifact-notifier/ folder with application write access; this folder is used for lucene's
index storage (can be configured via *data.path* in *configuration.properties*)
- Run MavenArtifactNotifierInitFromExcelMain from the init module and you should be all set.

### Run Artifact Listener

#### Native Tomcat

Deploy the webapp (`maven-artifact-notifier-webapp/target/maven-artifact-notifier.war`) in your container of choice - we use Tomcat 7 embedded in WST.

You can override default configuration (`configuration.properties`) by creating a `/etc/maven-artifact-notifier/configuration.properties` file.

You may also override log4j configuration with a `/etc/maven-artifact-notifier/log4j.properties` file.

#### Docker

Build the project (`mvn clean package`) and configure the application for local testing with Docker by modifying `docker/configuration-docker.properties` if required. 
You can run it with Tomcat in a Docker container using `docker/tomcat-start.sh`, which mounts the WAR file into Tomcat's  webapps directory so you can run development builds in place. Stop it with `docker/tomcat-stop.sh`. The docker container exposes Tomcat's default port of 8080, so that port must not be in use unless you modify the startup script to expose a different port.

Artifact listener should now be available at [http://localhost:8080/maven-artifact-notifier](http://localhost:8080/maven-artifact-notifier). Default login/password is admin@artifact-listener.org/admin.
