# Use Java 21 runtime
FROM eclipse-temurin:21-jre-jammy

# Create app directory
WORKDIR /app

# Copy the JAR built by Maven into the container
# We use target/*.jar to catch whatever version was built
COPY target/*.jar app.jar

# Run the app
ENTRYPOINT ["java", "-jar", "app.jar"]