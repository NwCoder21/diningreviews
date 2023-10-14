
# Use the Eclipse Temurin base image with Java JDK
FROM openjdk:17-jdk-alpine
#ARG JAR_FILE=target/*.jar
COPY ./target/diningreviews-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar", "/app.jar"]

