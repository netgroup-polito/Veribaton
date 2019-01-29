# Veribaton

Veribaton is a java Spring Boot service exposing a subset of [Openbaton](http://http://openbaton.github.io/) REST API to enable smart deployment through interaction with [Verifoo](https://github.com/netgroup-polito/verifoo).

## Installation
Veribaton requires Java version 1.8 or higher, a release of Verifoo after January 2018 and Openbaton >= 5.0.0 to run properly.

##### Prerequisites
 - Download and install Java as described in Oracle Java [instruction page](https://www.java.com/en/download/help/download_options.xml)
 - Download and install the latest release of Verifoo from [here](https://github.com/netgroup-polito/verifoo)
 - Install Openbaton using [instructions](https://openbaton.github.io/documentation/nfvo-installation/)

##### Install from sources
- Clone this repository in a directory of choice:
```sh
$ git clone https://gitlab.com/raerith/veribaton/
```
- Modify application properties in folder resources using your favourite text editor.
```sh
$ cd veribaton/src/main/resources
$ vi application.properties
```
Editable properties:

| **Property**      | Description   | Default value|
| ------------- |:-------------:| -----:|
| server.port      | specifies the port the REST API server will listen on | 9090|
| verifoo.scheme      | scheme for verifoo URL, can be http or https      |   http |
| verifoo.host | Verifoo base URL, can be a hostname or IP address      |    localhost |
| verifoo.port | Port on which verifoo is listening | 8090 |
| verifoo.baseUri | Base URI for Verifoo REST service | /verifoo/rest |
| verifoo.deploymentUri | URI for deployment service on Verifoo REST API | /deployment |
| openbaton.host | Openbaton NFVO address, can be a hostname or IP | localhost |
| openbaton.port | Openbaton port | 8080 |
| openbaton.username | username to use when requesting services from Openbaton | admin|
| openbaton.password | password for Openbaton user specified | openbaton |
| openbaton.ssl | whether Openbaton REST uses https or not, can be true or false | false|

The property `spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration` can be deleted or commented out for enabling server basic authentication as provided by default from spring boot web configuration.

- Move to veribaton base directory and start the service using Gradle wrapper.
```sh
$ ./gradlew bootRun
```
