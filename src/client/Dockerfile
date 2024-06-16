# Base java:11
FROM eclipse-temurin:11-jre

# Add Backend jar to container
ADD target/client-*.jar client.jar

# Entry with exec
ENTRYPOINT exec java $JAVA_OPTS -jar /client.jar