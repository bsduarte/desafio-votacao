# Use the official Maven image with Amazon Corretto 21 as the base image
FROM maven:3-amazoncorretto-21 AS builder

ADD . /usr/src/voting
WORKDIR /usr/src/voting

# Build the application using Maven
RUN mvn clean package -DskipTests

# Use the official Amazon Corretto 21 image for the final runtime
FROM amazoncorretto:21-alpine AS runner
COPY --from=builder /usr/src/voting/target/voting-0.0.1-SNAPSHOT.jar /usr/app/voting.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/usr/app/voting.jar"]