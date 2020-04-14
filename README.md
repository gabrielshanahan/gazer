# Gazer

## Setup - user
```bash
export MYSQL_ROOT_PASSWORD=<...>
export MYSQL_GAZER_PASSWORD=<...>
gaze
```

## Setup - dev
```bash
export MYSQL_ROOT_PASSWORD=<...>
export MYSQL_GAZER_PASSWORD=<...>
gaze --build
```

## Optional config
MYSQL_GAZER_USER (default=gazer)
MYSQL_GAZER_DATABASE (default=gazer)

## About
- Uses layered jars to achieve faster builds
- Uses docker-entrypoint-initdb.d from mysql docker image to setup gazer user for mysql, see docker-compose.yml
- ./gaze checks for necessary environment variables and runs docker-compose up
- docker-compose down deletes data, stop does not
- If timeout for database startup is not enough, edit it in wfi_args in docker-compose.yml
- has dev and prod profile - dev uses H2, and is selected automatically when running bootRun Gradle task. The prod 
profile is selected in Dockerfile. Fails fast if no profile is selected.
- API data format is tightly coupled to DB schema
