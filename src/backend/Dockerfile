# Base java:8
FROM java:8

# Add Backend jar to container
ADD target/backend-*.jar backend.jar

# Entry with exec
ENTRYPOINT exec java $JAVA_OPTS -jar /backend.jar

# Expose port 8080
EXPOSE 8080