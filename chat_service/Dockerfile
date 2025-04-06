FROM gradle:8.3.1-jdk17 AS build
WORKDIR /home/gradle/project
COPY --chown=gradle:gradle . .
RUN gradle clean bootJar --no-daemon

FROM openjdk:17-jdk-slim
VOLUME /tmp
COPY --from=build /home/gradle/project/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
