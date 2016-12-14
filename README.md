artifact-listener.org
=====================

[![Build Status](https://travis-ci.org/openwide-java/artifact-listener.svg?branch=master)](https://travis-ci.org/openwide-java/artifact-listener)

About
-----

This is the code behind https://www.artifact-listener.org/.

Sources are licensed under the Apache 2 license.

This service is mostly developed by Open Wide (http://www.openwide.fr/). External contributions, ideas... are highly welcome.

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
```

#### Docker

To run PostgreSQL in a [docker](https://www.docker.com/) container, run the included `docker/postgres-start.sh`.
Shut it down with `postgres-stop.sh`. These commands assume you have Docker installed on your
development machine and that `docker` is on your path. The docker container exposes the default Postgres port of 5432,
so that port must not be in use unless you modify the startup script to expose a different port.

### Initialize Artifact Listener Database

- Check that you can connect to your database using the information in development.properties Maven profile file.
- Run eclipse/processor all.launch
- Ensure that target/generated-sources/apt is a source folder in your eclipse project; if not please add it manually
(source detection depends on your m2e eclipse installed plugins and options)
- Create /data/services/maven-artifact-notifier/ folder with application write access; this folder is used for lucene's
index storage (can be configured via *data.path* in *configuration.properties*)
- Run MavenArtifactNotifierInitFromExcelMain from the init module and you should be all set.

### Run Artifact Listener

#### Native Tomcat

Deploy the webapp (`maven-artifact-notifier-webapp/target/maven-artifact-notifier.war`) in your container of choice - we use Tomcat 7 embedded in WST.

#### Docker

To configure the application for local testing with Docker, build the project with the 'docker' Maven profile (`mvn clean package -Pdocker`).
You can run it with Tomcat in a Docker container using `docker/tomcat-start.sh`, which mounts the WAR file into Tomcat's  webapps directory so you can run development builds in place. Stop it with `docker/tomcat-stop.sh`. The docker container exposes Tomcat's default port of 8080, so that port must not be in use unless you modify the startup script to expose a different port.

Artifact listener should now be avalable at [http://localhost:8080/maven-artifact-notifier](http://localhost:8080/maven-artifact-notifier). Default login/password is admin@artifact-listener.org/admin.
