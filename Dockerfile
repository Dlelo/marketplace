# ─── build stage ────────────────────────────────────────────────────
# Compile the Spring Boot fat-JAR inside Docker so the image is reproducible
# and the host doesn't need a JDK / Gradle installed.
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /workspace

# Cache the Gradle distribution + dependencies separately from the source.
COPY gradlew settings.gradle build.gradle ./
COPY gradle ./gradle
RUN chmod +x ./gradlew && ./gradlew --no-daemon dependencies > /dev/null 2>&1 || true

# Copy source and build the boot JAR (skip tests — run them in CI).
COPY src ./src
RUN ./gradlew --no-daemon -x test bootJar

# ─── runtime stage ──────────────────────────────────────────────────
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Use a non-root user for the runtime.
RUN addgroup -S app && adduser -S app -G app

# Copy only the boot JAR (skip the -plain.jar artifact).
COPY --from=build /workspace/build/libs/*-SNAPSHOT.jar app.jar
RUN chown app:app app.jar
USER app

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
