# "listMethods" Dummy Service

This is a dummy service that provides a mock up version of X-Road Security Server's
 `listMethods` meta service. XRdE2E uses `listMethods` for checking Security
 Server's health. This service can be used for testing XRdE2E in case access
 to a Security Server is not available.

 This dummy service supports defining any number of targets in
 the ```/var/xrde2e-client/xrde2e.properties``` configuration file and all
 `instanceId`, `memberClass`, `memberCode` and `serverCode` values are accepted.
 The service does not validate the contents of an incoming message in any way.

 ### Build

 ```
 ./build_docker_image.sh
 ```

## Run

Run `listMethods` dummy service Docker image alone:

```
$ docker run -p 8081:8081 -d list-methods-dummy-service
```

Run the whole XRdE2E stack using `listMethods` dummy service instead of a
Security Server. More [information](../README.md#configuration) about required
configuration.

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
