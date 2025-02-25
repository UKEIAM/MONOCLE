# Use a Maven image to build the application
FROM maven:3.8.4-openjdk-17-slim AS build

# Set the working directory in the container
WORKDIR /app

# Copy the Maven configuration and POM files
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN sed -i -e 's/\r$//' mvnw
# Ensure Maven wrapper is executable
RUN chmod +x mvnw

COPY src ./src

# Build the application with Maven (assuming Maven build structure)
RUN mvn package -DskipTests

# Use a base image for the application
FROM ubuntu:18.04

# These commands were copied from the Dockerfile of DatenHotel
RUN apt-get -y update && apt-get -y upgrade
RUN apt-get -y install openjdk-17-jdk wget
RUN apt-get -y install tzdata
RUN ln -fs /usr/share/zoneinfo/Europe/Berlin /etc/localtime
ENV DEBIAN_FRONTEND=noninteractive
RUN dpkg-reconfigure --frontend noninteractive tzdata
ENV JAVA_OPTS "-Djava.security.egd=/dev/./urandom -Djava.awt.headless=true -Xmx1024m -XX:MaxPermSize=512m -XX:+UseConcMarkSweepGC"

# create the needed directories for FileWatcher
RUN mkdir -p /mtb-data/main
RUN mkdir -p /mtb-data/unzip
RUN mkdir -p /mtb-data/archive
RUN mkdir -p /mtb-data/reports

# Set the working directory in the container
WORKDIR /app

## Copy the packaged jar file from the build stage to the container
COPY --from=build /app/target/*.jar mtb-control.jar
#COPY --from=build /app/target/* .
# Expose the port on which your Spring Boot application will run
EXPOSE 8080

# Define the command to run your Spring Boot application
CMD ["java", "-jar", "mtb-control.jar"]
