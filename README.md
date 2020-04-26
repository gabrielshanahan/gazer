# Gazer

Gazer is a simple application that makes periodic HTTP requests to a list of endpoints and
stores the results. In our parlance, it starts a *gazer* for every endpoint. A *gazer* periodically *gazes* at the 
endpoint and records what it sees.

The app runs on top of MySQL and exposes a REST API allowing CRUD operations on the list of endpoints, as well as
listing the results. It is written in Kotlin and Spring, built using Gradle, and can be run locally via. Gradle
tasks or in containers using `docker-compose`.

This application was created purely for educational purposes and **should by no means be used in a production environment**.

* [Get up and running](#get-up-and-running)
* [Usage](#usage)
  * [Examples:](#examples-)
* [Design decisions](#design-decisions)
* [Architecture](#architecture)
  * [Services](#services)
  * [Containers](#containers)
  * [Modules](#modules)
* [Project documentation](#project-documentation)
  * [Run](#run)
  * [Build & Tooling](#build---tooling)
* [Module documentation](#module-documentation)
  * [Func](#func)
  * [Data](#data)
  * [API](#api)
  * [Gazer](#gazer-1)


## Get up and running
Clone the repository and build the modules:
```bash
./gradlew bootJar
```

Then, setup the root and user database passwords via the appropriate environment variables:
```bash
export MYSQL_ROOT_PASSWORD=<...>
export MYSQL_GAZER_PASSWORD=<...>
```

You can optionally configure the user and database names:
```bash
MYSQL_GAZER_USER (default=gazer)
MYSQL_GAZER_DATABASE (default=gazer)
```

Finally, run the services:
```bash
./gaze
```

This calls `docker-compose` under the hood. If you need to rebuild the images, run:
```bash
./gaze --build
```

Alternatively, the service can also be started locally with an H2 database. First:
```bash
./gradlew :data:bootRun
```

After this service has started, start the remaining two in any order
```bash
./gradlew :api:bootRun
./gradlew :gazer:bootRun
```

## Usage
The API is available at `localhost:8080`. Authentication is done via token in a `GazerToken` header. There are two
tokens hardcoded into the application to make testing simpler.

| User       | Token                                |
|------------|--------------------------------------|
| Applifting | 93f39e2f-80de-4033-99ee-249d92736a25 |
| Batman     | dcb20f8a-5657-4f1b-9f7f-ce65739b359e |


The API is HATEOAS compliant and returns HAL-FORMS JSON making discovery easy:
```bash
curl -i localhost:8080 -H "Content-Type: application/json" -H "GazerToken: dcb20f8a-5657-4f1b-9f7f-ce65739b359e"
```

### Examples:
```bash
curl -i localhost:8080/monitoredEndpoints -X POST -d '{"name":"Applifting homepage", "url":"http://www.applifting.cz", "monitoredInterval":10}' -H "Content-Type: application/json" -H "GazerToken: dcb20f8a-5657-4f1b-9f7f-ce65739b359e"
curl -i localhost:8080/monitoredEndpoints/<endpoint_id>/monitoringResults?limit=10 -H "Content-Type: application/json" -H "GazerToken: dcb20f8a-5657-4f1b-9f7f-ce65739b359e"
```

## Design decisions
1) The API and gazing services should be entirely independent of one another, since architectural requirements such as scalability and availability
for these components are completely different. As a consequence, the gazing service will interact directly with database repositories.
2) Heavy usage of the user-facing component is not anticipated to be very high and is therefore implemented in a blocking, synchronous
manner
3) The gazing functionality is basically completely IO bound, so an effort is made to be as non-blocking & asynchronous as
possible. This is limited by the blocking nature of JDBC.
4) All data-constraints are defined in the business layers. No data-constraints (apart from non-nullability and data type) are defined at the persistence level. This is for the same reason that they wouldn't be defined as part of the specification of pen and paper.  


## Architecture
The app consists of 3 Spring services implemented across 4 modules and runs in 4 Docker containers.

### Services
* `data` - Responsible for initializing the database, creating the schema and loading the hardcoded users. Must not be started before DB is up and running.
* `api` - Publishes the REST API and deals with all requests. Must not be started before `data has done its thing.
* `gazer` - Runs the gazers. Must not be started before `data` has done its thing.

### Containers
* `db` - Spins up a MySQL database on `3306`.
* `data` - Waits until MySQL is up and running, then starts the `data` service.
* `api` - Waits until `data` is up and running, then starts the `api` service.
* `gazer` - Waits until `data` is up and running, then starts the `gazer` service.

### Modules
* `data` - Responsible for initializing the database and owns all database entities and repositories.
* `api` - Owns the implementation of the REST API. Depends on `data` and `func`.
* `gazer` - Owns the implementation of all gazing functionality. Depends on `data` and `func`.
* `func` - Stuffed chock-full with utility functions. Specifically, two of them. Even more specifically, a [blue and red version](https://journal.stuffwithstuff.com/2015/02/01/what-color-is-your-function/) of the same thing.

## Project documentation
This section aims to give a high-level overview of the application and highlight important or non-obvious pieces of
information.

---

### Run
* The Spring services implement a *dev* and *prod* profile to facilitate configurations for local runs using
Gradle (*dev*) vs. using docker-compose (*prod*).
* The *prod* profile connects to a MySQL instance, while the *dev* profile uses a H2 instance with the console enabled
at `http://localhost:8081/h2-console/` (the port is different to prevent clashes with the api service).
* When starting the application using the methods described herein, the profiles are set automatically (see `Dockerfile`
and `<service>/build.gradle.kts`).
#### Docker
* A `gaze` bash file checks if all the necessary environment variables are set and then runs `docker-compose up`, 
passing on any arguments it was called with.
* A single parametrised `Dockerfile` is used to build all the images.
* The docker builds take advantage of the
[layered jars](https://spring.io/blog/2020/01/27/creating-docker-images-with-spring-boot-2-3-0-m1) functionality
supported in Spring Boot 2.3.0, which is one of two reasons why we decided to use this release (the other was
[improved R2DBC support](https://docs.spring.io/spring-data/r2dbc/docs/1.1.0.RC1/reference/html/#r2dbc.repositories.queries), 
which ended up not getting used because it didn't play well when used alongside JPA). At the time of writing, the
release is not yet GA.
* We use a wait script (see `scripts/`) to wait for initialization of dependent containers. The script repeatedly tests if a given
`host:port` combination becomes available within a certain timeout period, which can be customized in `docker-compose.yml`.
* The MySQL user is setup via the contents of `data/scripts`, which are copied to
[docker-entrypoint-initdb.d](https://hub.docker.com/_/mysql). All services currently use the same database user.
* Volumes are not used, so `docker-compose down` removes the data. Use `docker-compose stop` if you want to keep it between runs.
#### Gradle
* When the *dev* profile is active, the data service spins up and exposes a H2 instance, to which the other services connect. Therefore, it is necessary to
start the data service first (`./gradlew :data:bootRun`) and only after it has finished booting can the other services
be started (`./gradlew :api:bootRun` and `./gradlew :gazer:bootRun`). No script is provided for this, since the expectation
is that while developing, you want to execute these tasks in separate terminals to be able to read the applications output.

---

### Build & Tooling
* We use **Gradle** configured via Kotlin scripts as a build tool, **JUnit5** for testing, **Jacoco** for test coverage, **Dokka** for
doc generation, **ktlint** for linting, **detekt** for static analysis and **SonarQube** for additional validation and visualisation.
#### Gradle
* The configuration is spread across several `build.gradle.kts` files - one located at the root of the project, the
rest in each module.
* The modules are defined in `settings.gradle.kts`, along with the repository for the Spring Boot release.
* Tasks spanning multiple modules are run in parallel (see `gradle.properties`).
* The root `build.gradle.kts` contains configurations and plugins used throughout the project. Some of those are necessary to make Spring and Kotlin play nice together.
* Services are built using the `bootJar` gradle task, which creates a `<module>-<version>-boot.jar` file in `<module>/build/libs`.
* If a module is needed as a regular dependency, it is built using the `jar` task, which creates a `<module>-<version>.jar` file in `<module>/build/libs`.
#### JUnit5
* JUnit is configured to run tests concurrently.
#### [ktlint](https://ktlint.github.io/)
* Via. [kotlinter](https://github.com/jeremymailen/kotlinter-gradle) plugin.
* Adds `lintKotlin` and `formatKotlin` tasks. The former just lints, the latter also does the necessary formatting.
* Any custom `.editorconfig` is natively respected. 
#### [detekt](https://arturbosch.github.io/detekt/)
* Adds the `detekt` task, along with [various additional](https://arturbosch.github.io/detekt/groovydsl.html#available-plugin-tasks) `detekt*` tasks.
* The config can be found at `config/detekt/detekt.yml`.
* Allows generating a baseline (ie. a snapshot of all current issues, which are subsequently ignored), located at `<module>/config/detekt/baseline.xml`.
* We include a wrapper around ktlint.
#### [SonarQube](https://www.sonarqube.org/) (skip if not using)
* Kotlin is now supported by SonarQube, which wasn't always the case. This is why a [3rd party plugin](https://github.com/arturbosch/sonar-kotlin) was created some time ago to interface detekt and SonarQube.
  * Unfortunately, it conflicts with the built-in plugin - if the 3rd party plugin is installed, the built-in plugin must be disabled.
  * Therefore, we present two different ways to get SonarQube up and running - vanilla flavour, which uses the builtin Kotlin plugin, and Detekt flavour, which uses the detekt-plugin.
  
##### Vanilla SonarQube
* Download [this `docker-compose.yml` file](https://gist.github.com/Warchant/0d0f0104fe7adf3b310937d2db67b512)
* Run `docker-compose up`
* Wait for initialization of SugarQube on `localhost:9000`
* Login using admin/admin (only necessary to generate token, see bellow)

##### Detekt SonarQube
* `git clone https://github.com/gabriel-shanahan/sonar-kotlin.git`
* Run `docker-compose up` on the included `docker-compose.yml`
* Wait for initialization of SugarQube on `localhost:9000`
* Login using `admin/admin`
* Open Administration, and select the Kotlin language. Change the file extension from `.kt` to anything that is not a real file extension (only necessary to do this once)

##### SonarQube with Gradle (only necessary once):
* Under your profile, click Security and add a token (name it whatever you want)
* Create/update `~/.gradle/gradle.properties` with the following contents (it is also possible to do this on a per project basis, but not really necessary in this case):

```bash
# gradle.properties
systemProp.sonar.host.url=http://localhost:9000

#----- Token generated from an account with 'publish analysis' permission
systemProp.sonar.login=<insert_token_here>
```

The default SonarQube Gradle task also runs tests, and doesn't finish if tests fail. To get around that, instead of running SonarQube from Gradle, run the following command in the project root:
```bash
./gradlew sonarqube -x test
```

## Module documentation

This section aims to give a high-level overview of every module and highlight important or non-obvious pieces of
information. It is recommended to browse through before looking at the code.

### Func
* Contains the `into` function (and its suspending variant), which is basically a piping operator. It is used throughout the project to decrease the amount of nesting needed.

---

### Data
#### Structure
* Contains 2 packages:
  * The **model** package contains JPA entities. A common `AbstractEntity` ancestor defines functionality to auto-generate binary UUIDs.
  * The **repository** package defines corresponding JPA repositories, along with functions necessary in the `api` and `gazer` modules.
#### Implementation
* Contains definitions for 3 tables (`Users` -1:N- `MonitoredEndpoint` -1:N- `MonitoringResults`) and 3 corresponding repositories.
* The repositories implement queries for entities based on their parent, while users are searched for by token (see the api module docs bellow).
#### Misc
* Contains `resources/*.sql` files that initialize the MySQL/H2 databases with the hardcoded users, with the proper one being [selected by Spring Boot via. the `spring.datasource.platform` property](https://docs.spring.io/spring-boot/docs/current/reference/html/howto.html#howto-initialize-a-database-using-spring-jdbc).
* Exposes a H2 server on `9092` under the dev profile. Restricting the profile turns out to be non-optional, otherwise `gazer` tests will fail unless executed one class at a time, because the server gets started up for every class. This is a consequence of the way we are forced to setup the application context for `gazer` tests (see the `gazer` module docs bellow for more info). 
* Also exposes a web server on `8081`. Web dependencies need to be included for the H2 console to work, but we can also use this to find out when the service is up and running, i.e. when the DB is initialized correctly. When running via. `docker compose`, the dependent containers wait until a web server is reachable on this port.
  * The port is changed from `8080` to avoid clashes with the `api` service when running everything locally.
* Logger format is changed to be consistent with the `gazer` module, where we need to show the entire thread and coroutine names (default settings truncate the beginnings).

---

### API
#### Nomenclature
* Objects which directly represent database tables are called *entities* (these are contained in the `data` module).
* Module-specific adapters for *entities* are called *models* - they loosen the coupling between the database schema and the data domain of the module.
* Data computed by an endpoint, which is then enriched by links to related endpoints, is called a *resource*.
* A *response* is what actually gets sent back, i.e. a *resource* + optional headers, HTTP status code, etc.
#### Structure
* Contains 4 packages:
  * The **controller** package contains code that handles incoming requests. It contains `RestController`s, defines a small DSL and contains two additional packages:
    * The **resource** package contains code that creates *resources* out of *models*/*collections*, in accordance with HATEOAS.
    * The **response** package contains code that creates *responses* out of *resources*.
  * The **exceptions** package defines domain-specific exceptions and handlers thereof.
  * The **model** package contains the *models*, functions for transforming between *models* and *entities*, and defines constraints on the data, i.e. which properties can be set and what values are acceptable. 
  * The **validation** package contains code that handles validations, along with a custom NullOrNotBlank validation and OnCreate validation group.
#### Implementation
* To facilitate simple testing, authentication is done by comparing a token sent in a `GazerToken` header to a hardcoded token in the database. This is done manually by each method of the controllers.
  * A (pretty lengthy) attempt was made to create a custom scheme using Spring Security that would support this method of authentication. One of the main motivations was the consequent ability to use Spring Data REST and get HATEOAS compliance OOTB. We actually almost succeeded, but due to problems with configuring endpoints which should be ignored combined with low confidence the solution wouldn't cause problems down the road, we decided to abandon this approach in the end. The work done can be found using `git log --full-history -- api/src/main/kotlin/io/github/gabrielshanahan/gazer/api/security`.
* All endpoints return `HAL-FORMS JSON` on success, and regular `JSON` on error. Implementing [problems](https://docs.spring.io/spring-hateoas/docs/1.1.0.M3/reference/html/#mediatypes.http-problem) would be one way of unifying this.
* The `RestController` for MonitoredEndpoints define standard CRUD endpoints, as well as an endpoint to list results related to a particular endpoint
* The `RestController` for MonitoringResults contains only endpoints for retrieval. This is a partial consequence of design decision (1) - gazers won't be using the API, and we don't want users to have the ability to manipulate results, since that kind of goes against the purpose of this app.
* A common ancestor for both endpoints is provided solely to have a single place for defining dsl-specific extension functions that are used in both controllers.
* The DSL includes functions for constructing `HAL-FORMS` links. Spring already offers such a [DSL](https://github.com/spring-projects/spring-hateoas/tree/0e02d4f04117e03ab94110c9de09b2ac28d55599/src/main/kotlin/org/springframework/hateoas/server/mvc), but at the time of writing it was found to behave in unintuitive ways.
#### Data-constraints
* The motivation for choosing a minimal monitored interval was to prevent a hypothetical customer using a hypothetical production-ready version of this app from turning it into a DoSing tool.
#### Misc
* Logger format changed to be consistent with the `gazer` module, where we need to show the entire thread and coroutine names (default settings truncate the beginnings).
#### Tests
* Only simple tests for CRUD operations and validation where written. There are no tests for the HATEOAS parts.
* Running the tests concurrently would sometimes cause some of them to fail randomly, most likely because Spring uses the same repository beans in all tests. This is the reason the HTTPGetTest test lifecycle is `per class` and apparently things seem to be working, but if any problems occur, just run the tests sequentially. Tests would have to be completely redesigned to address this issue properly.

---

### Gazer
#### Nomenclature
* Conceptually, a *gazer* is code that periodically monitors one specific endpoint, i.e. every endpoint has its own *gazer*. The manifestation of a *gazer* is a coroutine.
* A *model* has the same meaning as in the `api` module.
#### Structure
* Contains 4 packages:
  * The `actor` package contains the actor responsible for persisting the gazer results.
  * The `model` package contains the *models*, functions for transforming between *models* and *entities*, along with some logging helper functions.
  * The `properties` package contains definitions of custom configuration properties for the `gazer` module.
  * The `service` package contains code implementing the actual gazing, i.e. HTTP request execution.
* The code which actually brings all the above together is contained in `GazerApplication`. 
#### Implementation
* The functionality is implemented as a `ComandLineRunner`.
* When the service starts, one gazer per endpoint is launched. The database is periodically polled for the list of all endpoints and the results compared with the current sets of gazers, which are added/removed/updated as necessary. The default polling period is `1s` and can be controlled by the `gazer.syncRate` property.
  * Periodically fetching all the endpoints from the database would not scale well. An ideal design would facilitate communication of create/update/delete events across services through a messaging system.
* A `supervisor scope` is used to prevent the failure of one gazer to affect the others.
* Gazers don't deal with persistance, instead sending the results to a Kotlin `channel`. There are multiple reasons for this, ranging from separation of concerns to the fact that JDBC is blocking, and even if it wasn't, we can't guarantee a monitored interval if it's dependent on environmental factors such network latency, DB load etc., by design. 
* A very simple actor based model is used to implement the `channel`. Kotlin `actors` are used for this, even though they are marked as obsolete. The reason is that we would just end up reimplementing exactly what is already there.
* Backpressure between the actor and gazers is controlled by a buffer on the side of the actor. If the buffer is full, gazers attempting to send a new result `suspend` until room is made. By default, the buffer size is `1024` and can be controlled by the `gazer.bufferSize` property. If set to a negative number, the buffer becomes unlimited.
  * Currently, the actor processes and persists one message at a time. A low-hanging-fruit optimization would be to batch-process all the messages in the buffer. 
#### Misc
* The logging helper functions that construct truncated string representations of endpoints/results unfortunately cannot be easily parametrised by configuration properties, because we have no way to inject beans into top-level functions.
* Logger format is changed to show the entire thread and coroutine names (default settings truncate the beginnings).
#### Tests
* Kotlin `channels` appear to completely break Spring tests - executing tests in a standard test environment sends the whole thing into an infinite loop even before the tests start executing. Therefore, we have to run the tests in an application context where the `channel` is not loaded as a bean. This makes the tests very cumbersome, and also causes certain beans to be reloaded for every class. This requires special configuration (see `application.properties` under `test/`) and caused crashes when the H2 server in the `data` module was exposed for all profiles.
* Due to MockK having problems with mocking generic classes, we weren't able to mock the HTTP request and the corresponding tests actually have to do the request.
