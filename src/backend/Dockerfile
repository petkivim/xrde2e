# Base java:11
FROM eclipse-temurin:21-jre

# Add Backend jar to container
ADD target/backend-*.jar backend.jar

# Entry with exec
ENTRYPOINT exec java $JAVA_OPTS -jar /backend.jar

# Expose port 8080
EXPOSE 8080