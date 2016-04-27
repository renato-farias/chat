hello-chat-engine
==============

Preparing the environment:

Create your VM using vagrant
```bash
mkdir "~/.m2"
vagrant up
```

After created, access your VM
```bash
vagrant ssh
```

Accessing vagrant folder, install all environment dependencies:
```bash
cd /vagrant
sh setup.sh
```

After install, install all maven/project dependencies
```bash
cd /vagrant
mvn install
```

Testing the project (hello-cometd.jar)
```bash
cd /vagrant/target
java -jar hello-cometd-1.0.jar --config=../config/config.properties --log4j=../config/log4j.properties --webxml=../web.xml
```

Building a new jar
```bash
cd /vagrant
mvn
```

Bluid project for Eclipse
```bash
cd /vagrant
mvn eclipse:eclipse
```

Also you need to set de env var M2_REPO to "~/.m2/repository"


