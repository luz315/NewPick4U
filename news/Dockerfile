# Dockerfile
FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY build/libs/news-*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]