# syntax=docker/dockerfile:1

# Use the alpine base image with Java JDK
FROM openjdk:17-jdk-alpine

# Create user to run app (instead of root running it)
RUN addgroup -S app && adduser -S app -G app

# Use the user "app"
USER app

# Copy the jar file into the docker image
COPY target/*.jar app.jar

# RUnn the Jar file
ENTRYPOINT ["java", "-jar","/app.jar"]


# Use the Eclipse Temurin base image with Java JDK
#FROM eclipse-temurin:17-jdk-jammy

## Set the working directory to /app
#WORKDIR /app

## Copy .mvn/ and pom.xml to the image
#COPY .mvn/ .mvn
#COPY mvnw pom.xml ./

## Install project dependencies using Maven
#RUN ./mvnw dependency:resolve

## Copy the source code into the image
#COPY src ./src

## Copy the H2_DB folder into the image
#COPY H2_DB ./H2_DB

## Specify the command to run your Spring Boot application
#CMD ["./mvnw", "spring-boot:run"]

