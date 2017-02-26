# XRdE2E

XRdE2E is an end-to-end monitoring tool for X-Road security servers. Monitoring of the security servers is done using the ```listMethods``` meta service. If security server sends a valid response, it means that ```proxy``` and ```signer``` components are both working fine.

![xrde2e-deployment-single](https://github.com/petkivim/xrde2e/blob/master/images/xrde2e-deployment-single.png)

### Build

```
./build_docker_images.sh
```

### Run

```
docker-compose up -d
```
