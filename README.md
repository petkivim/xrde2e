# XRdE2E

XRdE2E is an end-to-end monitoring tool for X-Road security servers. Monitoring of the security servers is done using the ```listMethods``` meta service. If security server sends a valid response, it means that ```proxy``` and ```signer``` components are both working fine.

XRdE2E includes four components:

* UI - A simple web page for accessing and searching the monitoring data.
* Backend - REST API that provides access to the monitoring data collected from security servers. The Swagger description of the API is accessible at ```http://{HOST}/apidocs/v1/``` or here: [JSON](https://github.com/petkivim/xrde2e/blob/master/ui/src/apidocs/v1/swagger.json), [YAML](https://github.com/petkivim/xrde2e/blob/master/ui/src/apidocs/v1/swagger.yaml).
* Database - MongoDB database for storing the monitoring data.
* Client - Monitoring client that collects data from security servers. The client calls the monitored security servers through a client security server as it's not able to call the targets directly.

The client is polling each target according to the interval that's defined in the configuration and stores the results in the database. The backend provides REST API for querying the data from the database. The UI fetches the data using the REST API and shows the results to the user. When the user accesses the UI the monitoring data is fetched from the database which is updated asynchronously by the client. The number of days that the data is stored in the database can be configured in the client's configuration.

The diagram below shows the basic deployment of the system.

![xrde2e-deployment-single](https://github.com/petkivim/xrde2e/blob/master/images/xrde2e-deployment-single.png)

The basic deployment has one major limitation - it's possible to monitor security servers that are all registered in the same X-Road instance. Usually a X-Road member organization has security servers in at least two different instances: test and production. Instead of deploying two different monitoring systems, one for test and another for production, it's better to deploy one monitoring system with multiple clients.

Each client can call only one client security server and each security server can be registered in only one instance at the time. Therefore, we need one client and one client security server per instance. However, different clients can share the same database which makes it possible for the user to access monitoring data collected from different instances through the same UI. The diagram below gives an example of this kind of deployment.

![xrde2e-deployment-multi](https://github.com/petkivim/xrde2e/blob/master/images/xrde2e-deployment-multi.png)

### Build

```
./build_docker_images.sh
```

### Configuration

The default configuration expects that client properties can be found from ```/var/xrde2e-client/xrde2e.properties``` file. MongoDB's data directory is ```/var/mongodb```. It's possible to change these locations modifying [docker-compose.yml](https://github.com/petkivim/xrde2e/blob/master/docker-compose.yml) file.

The contents  of the ```/var/xrde2e-client/xrde2e.properties``` file can be seen below. At least ```proxy``` and ```consumer``` properties must be updated.  ```proxy``` property defines the access point to the client security server that's used for calling the target security servers. ```consumer``` is the subsystem that's user for calling the ```listMethods``` meta service of the targets. In addition, the target security servers must be defined using ```x.subsyste```, ```x.server``` and ```x.label``` properties. **NB!** It is very important to replace the ```x``` prefix with the number of the target. Numbering starts from zero and no numbers must not be skipped. Jumping over a number causes that all the targets defined after the missing number are skipped.

```
# Connection string that describes the host to be used and options.
# When connectionString is null or empty host and port are used.
# E.g. with username and password:
# mongodb://user:password@localhost:27017/xrde2emonitoring?safe=true
db.connectionString=mongodb://localhost:27017/xrde2emonitoring?safe=true
# Security server URL/IP
proxy=http://x.x.x.x/
# Request interval in milliseconds
interval=5000
# Interval between starting a new E2E monitoring thread when the program
# starts. Defined in milliseconds.
threadInterval=300
# Delete entries older than X days from historical status
deleteOlderThan=1
# Delete entries older than X hours from current status
deleteOlderThanFromCurrent=12
# Run removal of old entries every X hours.
# If value is set to 0 (zero), the removal of old entries is skipped.
deleteOlderThanInterval=1
# Consumer identifier
consumer=FI-TEST.GOV.0245437-2.MyTestClient
# List of targets in format: 
# x.subsystem=instanceIdentifier.memberClass.memberCode.subsystemCode
# x.server=instanceIdentifier.memberClass.memberCode.serverCode
# x.label=Human readable name for the target
0.subsystem=FI-TEST.GOV.0245437-2.TestService
0.server=FI-TEST.GOV.0245437-2.myserver01
0.label=My server 1
1.subsystem=FI-TEST.GOV.0245437-2.TestService
1.server=FI-TEST.GOV.0245437-2.myserver02
1.label=My server 2
.
.
50.subsystem=FI-TEST.COM.5545756-1.InfoService
50.server=FI-TEST.COM.5545756-1.server01
50.label=Info server
```

### Run

After updating the configuration it's time to start the system using Docker Compose. The command below starts the containers in the background and leaves them running.

```
docker-compose up -d
```

Follow log output. The output of each container is aggregated to a single log stream.

```
docker-compose logs -f
```

Stop the containers.

```
docker-compose stop
```

Stop and remove the containers.

```
docker-compose down
```

Overview of Docker Compose CLI is available [here](https://docs.docker.com/compose/reference/overview/).

In case it is not possible to access a Security Server, XRdE2E can be tested using [list-methods-dummy-service](list-methods-dummy-service/README.md) that provides a mock up of Security Server's `listMethods` meta service.

### Using Multiple Clients

Using multipe clients requires some changes to [docker-compose.yml](https://github.com/petkivim/xrde2e/blob/master/docker-compose.yml) file. Below there's the client configuration for a single client.

```
xrde2e-client:
    image: xrde2e-client:latest
    depends_on:
      - db
    volumes:
      - /var/xrde2e-client:/my/conf:Z
    environment:
- JAVA_OPTS=-DpropertiesDirectory=/my/conf/
```

Below there's an example configuration for two clients: test and prod. In addition, both clients need their own ```xrde2e.properties``` file that are located in ```/var/xrde2e-client-test``` and ```/var/xrde2e-client-prod```.

```
xrde2e-client-test:
    image: xrde2e-client:latest
    depends_on:
      - db
    volumes:
      - /var/xrde2e-client-test:/my/conf:Z
    environment:
- JAVA_OPTS=-DpropertiesDirectory=/my/conf/
xrde2e-client-prod:
    image: xrde2e-client:latest
    depends_on:
      - db
    volumes:
      - /var/xrde2e-client-prod:/my/conf:Z
    environment:
- JAVA_OPTS=-DpropertiesDirectory=/my/conf/
```

After the configuration changes just restart the system.

### Using Mutual SSL Authentication

Especially in a production environment it's a good idea to use mutual SSL authentication between the client and the client security server. This requires generating self-signed certificate for the client and importing it to the security server. Likewise, the security server's certificate must be imported to the client's trust store. In addition, the location of the keystore and truststore files must be defined in the ```docker-compose.yml``` file.

```
  xrde2e-client:
    image: xrde2e-client:latest
    depends_on:
      - db
    volumes:
      - /var/xrde2e-client:/my/conf:Z
    environment:
      - JAVA_OPTS=-DpropertiesDirectory=/my/conf/ -Djavax.net.ssl.trustStore=/my/conf/xrde2eTrustStore.jks -Djavax.net.ssl.trustStorePassword=changeit -Djavax.net.ssl.keyStore=/my/conf/xrde2eKeyStore.jks -Djavax.net.ssl.keyStorePassword=changeit
 ```

Import the internal certificate of the security server to the truststore of the client.

```
keytool -import -file cert.crt -alias secserver -keystore xrde2eTrustStore.jks
```

Create a new keystore file and a keypair for the client.

```
keytool -genkey -keyalg RSA -alias client -keystore xrde2eKeyStore.jks -storepass changeit -validity 720 -keysize 2048
```

Export the certificate from the keystore and add it to the security server under the same subsystem defined by the ```consumer``` property.

```
keytool -export -alias selfsigned -keystore xrde2eKeyStore.jks -rfc -file xrde2e_X509_certificate.cer
```

### MongoDB And Authentication

by default MongoDB does not require authentication as it is started with ```--noauth``` option.

When the system is up and running jump into to the MongoDB container.
```
docker exec -i -t xrde2e_db_1 bash
```

First, create admin user. Replace ```{ADMIN_PWD}``` with your own password.

```
mongo admin --eval "db.createUser({ user: 'admin', pwd: '{ADMIN_PWD}', roles: [ { role: 'userAdminAnyDatabase', db: 'admin' } ] });"
```

Then, create a new user for the client(s). Replace ```{ADMIN_PWD}``` with the admin password and ```{XRDE2E_CLIENT_PWD}``` with the password that you want to set for the client(s).

```
mongo -u "admin" -p "{ADMIN_PWD}" --authenticationDatabase "admin" xrde2emonitoring --eval "db.createUser({ user: 'xrde2e-client', pwd: '{XRDE2E_CLIENT_PWD}', roles: [ { role: 'readWrite', db: 'xrde2emonitoring' } ] });"
```

At last, create a new user for the backend. Replace ```{ADMIN_PWD}``` with the admin password and ```{XRDE2E_BACKEND_PWD}``` with the password that you want to set for the backend.

```
mongo -u "admin" -p "{ADMIN_PWD}" --authenticationDatabase "admin" xrde2emonitoring --eval "db.createUser({ user: 'xrde2e-backend', pwd: '{XRDE2E_BACKEND_PWD}', roles: [ { role: 'read', db: 'xrde2emonitoring' } ] });"
```

Backend password must be updated to ```docker-compose.yml``` file. In addition, MongoDB's authentication must be switched on.

```
  xrde2e-backend:
    image: xrde2e-backend:latest
    depends_on:
      - db
    ports:
      - "8080:8080"
    environment:
      - JAVA_OPTS=-Dspring.data.mongodb.host=db -Dspring.data.mongodb.username=xrde2e-backend -Dspring.data.mongodb.password={XRDE2E_BACKEND_PWD} -Dspring.data.mongodb.database=xrde2emonitoring
  db:
    image: mongo:3.4.0
    command: mongod --dbpath=/data/db --auth
    ports:
      - "27017:27017"
    volumes:
      - /var/mongodb:/data/db:Z
```

Also client's ```db.connectionString``` property in the ```xrde2e.properties``` file must be updated.

```
db.connectionString=mongodb://xrde2e-client:{XRDE2E_CLIENT_PWD}@db:27017/xrde2emonitoring?safe=true
```

Restart the system for configuration changes to take effect.
