# Use OpenJDK as the base image
FROM eclipse-temurin:21-jdk-alpine

# Set working directory
WORKDIR /app

# Copy the built JAR file
COPY build/libs/*.jar app.jar

# Expose the application port
EXPOSE 8080

# Run the JAR file
ENTRYPOINT ["java", "-jar", "app.jar"]
