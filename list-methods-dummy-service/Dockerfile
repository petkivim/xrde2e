FROM node:8

# Create app directory
WORKDIR /usr/src/app

# Install app dependencies
COPY src/package*.json ./
RUN npm install

# Bundle app source
COPY src/index.js ./

EXPOSE 8081
CMD [ "npm", "start" ]
