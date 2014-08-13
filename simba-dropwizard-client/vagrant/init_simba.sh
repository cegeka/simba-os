#!/bin/sh -e

mysql -D simba --user=simba_local --password=simba_local < /db/mysql/000_create_db.sql
mysql -D simba --user=simba_local --password=simba_local < /db/mysql/001_insert_parameters.sql
mysql -D simba --user=simba_local --password=simba_local < /db/mysql/002_init.sql
