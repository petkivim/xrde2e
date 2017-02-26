#!/bin/bash

# Build all the artifacts
cd src/
mvn clean install
cd ..

# Build Docker images for each module
docker build -t xrde2e-client src/client/
docker build -t xrde2e-backend src/backend/
docker build -t xrde2e-ui ui/

