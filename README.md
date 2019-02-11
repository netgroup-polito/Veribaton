# Veribaton

Veribaton is a java Spring Boot service exposing a subset of [Openbaton](http://http://openbaton.github.io/) REST API to enable smart deployment through interaction with [Verifoo](https://github.com/netgroup-polito/verifoo).

## Usage
The service is available through its REST API. To interact with operations available and their documentation it is possible to access the following URL:

`http://[veribaton_host]:[veribaton_port]/swagger`

In case default parameters have not been changed, the swagger documentation can be reached atç

`http://locahlhost:9090/swagger`

##### Network Service Descriptors API

Using the NSD API is possible to query, create, update and delete NS descriptors. Newly created descriptors will be handled through integration with Verifoo and therefore validated before upload to catalog.

An example of catalog upload can be achieved through this HTTP call:

```
 POST http://locahlhost:9090/ns-descriptors
 project-id: default
 Content-Type: application/json
```
 
 The example request body can be found under src/main/resources/demo.json.

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

## Usage
When building network services, it is possible to customize the network service and vnf descriptors in order to have it reviewed and updated from Verifoo, other than validated.
These are the guidelines of the interaction with Verifoo:
- Each node must have the property "type", and it can have the values explicitated in the [list](#Type Values Domain) below.
- Every node can be set as optional in order to Verifoo to remove it in case is not necessary for the service graph validity. This can be achieved using the configuration parameter "optional" set to "true".
- Neighbors graph in verifoo is represented using networks in the NSD. If using a single network, Verifoo will receive a network mesh between nodes,
otherwise will be considered neighbors between each other the nodes having an interface on the same network.
- Different networks will be collapsed to a single one in Verifoo to NSD conversion, due to the missing implementation of the Service Function Chaining in Openbaton.
- Firewalls can be autoconfigured: if the configuration is left empty, three different configuration parameters will be added: defaultAction (allow/deny), allow and deny.
Allow and deny represent couples of source and destination node which communication the firewall should block or permit, and they are in the form: {src},{dest};{src2},{dest2}.

## Packages description

#### it.polito.veribaton
The main class is `Application.java`. Takes care of starting up the tomcat embedded server and registering hooks for soft shutdown.
 
#### it.polito.veribaton.api.catalogue
Implements the API interface for Network Service Descriptor. The controller class is `NetworkServiceDescriptorController.java`, which handles requests for Network Services CRUD operations.

#### it.polito.veribaton.swagger
Includes the class `SwaggerController.java` which controls swagger documentation. Performs a URL rewrite to map /swagger endpoint to actual html location.

#### it.polito.veribaton.utils
The class `Converter.java` introduces methods to map Openbaton data model to Verifoo, and vice-versa.

Class `LogWriter` has utility methods to log XML ans JSON object to file.

#### it.polito.veribaton.errors
`VeribatonErrorController.java` is the request controller for errors happening in the REST interface, such as an invalid URL.

#### org.openbaton.catalogue
Defines Openbaton data model.

#### org.openbaton.exceptions
Includes all possible exceptions that could happen from the point of view of the NFVO.

----
#### Type Values Domain
- FIREWALL
- ENDHOST
- ENDPOINT
- ANTISPAM
- CACHE
- DPI
- MAILCLIENT
- MAILSERVER
- NAT
- VPNACCESS
- VPNEXIT
- WEBCLIENT
- WEBSERVER
- FIELDMODIFIER
- FORWARDER


