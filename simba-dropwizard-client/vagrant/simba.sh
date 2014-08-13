#!/bin/sh -e

# This script needs to be performed as root

# Upgrade to Java 7 since our binaries are compiled vs Java 7
apt-get upgrade
apt-get update

apt-get install -y openjdk-7-jdk
update-java-alternatives -s java-1.7.0-openjdk-amd64 --jre

# Tomcat
apt-get install tomcat7 -y

sed -i 's/8080/8087/g' /etc/tomcat7/server.xml
# pass your custom simba application's specific properties along with Tomcat's startup parameters
sed -i 's/JAVA_OPTS=\"-Djava.awt.headless=true -Xmx128m -XX:+UseConcMarkSweepGC\"/JAVA_OPTS=\"-Djava.awt.headless=true -Xmx512m -XX:+UseConcMarkSweepGC -Dsimba.properties.file=\/simba\/src\/main\/resources\/simba.properties\"/g' /etc/default/tomcat7

cp /conf/simba-tomcat7-context.xml /etc/tomcat7/Catalina/localhost/simba.xml

rm -Rf /opt/tomcat
mkdir /opt/tomcat /opt/tomcat/webapps
# Here you want to probably download your custom simba.war from wherever you install your successfully built artifacts. An example when you install to a Nexus Repository:
# wget -O /opt/tomcat/webapps/simba.war http://nexus.cegeka.be/nexus/service/local/artifact/maven/redirect?r=myapp-snapshots\&g=org.myapp\&a=myapp-simba\&v=0.0.1-SNAPSHOT\&p=war --user=myappsNexusUser --password=myappsNexusPassword

service tomcat7 restart

# Logs can be found in /var/log/tomcat7/catalina.out after a vagrant ssh