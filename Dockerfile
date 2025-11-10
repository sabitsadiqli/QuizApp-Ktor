# 1. Use official Gradle image to build the project
FROM gradle:8.7.0-jdk17 AS build
WORKDIR /app
COPY . .
RUN gradle buildFatJar --no-daemon

# 2. Use lightweight JDK image for runtime
FROM eclipse-temurin:17-jdk
WORKDIR /app

# Copy the fat jar built by Gradle
COPY --from=build /app/build/libs/*-all.jar app.jar

# 3. Expose port 8080 (Ktor default)
EXPOSE 8080

# 4. Run the app
ENTRYPOINT ["java", "-jar", "app.jar"]
