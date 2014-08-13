#!/bin/sh -e

# Edit the following to change the name of the database user that will be created:
SIMBA_DB_USER=simba_local
SIMBA_DB_PASS=simba_local
SIMBA_DB_NAME=simba
SIMBA_MAX_CONNS=99

###########################################################
# Changes below this line are probably not necessary
###########################################################
print_db_usage () {
  echo "Your MySQL databases have been setup and can be accessed on your local machine on the forwarded port (default: 3306)"
  echo ""
  echo " Database: $SIMBA_DB_USER"
  echo " Username: $SIMBA_DB_USER"
  echo " Password: $SIMBA_DB_PASS"
  echo " Max # Conns: $SIMBA_MAX_CONNS"
  echo ""
  echo "jdbc connection string:"
  echo "jdbc:mysql://localhost:3306/$SIMBA_DB_NAME?user=$SIMBA_DB_USER&password=$SIMBA_DB_PASS"
  echo ""
}

export DEBIAN_FRONTEND=noninteractive

PROVISIONED_ON=/etc/vm_provision_on_timestamp
if [ -f "$PROVISIONED_ON" ]
then
  echo "VM was already provisioned at: $(cat $PROVISIONED_ON)"
  echo "To run system updates manually login via 'vagrant ssh' and run 'apt-get update && apt-get upgrade'"
  echo ""
  print_db_usage
fi

# Update package list and upgrade all packages
apt-get update
apt-get -y upgrade

MYSQL_CONF="/etc/mysql/my.cnf"

if [ ! -f "$MYSQL_CONF" ]
then
  apt-get -y install mysql-server
  # Edit my.conf to change listen address to '*':
  sed -i 's/127.0.0.1/0.0.0.0/g' $MYSQL_CONF
  service mysql restart
  mysql -u root -e ";CREATE DATABASE $SIMBA_DB_NAME;GRANT ALL ON $SIMBA_DB_NAME.* TO $SIMBA_DB_USER@localhost IDENTIFIED BY '$SIMBA_DB_PASS' WITH MAX_USER_CONNECTIONS $SIMBA_MAX_CONNS;GRANT ALL ON $SIMBA_DB_NAME.* TO $SIMBA_DB_USER@'%' IDENTIFIED BY '$SIMBA_DB_PASS' WITH MAX_USER_CONNECTIONS $SIMBA_MAX_CONNS"
else
  echo "MySQL already installed, skipping installation"
  service mysql status > mysqlStatus.txt
  if grep -q stop < mysqlStatus.txt; then
      echo "Restarting MYSQL Service"
      service mysql restart
  fi
  mysql -u root -e ";DROP DATABASE $SIMBA_DB_NAME;CREATE DATABASE $SIMBA_DB_NAME;GRANT ALL ON $SIMBA_DB_NAME.* TO $SIMBA_DB_USER@localhost IDENTIFIED BY '$SIMBA_DB_PASS' WITH MAX_USER_CONNECTIONS $SIMBA_MAX_CONNS;GRANT ALL ON $SIMBA_DB_NAME.* TO $SIMBA_DB_USER@'%' IDENTIFIED BY '$SIMBA_DB_PASS' WITH MAX_USER_CONNECTIONS $SIMBA_MAX_CONNS"
fi
mysql -u root -e "SET GLOBAL log_output = 'FILE';"
mysql -u root -e "SET GLOBAL general_log = 'ON';"
mysql -u root -e "SET GLOBAL general_log_file = '/tmp/mysql.log';"


apt-get clean

# Tag the provision time:
date > "$PROVISIONED_ON"

echo "Successfully created MySQL dev virtual machine."
echo ""
print_db_usage
