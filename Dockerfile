# Dockerfile for Sportrait app (for postgres docker testdb container see separate folder)
FROM tomcat:9
 
MAINTAINER Alexander Josef <alexander.josef@sportrait.com>


# which user to use? check if there's a 'tomcat' user - do not use root
USER root


# copy tomcat server.xml configuration
ADD ./infrastructure/server.xml /usr/local/tomcat/conf/server.xml

# create tomcat ROOT directory for sportrait (according to server.xml)
RUN mkdir -p /usr/local/tomcat/sportrait

# copy available war file (created from mvn / build script ) from maven target folder  to sportrait/ROOT
ADD ./target/*.war /usr/local/tomcat/sportrait/ROOT.war

# postgresql JDBC driver to TOMCAT_HOME/lib
ADD ./infrastructure/postgresql-42.2.2.jar /usr/local/tomcat/lib/postgresql-42.2.2.jar

# Add AWS credentials (??)
ADD ./infrastructure/.aws /root/.aws


# expose port 8080 to host (needed? probably not. see also docker-compose.yml file)
# EXPOSE 8080





