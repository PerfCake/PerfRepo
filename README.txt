Performance Result Repository


Setting-up development environment:
-----------------------------------

- for those wanting to setup JBoss Developer Studio
- other requirements. java, maven, git, git-flow
- more info on git flow:
      http://nvie.com/posts/a-successful-git-branching-model/
      https://github.com/nvie/gitflow/wiki
      http://danielkummer.github.io/git-flow-cheatsheet/

1. Install postgresql-server (instructions for Fedora 18)
      - sudo yum install postgresql-server
      - sudo service postgresql initdb
      - sudo service postgresql start
      - su -l postgres
      - createuser --no-superuser --no-createdb --no-createrole --pwprompt perfrepo
      - createdb --owner perfrepo perfrepo
      - sudo gedit /var/lib/pgsql/data/pg_hba.conf
        add line:
           host all all 10.34.129.0/24 md5       # use your network of course
      - sudo gedit /var/lib/pgsql/data/postgresql.conf
        add line:
           listen_addresses = '*'
      - sudo yum install pgadmin3
      - check access to the perfrepo database

2. Download and Install JBDS bundled with EAP
      - http://download.devel.redhat.com/released/JBossDS/JBDS-6.0.1/jbdevstudio-product-eap-universal-6.0.1.GA-v20130327-2052-B361.jar
      - in this guide JBOSS_HOME will refer to $JBDS_HOME/runtimes/jboss-eap
      - make sure you have m2e and egit plugin installed

3. Download Posgresql JDBC driver
      - http://jdbc.postgresql.org/
      - place to $JBOSS_HOME/standalone/deployments

4. Create datasource using the postgress JDBC driver
      - add management user with $JBOSS_HOME/bin/add-user.sh
      - go to JBoss EAP mgmt console: http://localhost:9990/console/App.html#datasources
      - Profile > Connector > Datasources > Add
      - JNDI:       java:jboss/datasources/PerfRepoDS 
      - Connection: jdbc:postgresql://10.34.129.134:5432/perfrepo
      
5. Checkout sources:
      - JDBS is workspace referred to as $WORKSPACE
      - cd $WORKSPACE
      - git clone git+ssh://code.engineering.redhat.com/jbossqe-perfrepo.git
      - cd jbossqe-perfrepo
      - git flow init
