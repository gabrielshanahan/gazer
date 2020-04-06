#!/bin/bash
set -e

mysql --protocol=socket -uroot -p$MYSQL_ROOT_PASSWORD <<EOSQL
CREATE USER IF NOT EXISTS 'gazer'@'%' identified by '${MYSQL_GAZER_PASSWORD}';
GRANT ALL ON ${MYSQL_DATABASE:-gazer}.* to 'gazer'@'%';
EOSQL

