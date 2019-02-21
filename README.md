# Veribaton

Veribaton is a java Spring Boot service exposing a subset of [Openbaton](http://http://openbaton.github.io/) REST API to enable smart deployment through interaction with [Verifoo](https://github.com/netgroup-polito/verifoo).

## Usage
The service is available through its REST API. To interact with operations available and their documentation it is possible to access the following URL:

`http://[veribaton_host]:[veribaton_port]/swagger`

The variable `veribaton_host` corresponds to the IP address of the server running the instance, while `veribaton_port` corresponds to the value of the variable `server.port` in application properties, as documented [below](#editable-properties).
In case default parameters have not been changed, and the service is being accessed locally, the swagger documentation can be reached at:

`http://locahlhost:9090/swagger`

### Network Service Descriptors API

Using the NSD API is possible to query, create, update and delete NS descriptors. Newly created descriptors will be handled through integration with Verifoo and therefore validated before upload to catalog.

An example of catalog upload can be achieved through this HTTP call:

```
 POST http://locahlhost:9090/ns-descriptors
 project-id: default
 Content-Type: application/json
```
 
 The example request body can be found under src/main/resources/demo.json.

## Installation
Veribaton requires Java version 1.8 or higher, a release of Verifoo after January 2018 and Openbaton >= 6.0.0 to run properly.

##### Prerequisites
 - Download and install Java as described in Oracle Java [instruction page](https://www.java.com/en/download/help/download_options.xml)
 - Download and install the latest release of Verifoo from [here](https://github.com/netgroup-polito/verifoo)
 - Install Openbaton using [instructions](https://openbaton.github.io/documentation/nfvo-installation/)
 
 Details about environment setup will be addressed [below](#env).

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
###### <a name="editable-properties"></a> Editable properties:

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

## Usage Notes
When building network services, it is possible to customize the network service and vnf descriptors in order to have it reviewed and updated from Verifoo, other than validated.
These are the guidelines of the interaction with Verifoo:
- Each node must have the property "type", and it can have the values explicitated in the [list](#types) below.
- Every node can be set as optional in order to Verifoo to remove it in case is not necessary for the service graph validity. This can be achieved using the configuration parameter "optional" set to "true".
- Neighbors graph in verifoo is represented using networks in the NSD. If using a single network, Verifoo will receive a network mesh between nodes,
otherwise will be considered neighbors between each other the nodes having an interface on the same network.
- Different networks will be collapsed to a single one in Verifoo to NSD conversion, due to the missing implementation of the Service Function Chaining in Openbaton.
- Firewalls can be autoconfigured: if the configuration is left empty, three different configuration parameters will be added: defaultAction (allow/deny), allow and deny.
Allow and deny represent couples of source and destination node which communication the firewall should block or permit, and they are in the form: {src},{dest};{src2},{dest2}.

## Contributing
In order to contribute to the project, it is possible to clone the repository from sources as described in installation steps.
The source code resides in folder `/src/main/java`. 
The project files are in the IntelliJ IDEA format, but given the nature of Spring Boot framework, any editor can be used to contribute.
Follows a description of the different packages in which is detailed the class relationship between components.

### Packages description

#### it.polito.veribaton
The main class is `Application.java`. Takes care of starting up the tomcat embedded server and registering controllers and hooks for soft shutdown.
 
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
#### <a name="types"></a> Type Values Domain
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

## <a name="editable-properties"></a>Environment Setup
#### Verifoo
This guide describes installation and setup of Verifoo 2.0 on a Ubuntu Linux 18.04LTS system.
Requirements are JDK >= 8, and Tomcat >= 8.5

- Install JDK 8 and required packages

```sh
$ sudo apt update
$ sudo apt-get install -y openjdk-8-jdk\
                        unzip wget ant
```

- Download tomcat 8.5.37
```sh
$ cd /tmp
$ wget http://www-us.apache.org/dist/tomcat/tomcat-8/v8.5.37/bin/apache-tomcat-8.5.37.zip
```

- Install Tomcat
```sh
$ unzip apache-tomcat-*.zip
$ sudo mkdir -p $HOME/tomcat
$ sudo mv apache-tomcat-8.5.37 $HOME/tomcat
$ sudo ln -s $HOME/tomcat/apache-tomcat-8.5.37 $HOME/tomcat/latest
$ chmod +x -R $HOME/tomcat/latest/bin
```

- Set tomcat home
```sh
# set tomcat home
$ sudo bash -c 'echo "export CATALINA_HOME=$HOME/tomcat/latest" >> $HOME/.profile'
$ source $HOME/.profile
```

- Create Tomcat users as required from Verifoo
```sh
$ sudo bash -c ' cat << \EOF > $HOME/tomcat/latest/conf/tomcat-users.xml
<?xml version="1.0" encoding="UTF-8"?>
<!--
  Licensed to the Apache Software Foundation (ASF) under one or more
  contributor license agreements.  See the NOTICE file distributed with
  this work for additional information regarding copyright ownership.
  The ASF licenses this file to You under the Apache License, Version 2.0
  (the "License"); you may not use this file except in compliance with
  the License.  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
-->
<tomcat-users xmlns="http://tomcat.apache.org/xml"
              xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
              xsi:schemaLocation="http://tomcat.apache.org/xml tomcat-users.xsd"
              version="1.0">

  <role rolename="manager-gui"/>
  <role rolename="manager-script"/>
  <user username="admin" password="admin" roles="manager-gui,manager-script"/>
</tomcat-users>
EOF'
```

- (Only local installation) Change Tomcat listen port in order to not conflict with Openbaton on port 8080.
This can be achieved modifying `$CATALINA_HOME/tomcat/latest/conf/server.xml`.

- Install Verifoo
```sh
$ cd $HOME
$ git clone https://github.com/netgroup-polito/verifoo.git
$ cd verifoo
$ ant start-tomcat &
```

#### Openbaton
This guide describes installation and setup of Openbaton 6.0.0 on a Ubuntu Linux 18.04LTS system.
The preferred installation method for this tool is via Docker containers, hence the need of installing docker on the system.
Required versions are: - Docker (>=18.03) - Docker Compose (>=1.20)

- Install generic software packages for repository management
```sh
$ sudo apt-get update
$ sudo apt-get install -y \
      apt-transport-https \
      ca-certificates \
      curl \
      gnupg-agent \
      software-properties-common
```
- Download docker repository key and add it to trusted vendor keys, then add docker deb repository
```sh      
$ curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo apt-key add -
$ sudo add-apt-repository \
     "deb [arch=amd64] https://download.docker.com/linux/ubuntu \
     $(lsb_release -cs) \
     stable"
 ```
 - Install Docker Community Edition and Docker Compose
 ```sh
$ sudo apt-get update
$ sudo apt-get install docker-ce -y
$ sudo apt-get install docker-compose -y
```

- Add current user to docker group in order to run docker commands without sudo. This will be needed upon Openbaton VIM configuration in order to allow unix domain socket communication.
```sh
$ sudo usermod -aG docker $USER
```
- Log out and log in again to finalize group membership

- Download Openbaton docker-compose yaml file and launch it. The HOST_IP environment variable is just for local development as it corresponds to docker default bridge, for multi-machine or non standard deployments, change it to the current host ip.
```sh
$ curl -o docker-compose.yml https://raw.githubusercontent.com/openbaton/bootstrap/6.0.0/docker-compose.yml | env HOST_IP=172.17.0.1  docker-compose up -d
```

- In order to verify installation succeeded you can access: `http://localhost:8080` and enter using username: `admin` and password `openbaton`.