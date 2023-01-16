FROM maven:3.8.7-openjdk-18-slim AS build
RUN mkdir /project
COPY . /project
WORKDIR /project
RUN mvn clean package -DskipTests

FROM amazoncorretto:17.0.5
RUN mkdir /app
COPY --from=build /project/target/camel-prometheus-0.0.1-SNAPSHOT.jar /app/java-application.jar
WORKDIR /app
CMD "java" "-jar" "java-application.jar"