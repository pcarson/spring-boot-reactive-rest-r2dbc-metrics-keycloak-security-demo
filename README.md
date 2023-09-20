# springboot 3 reactive rest + r2dbc/metrics/grafana demo secured by keycloak security

### overview
This demo repository contains a springboot 3, spring 6 maven project which

* exposes a Spring Reactive REST API to maintain a 'user' object
  * NOTE that [as per ](https://docs.spring.io/spring-framework/docs/5.0.0.RC4/spring-framework-reference/reactive-web.html) the decision was taken to implement RequestMapping interface rather than RouterFunctions, to support:
    * OpenAPI
    * Returning RequestEntity structures as the response to expose HTTP return codes programmatically 
* uses an in-memory version of an H2 database to store the data received over the REST API, using
  * R2DBC drivers to support reactive interfaces
  * Configuring r2dbc consists of ...
    * Configuring a ReactiveCrudRepository
    * resources/schema.sql to create the relevant table, as we don't have JPA to do it for us ...
    * AutoConfigure provides a connection manager from the provided r2dbc properties
* sends metrics to a configured influxDB using the micrometer libraries 
* exposes swagger/open-api interface information - once started, see [here](http://localhost:8080/swagger-ui.html)

HOWEVER - it will only do this if correctly secured with a Keycloak JWT token.
Access to all the REST API endpoints is secured to configured keycloak users who have the 'springboot-user' role.
Luckily, the preconfigured keycloak instance already contains a suitable realm and user configured to go ...

### development environment: <a name="environment"></a>
This code was developed and tested on:
```agsl
* Linux 5.15.0-71-generic #78-Ubuntu x86_64 GNU/Linux
* OpenJDK Runtime Environment (build 17.0.6+10-Ubuntu-0ubuntu122.04)
```

### docker-compose
The docker-compose-keycloak.yml file contains configuration for starting a preconfigured
keycloak instance, with a predefined 
* springboot realm
* test-user (keycloak user) with the springboot-user role

for the moment we need to start both docker-compose configurations seperately, as combining them causes keycloak to fail on startup.
Watch this space ...

The docker-compose.yml file contains configuration for
* starting an influx DB docker container
* starting a grafana docker container which is configured to 
  * access influx by default
  * expose a default dashboard of metrics from the demo springboot application
* starting the demo springboot project

It will start all containers locally. The springboot app contains default connectivity to influxDB, as does grafana

The grafana dashboard can be viewed, once started, see [here](http://localhost:3000)

### to see the demo ......
* you'll need docker/docker-compose installed locally - you'll have to check that out yourself ..
* clone this repository to your local disk
* build the springboot demo. If you want to do this yourself, you'll need java 17 installed, and if not using the maven wrapper mvnw mentioned below, a local install of maven
  * on the command line in the root directory (i.e. the same directory as the 'pom.xml' file), type
```agsl
./mvnw clean install
```
* once the build finishes, there should be a file 'spring-boot-rest-jpa-metrics-demo-0.0.1-SNAPSHOT.jar' visible in the target directory
* on the command line in the root directory (i.e. the same directory as the 'docker-compose.yml' file), type
```agsl
docker-compose build
```
* this instruction prepares the docker containers necessary to run the demo
* when this instruction is complete, type:
```agsl
docker-compose -f docker-compose-keycloak up
docker-compose up
```
* this should start all of the necessary containers
* NOTE that keycloak has to be running before the demo app will start

Once all containers are running, you can proceed to add some data over the demo REST API

FIRST retrieve a valid JWT token from keycloak on localhost:7070, for the configured user test-user:
* NOTE that the basic Authorization below is derived from the client Id and client credentials for the springboot-client pre-defined on Keycloak

```agsl
curl --request POST \
  --url http://localhost:7070/auth/realms/springboot/protocol/openid-connect/token \
  --header 'Authorization: Basic c3ByaW5nYm9vdC1jbGllbnQ6NkpCcXgwT0lTVXpZR2xHYWZPS3p0MUVERjZwNmFuZmQ=' \
  --header 'Content-Type: application/x-www-form-urlencoded' \
  --cookie 'JSESSIONID=EEC583DEF33C0360AC4E1514EDC93D4E.1d1fd1190151; JSESSIONID=E5FC47491FA40843E4E16839F8B706FF' \
  --data username=test-user@test.com \
  --data password=password123 \
  --data grant_type=password
```

this should return an access token, such as:
```agsl
"access_token": "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICI3RDI0ZEVEdTB4QzdfVzdRWDZkTTd1UTZhcGQ2RWlBNDZ4R0Q5NkdyMTRVIn0.eyJleHAiOjE2OTExNjY4MDcsImlhdCI6MTY5MTE2NjIwNywianRpIjoiNzg1OWQ0M2UtNzQ4NC00MGQxLWEyMTYtYTJkYmVkNGQxYWUzIiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo3MDcwL2F1dGgvcmVhbG1zL3NwcmluZ2Jvb3QiLCJhdWQiOiJhY2NvdW50Iiwic3ViIjoiZTdkMmE2MmEtZDcwOS00MDY5LTk4ODItMzk3YjAyM2M5YmQ1IiwidHlwIjoiQmVhcmVyIiwiYXpwIjoic3ByaW5nYm9vdC1jbGllbnQiLCJzZXNzaW9uX3N0YXRlIjoiZGQxYTNhMTctZmJlMS00ZWU3LTk5NzgtOTYyMjQ3N2Y0OWE1IiwiYWNyIjoiMSIsInJlYWxtX2FjY2VzcyI6eyJyb2xlcyI6WyJzcHJpbmdib290LXVzZXIiLCJvZmZsaW5lX2FjY2VzcyIsInVtYV9hdXRob3JpemF0aW9uIiwiZGVmYXVsdC1yb2xlcy1zcHJpbmdib290Il19LCJyZXNvdXJjZV9hY2Nlc3MiOnsiYWNjb3VudCI6eyJyb2xlcyI6WyJtYW5hZ2UtYWNjb3VudCIsIm1hbmFnZS1hY2NvdW50LWxpbmtzIiwidmlldy1wcm9maWxlIl19fSwic2NvcGUiOiJwcm9maWxlIGVtYWlsIiwic2lkIjoiZGQxYTNhMTctZmJlMS00ZWU3LTk5NzgtOTYyMjQ3N2Y0OWE1IiwiZW1haWxfdmVyaWZpZWQiOmZhbHNlLCJuYW1lIjoidGVzdCB1c2VyIiwicHJlZmVycmVkX3VzZXJuYW1lIjoidGVzdC11c2VyIiwiZ2l2ZW5fbmFtZSI6InRlc3QiLCJmYW1pbHlfbmFtZSI6InVzZXIiLCJlbWFpbCI6InRlc3QtdXNlckB0ZXN0LmNvbSJ9.SnQvSAlBZAu0RG7fLH_uzKrTl935fQLf9Sl88tdp03l2zinDXbbqBIZfiCfXcpQ6GmH01QdsHaYSTu3fpF6nrn_EHQ5Y0eOJom1tScvc625HIbiwd2C__5tHDLR9Cs17jAYOEvwb_2kiHCRqv7HRzm1-8feZJL2_2lPREcpEMv4YDG3k2NqyyQh2Z4zJ7I-hGt3tdzcEMUtcErnYauyBNMnsmmChrIdjIXy8g-PrK12FGIv0StBMN7cCKEGwvo5X86K2w5oxuMIpu6XjJ8TtZ45JkYoGP9v7ig2EnJR3qmfBs_tYBIXBxCIZsSiWmV_Wwyai6BMuyyIRc9lhfP2VEw"
```

This access token can then be used as a Bearer token to access the API endpoints that have been secured.

* click [here](http://localhost:8080/swagger-ui.html) to access and use the Open API documentation to add some data 
  * Add Authorisation:
    * Find the Authorize button and add the access token just generated above. This will apply for all requests below.
  * Add some data:
    * Find the POST /users option and select 'Try it out'
    * remove the "id" attribute, as R2dbc will attempt to update rather than add an item where an "id" is specified ....
    * Click on the 'Execute' button a few times
  * Query the data you've just added
    * Find the GET /users option and click 'Try it out'
    * Click on the 'Execute' button a few times
  * Remove the Authorization access token by selecting 'Logout'
  * Try everything again - all REST API requests should now be Unauthorized with a 401 return code
* This activity will now be visible in grafana, having been stored in the influxDB
  * click [here](http://localhost:3000) and then find and click on the springboot-demo dashboard
* Err ... that's it

### tests and code coverage
The project is also configured to produce code coverage data using the jacoco maven plugin.
After a build, this information can be found here: target/site/jacoco/index.html

### Sonar
Sonar can be built stand-alone as detailed below if you have access to an instance.
NB You'll need to update the 'sonar' properties in the pom.xml file to identify the host and login token to be used when sending analysis to sonar.

```$xslt
mvn clean org.jacoco:jacoco-maven-plugin:prepare-agent install -DskipITs=true
mvn sonar:sonar
```