# Firefly Case

#  Running seperetaly

Each project has their own docker compose files, so just run:

```
$ cd OrderManagementApi
$ docker-compose up
```
#  Running together

Also there is a docker-compose file in root directory, just run:

```
$ docker-compose up
```
Regardless of how you run, these are the url,port and swagger mappings for services:

|Service Name| Url | Swagger  | 
|:-------------:|:-------------:|:-------------:|
|OrderManagementApi | http://localhost:8080| http://localhost:8080/swagger-ui.html|
|ReportingApi | http://localhost:8081| http://localhost:8081/swagger-ui.html|
|GatewayApi | http://localhost:8082| none|

# Tests

I used [Test containers](https://www.testcontainers.org/) to perform tests.
While testing rabbitmq and database (postgresql) integrations, these containers are created inside spring and endpoints are easily passed to the context. Spring context automatically connects to those instances in order to perform tests.

The biggest drawback of this is, when these tests are tried to be run in a docker environment (hence when called docker-compose up command) spring tries to create docker inside docker. This is a known issue by testcontainers, and I tried the solution, but could not accomplish to solve the issue anyway.
That is why Dockerfiles' create images with skipTests=true
But tests can be run from ide or terminal with the following command:

```
$ mvn test
```
Of course I could have deleted that implementation, run tests using docker-compose, but I wanted to keep those to show while explaining the case.

#Implementation
