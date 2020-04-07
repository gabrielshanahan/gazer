#!/bin/bash
set -e

mysql --protocol=socket -uroot -p$MYSQL_ROOT_PASSWORD <<EOSQL
CREATE USER IF NOT EXISTS '${MYSQL_GAZER_USER:-gazer}'@'%' identified by '${MYSQL_GAZER_PASSWORD}';
GRANT ALL ON ${MYSQL_GAZER_DATABASE:-gazer}.* to '${MYSQL_GAZER_USER:-gazer}'@'%';
EOSQL

