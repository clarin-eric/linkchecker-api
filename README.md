# Linkchecker-web

## General
Linkchecker-web is a REST API based on SPRING BOOT, which serves as an web interface to the link checker database. The structure of this database is 
determined by the [curation-persistence](https://github.com/clarin-eric/curation-persistence) project, which is included as a maven dependency. 

## Requirements
- JDK17 or higher to maven-build the application
- JRE17 or higher to run the application
- a mariadb database-server and an empty database with the right for the application user to create the table structure in this database

## Setup
Linkchecker-web is a mavenized project. As for all Spring Boot projects created in the standard way by [Spring initializr](https://start.spring.io/) the project has a maven wrapper which assures the use of an appropriate version of maven. This means you don't have to install any maven yourself!
After downloading the project, you cd to the project directory and execute

```
mvnw install
```

This builds an executable file at <project-directory>/target/linkchecker-web<version>.jar and installs the classes in the maven repository (usually ~/.m2). 


## Configuration

To run the linkchecker-web application a configuration file has to be provided, which has at least the settings for a *spring.datasource* (see [Data Properties](https://docs.spring.io/spring-boot/docs/current/reference/html/application-properties.html#appendix.application-properties.data)) and *spring.security.user* (see [Security Properties](https://docs.spring.io/spring-boot/docs/current/reference/html/application-properties.html#appendix.application-properties.security)). For more information on different file formats and locations of the configuration file, have a look at the [Spring Boot documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.external-config.files), please.  
A sample file in yaml format is in the /config directory of this project. 
We recommend not to include the configuration file in the jar-build, but to place it either in a /config directory or to specify the file location with the property *spring.config.location* (see next section).

## Running the application
The build jar file is a so called 'fat client', which includes tomcat as an application server. Hence all you have to do is to run the application with

```
java -jar linkchecker-web<version>.jar [--spring.config.location=<path to configuration file>]
```

If you haven't configured any other port (see [here](https://docs.spring.io/spring-boot/docs/current/reference/html/application-properties.html#appendix.application-properties.server)), the application will run by default on port 8080.  