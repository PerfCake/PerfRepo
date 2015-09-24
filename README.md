# Meet PerfRepo

Performance result repository (PerfRepo) is a web application tool that intends to make it easy to capture and archive performance test results and simplify comparison of performance run results and detect a performance regression from one build to the next in an automated fashion.

Get involved by entering bugs, feature requests etc into our JIRA: https://issues.jboss.org/browse/PERFREPO/

## Features

More detailed Wiki page on PerfRepo features is coming soon.

* track performance result
* sort them by tags, tests etc.
* generate history charts, groupped metric charts
* compare builds 
* ... and much more

# Try it out

Here we describe process how to set up your own PerfRepo instance.

## Prerequisites

List of suggested components to be able to run PerfRepo easily. Possibly other application servers or databases can be used, but this combination is used and tested.

* Java 8 
* Maven 3+ (http://maven.apache.org/)
* JBoss EAP 6.4+ (http://jbossas.jboss.org/downloads)
* PostgreSQL 9+ (http://www.postgresql.org/)

## Configure Maven

Configure Maven to use JBoss Nexus repository. Follow the steps at https://developer.jboss.org/wiki/MavenGettingStarted-Users in section "Configuring Maven to use the JBoss Repository".

## Set up the database

1. Create a database (e.g. named `perfrepo`)
2. Script `db_schema_creation.sql` in `model/src/main/sql` creates all necessary tables and structures

## Set up the application server

Following text assumes PostgreSQL and JBoss EAP's `standalone.xml`. Before configuring the datasource, you have to install JDBC driver for PostgreSQL. You can follow the steps for example here: http://www.startain.com/2014/05/add-postgresql-jdbc-driver-to-jboss.html 

* Add PostgreSQL datasource driver, e.g.
```xml    
    <drivers>
        <driver name="postgresql" module="org.postgresql">
            <xa-datasource-class>org.postgresql.xa.PGXADataSource</xa-datasource-class>
        </driver>        
    </drivers>
```

* Add datasource `PerfRepoDS` pointing to the database, e.g.
```xml
    <datasource jndi-name="java:jboss/datasources/PerfRepoDS" pool-name="PerfRepoDS" enabled="true" use-java-context="true">
    	<connection-url>jdbc:postgresql://localhost:5432/perfrepo</connection-url>
        <driver-class>org.postgresql.Driver</driver-class>
        <driver>postgresql</driver>
        <security>
            <user-name>perfrepo</user-name>
            <password>perfrepo</password>
        </security>        
    </datasource>
    <drivers>...(described above)...</drivers>
```

* Add security domain `perfrepo`, e.g.
```xml
    <security-domain name="perfrepo" cache-type="default">
        <authentication>
            <login-module code="Database" flag="required">
                <module-option name="dsJndiName" value="java:jboss/datasources/PerfRepoDS"/>
                <module-option name="principalsQuery" value="select password from public.user where username = ?"/>
                <module-option name="rolesQuery" value="select g.name, 'Roles'  from public.user u, public.user_group ug, public.group g where u.username = ? and ug.user_id=u.id and ug.group_id=g.id"/>
                <module-option name="hashUserPassword" value="true"/>
                <module-option name="hashAlgorithm" value="MD5"/>
                <module-option name="hashEncoding" value="base64"/>
            </login-module>
        </authentication>
    </security-domain>
```

* Add SMTP server configuration for alerting
```xml
    <subsystem xmlns="urn:jboss:domain:mail:1.2">
        <mail-session jndi-name="java:jboss/mail/perfreposmtp">
            <smtp-server outbound-socket-binding-ref="mail-smtp"/>
        </mail-session>
    </subsystem>
    
    and add this into <socket-binding-group> tag
    
    <outbound-socket-binding name="mail-smtp">
        <remote-destination host="<your SMTP host>" port="25"/>
    </outbound-socket-binding>
```

* Alter the web domain subsystem by setting enable-welcome-root to "false".
```xml
    <subsystem xmlns="urn:jboss:domain:web:2.2" default-virtual-server="default-host" native="false">
        <connector name="http" protocol="HTTP/1.1" scheme="http" socket-binding="http"/>
        <virtual-server name="default-host" enable-welcome-root="false">
            <alias name="localhost"/>
            <alias name="example.com"/>
        </virtual-server>
    </subsystem>
```

* Start server
* Install PerfRepo using Maven, e.g. run `mvn clean install -DskipTests` in the root directory of PerfRepo
* Deploy the WAR, i.e. go to `web` directory and run `mvn clean package jboss-as:deploy -DskipTests`
* PerfRepo should be running on the `/repo` URL. For testing purposes, `db_schema_creation.sql` creates a default user
	* login: `perfrepouser`
	* password: `perfrepouser1.`

* If you need more users, you have to add them manually into database.

# Running tests

PerfRepo has several tests to ensure REST client compatibility. These tests are configured to use exact copy of production database named `perfrepo_test`, so to run them, you have to set up new datasource `PerfRepoTestDS`, new security domain `perfrepo_test` pointing to the testing database.





