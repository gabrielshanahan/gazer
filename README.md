# Gazer

Gazer is a simple application written that makes periodic HTTP requests to a list of endpoints and
stores the results. In our parlance, it starts a *gazer* for every endpoint who periodically *gazes* at the endpoint and 
records what it sees.

The app runs on top of MySQL and exposes a REST API allowing CRUD operations on the list of endpoints, as well as
listing the results. It is written in Kotlin and Spring, built using Gradle, and can be started up via. Gradle
tasks or in containers using `docker-compose`.

This application was created purely for educational purposes and **should by no means be used in a production environment**.

## Get up and running
Clone the repository and build the modules:
```bash
./gradlew bootJar
```

Then, run set the root and user passwords by setting the appropriate environment variables:
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

This calls docker-compose under the hood. If you need to rebuild the images, run:
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
The API is available at localhost:8080. Authentication is done via token in a GazerToken header. There are two
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
* The interface with users should be completely independent of gazing functionality, since scaling and availability requirements
for these components are fundamentally different
* Heavy usage of the user-facing component is not anticipated to be very high and is therefore implemented in a blocking, synchronous
manner
* The gazing functionality is basically completely IO bound, so an effort is made to be as non-blocking & asynchronous as
possible. This is limited by the blocking nature of JDBC.


## Architecture
The app consists of 3 Spring services implemented across 4 modules and runs in 4 Docker containers.

### Services
* data - Responsible for initializing the database, creating the schema and loading the hardcoded users. Must not be started before DB is up and running.
* api - Publishes the REST API and deals with all requests. Must not be started before data has done its thing.
* gazer - Runs the gazers. Must not be started before data has done its thing.

### Containers
* db - Spins up a MySQL database on 3306
* data - Waits until MySQL is up and running, then starts the data service
* api - Waits until data is up and running, then starts the api service
* gazer - Waits until data is up and running, then starts the gazer service

### Modules
* data - Responsible for initializing the database and owns all database entities and repositories
* api - Owns the implementation of the REST API. Depends on data and func.
* gazer - Owns the implementation of all gazing functionality. Depends on data and func
* func - Stuffed chock-full with utility functions. Specifically, two of them. Even more specifically, a [blue and red version](https://journal.stuffwithstuff.com/2015/02/01/what-color-is-your-function/) of the same thing.

## Documentation
This section aims to give a high-level overview of the application and highlight important or non-obvious pieces of
information. It is recommended to browse through before looking at the code.

### Run
* The Spring services implement a *dev* and *prod* profile to facilitate configurations for local runs using
Gradle (*dev*) vs. using docker-compose (*prod*)
* The *prod* profile connects to a MySQL instance, while the *dev* profile uses a H2 instance with the console enabled
at http://localhost:8081/h2-console/ (the port is different to prevent clashes with the api service)
* When starting the application using the methods described herein, the profiles are set automatically (see Dockerfile
and <service>/build.gradle.kts)
#### Docker
* A `./gaze` bash file checks if all the necessary environment variables are set and then runs docker-compose up, 
passing on any arguments it was called with
* A single parametrised Dockerfile is used to build all the images
* The docker builds take advantage of the
[layered jars](https://spring.io/blog/2020/01/27/creating-docker-images-with-spring-boot-2-3-0-m1) functionality
supported in Spring Boot 2.3.0, which is one of two reasons why we decided to use this release (the other was
[improved R2DBC support](https://docs.spring.io/spring-data/r2dbc/docs/1.1.0.RC1/reference/html/#r2dbc.repositories.queries), 
which ended up not getting used because it didn't play well when used alongside JPA). At the time of writing, the
release is not yet GA.
* We use a wait script (see scripts/) to wait for initialization of dependent containers. The script repeatedly tests if a given
host:port combination becomes available within a certain timeout period, which can be changed in docker-compose.yml.
* The MySQL user is setup via the contents of data/scripts, which are copied to
[docker-entrypoint-initdb.d](https://hub.docker.com/_/mysql). All services use the same user.
* Volumes are not used, so `docker-compose down` removes the data. Use `docker-compose stop` if you want to keep it between runs.
#### Gradle
* The data service spins up and exposes a H2 instance, to which the other services connect. Therefore, it is necessary to
start the data service first (`./gradlew :data:bootRun`) and only after it has finished booting can the other services
be started (`./gradlew :api:bootRun` and `./gradlew :gazer:bootRun`). No script is provided for this, since the expectation
is that while developing, you want to execute these tasks in separate terminals and able to read the applications output

### Build & Tooling
* We use Gradle configured via Kotlin scripts as a build tool, JUnit for testing, Jacoco for test coverage, ktlint for
linting, detekt for static analysis and Sonarqube for additional validation and visualisation
#### Gradle
* The configuration is spread across several `build.gradle.kts` files - one located at the root of the project, the
rest in each module
* The modules are defined in settings.gradle.kts, along with the repository for the Spring Boot release.
* Tasks spanning multiple modules are run in parallel (see gradle.properties).
* The root build.gradle.kts contains configurations and plugins used throughout the project. Some of those are necessary to make Spring and Kotlin play nice together.
* Services are built using the `bootJar` gradle task, which creates a `<module>-<version>-boot.jar` file in <module>/build/libs
* If a module is needed as a regular dependency, it is built using the `jar` task, which creates a `<module>-<version>.jar` file in <module>/build/libs
#### [ktlint](https://ktlint.github.io/)
* Via. [kotlinter](https://github.com/jeremymailen/kotlinter-gradle) 
* Adds `lintKotlin` and `formatKotlin` tasks. The former just lints, the latter also does the necessary formatting.
* ktlint respects `.editorconfig`
#### [detekt](https://arturbosch.github.io/detekt/)
* Adds `detekt` task, along with [various additional](https://arturbosch.github.io/detekt/groovydsl.html#available-plugin-tasks) `detekt*` tasks.
* The config can be found at `config/detekt/detekt.yml`
* Allows generating a baseline (ie. a snapshot of all current issues, which are subsequently ignored), located at `<module>/config/detekt/baseline.xml`
* We include a wrapper around ktlint
#### [SonarQube](https://www.sonarqube.org/)
* Kotlin is now supported by SonarQube, which wasn't always the case. This is why a [3rd party plugin](https://github.com/arturbosch/sonar-kotlin) was created some time ago to interface detekt and SonarQube.
  * Unfortunately, it conflicts with the built-in plugin - if the 3rd party plugin is installed, the built-in plugin must be disabled
  * Therefore, we present two different ways to get SonarQube up and running - vanilla flavour, which uses the builtin Kotlin plugin, and Detekt flavour, which uses the detekt-plugin
  
##### Vanilla SonarQube
* Download [this docker-compose.yml file](https://gist.github.com/Warchant/0d0f0104fe7adf3b310937d2db67b512)
* Run `docker-compose up`
* Wait for initialization of SugarQube on localhost:9000
* Login using admin/admin (only necessary to generate token, see bellow)

##### Detekt SonarQube
* `git clone https://github.com/gabriel-shanahan/sonar-kotlin.git`
* Run `docker-compose up` on the included docker-compose.yml
* Wait for initialization of SugarQube on localhost:9000
* Login using admin/admin
* Open Administration, and select the Kotlin language. Change the file extension from .kt to anything that is not a real file extension (only necessary to do this once)

##### SonarQube with Gradle (only necessary once):
* Under your profile, click Security and add a token (name it whatever you want)
* Create/update `~/.gradle/gradle.properties` with the following contents (it is also possible to do this on a per project basis, but not really necessary here):

```bash
# gradle.properties
systemProp.sonar.host.url=http://localhost:9000

#----- Token generated from an account with 'publish analysis' permission
systemProp.sonar.login=<insert_token_here>
```

The default SonarQube Gradle task also runs tests, and fails if tests fail. To get around that, instead of running SonarQube from Gradle, run the following command in the project root:
```bash
./gradlew sonarqube -x test
```

### Func
* Contains the `into` function (and its suspending variant), which is basically a piping operator. This used throughout the code, which often decreases the amount of nesting needed

### Data
#### Code
* Contains 2 packages: model and repository
  * The model package contains JPA entities. A common AbstractEntity ancestor defines functionality to auto-generate binary UUIDs.
  * The repository package defines corresponding JPA repositories, along with functions necessary in the api and gazer modules.
* Contains resources/*.sql files that initialize the MySQL/H2 databases with the hardcoded users, with the proper one being [selected by Spring Boot via. the `spring.datasource.platform` property](https://docs.spring.io/spring-boot/docs/current/reference/html/howto.html#howto-initialize-a-database-using-spring-jdbc).
* Exposes a H2 server on 9092 under the dev profile
* Also exposes a web server on 8081. Web dependencies need to be included for the H2 console to work, but we can also use this to find out when the service is up and running, i.e. when the DB is initialized correctly. When running via. docker compose, the dependent containers wait until a web server is reachable on this port.
  * The port is changed from 8081 to avoid clashes with the API service when running everything locally.
* Logger format changed to be consistent with the gazer module, where we need to show the entire thread and coroutine names (default settings truncated the beginnings).
#### Tests
* JUnit is configured to run tests concurrently

### API
* 
  
  

## Potential for improvement (in no particular order):
* [Problems](https://docs.spring.io/spring-hateoas/docs/1.1.0.M3/reference/html/#mediatypes.http-problem)
* Advanced configuration using ENVs
* R2DBC in gazer
* Ability to gaze from multiple locations
  

 

## About
- Uses layered jars to achieve faster builds
- Uses docker-entrypoint-initdb.d from mysql docker image to setup gazer user for mysql, see docker-compose.yml
- ./gaze checks for necessary environment variables and runs docker-compose up
- docker-compose down deletes data, stop does not
- If timeout for database startup is not enough, edit it in wfi_args in docker-compose.yml
- has dev and prod profile - dev uses H2, and is selected automatically when running bootRun Gradle task. The prod 
profile is selected in Dockerfile. Fails fast if no profile is selected.
- API is permissive - on update will accept even fields we don't update
- checkInterval is restricted to >10 seconds to prevent DOSing (although still possible to create many users each with the same endpoint)
- custom messages not implemented 
- [Problems](https://docs.spring.io/spring-hateoas/docs/1.1.0.M3/reference/html/#mediatypes.http-problem) are not 
implemented, errors are returned with content-type JSON, not HAL_FORMS_JSON

## Known issues
### Gazer
- client.get can't be mocked
- actor (ro more likely channels) cause tests to go into infinite loop
- gazer tests can only be run one class at a time

