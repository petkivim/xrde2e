FROM maven:3.8.6-openjdk-11

WORKDIR /app

CMD ["mvn", "clean", "package"]