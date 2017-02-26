# XRdE2E

XRdE2E is an end-to-end monitoring tool for X-Road security servers. Monitoring of the security servers is done using the ```listMethods``` meta service. If security server sends a valid response, it means that ```proxy``` and ```signer``` components are both working fine.

XRdE2E includes four components:

* UI - A simple web page for accessing and searching the monitoring data.
* Backend - REST API that provides access to the monitoring data collected from security servers.
* Database - MongoDB database for storing the monitoring data.
* Client - Monitoring client that collects data from security servers. The client calls the monitored security servers through a client security server as it's not able to call the targets directly.

The client is polling each target according to the interval that's defined in the configuration and stores the results in the database. The backend provides REST API for querying the data from the database. The UI fetches the data using the REST API and shows the results to the user. When the user accesses the UI the monitoring data is fetched from the database which is updated asynchronously by the client. The number of days that the data is stored in the database can be configured in the client's configuration.

The diagram below shows how the basic deployment of the system.

![xrde2e-deployment-single](https://github.com/petkivim/xrde2e/blob/master/images/xrde2e-deployment-single.png)

### Build

```
./build_docker_images.sh
```

### Run

```
docker-compose up -d
```
