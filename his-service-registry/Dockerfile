# Use a standard Maven image to build the application.
# This stage is named 'build' so we can reference it later.
FROM maven:3.8.5-openjdk-17 AS build

# Set the working directory inside the container.
WORKDIR /app

# Copy the POM file first to leverage Docker's layer caching.
# This ensures dependencies are only re-downloaded if the POM changes.
COPY pom.xml .

# Copy the rest of the source code.
COPY src ./src

# Build the application, skipping tests for a faster build.
# The output is a single executable JAR.
RUN mvn clean install -DskipTests

# ---

# The second stage uses a lean OpenJDK image for the runtime.
# This results in a much smaller final image.
FROM openjdk:17-jdk-slim

# Set the working directory.
WORKDIR /app

# Copy the executable JAR from the 'build' stage.
COPY --from=build /app/target/*.jar app.jar

# Expose the port the application will run on.
EXPOSE 8761

# The command to run the application.
ENTRYPOINT ["java", "-jar", "app.jar"]
