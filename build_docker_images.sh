#!/bin/bash

# Build a Docker image for compiling the code
docker build -t xrde2e-builder .

# Add Maven cache mount only if it exists
[ -d "$HOME/.m2" ] && M2VOL="-v $HOME/.m2:/root/.m2" || M2VOL=""
# Compile the code
docker run --rm -it -v ./src:/app $M2VOL xrde2e-builder

# Build Docker images for each module
docker build -t xrde2e-client src/client/
docker build -t xrde2e-backend src/backend/
docker build -t xrde2e-ui ui/

