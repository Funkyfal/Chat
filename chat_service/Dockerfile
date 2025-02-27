FROM openjdk:17-jdk-alpine

WORKDIR /app

COPY build/libs/chat_service-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]