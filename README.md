# Gazer

## Setup
```bash
export MYSQL_ROOT_PASSWORD=<...>
export MYSQL_GAZER_PASSWORD=<...>
docker compose up
```

## Optional config
MYSQL_GAZER_USER (default=gazer)
MYSQL_GAZER_DATABASE (default=gazer)

## About
- Uses layered jars to achieve faster builds
- Uses docker-entrypoint-initdb.d from mysql docker image to setup gazer user for mysql, see docker-compose.yml
