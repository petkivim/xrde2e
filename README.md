# XRdE2E

XRdE2E is an end-to-end monitoring tool for X-Road security servers. Monitoring of the security servers is done using the ```listMethods``` meta service. If security server sends a valid response, it means that ```proxy``` and ```signer``` components are both working fine.

XRdE2E includes four components:

* UI - A simple web page for accessing and searching the monitoring data.
* Backend - REST API that provides access to the monitoring data collected from security servers.
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

### Run

```
docker-compose up -d
```
