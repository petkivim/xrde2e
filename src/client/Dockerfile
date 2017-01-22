# Base java:8
FROM java:8

# Add Backend jar to container
ADD target/client-*.jar client.jar

# Entry with exec
ENTRYPOINT exec java $JAVA_OPTS -jar /client.jar