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

You need to create a PostgreSQL database called maven_artifact_notifier:
```
createuser -U postgres maven_artifact_notifier
createdb -U postgres -O maven_artifact_notifier maven_artifact_notifier
```

- Check that you can connect to your database using the information in development.properties Maven profile file.
- Run eclipse/processor all.launch
- Run MavenArtifactNotifierInitFromExcelMain from the init module and you should be all set.
- Then deploy the webapp in your container of choice - we use Tomcat 7 embedded in WST.
