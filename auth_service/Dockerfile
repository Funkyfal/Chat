FROM openjdk:17-jdk-slim

WORKDIR /app

COPY build/libs/auth_service-0.0.1-SNAPSHOT.jar app.jar

ENV SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/[db_name]
ENV SPRING_DATASOURCE_USERNAME=[username]
ENV SPRING_DATASOURCE_PASSWORD=[password]

ENTRYPOINT ["java", "-jar", "app.jar"]