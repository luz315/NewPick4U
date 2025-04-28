# Dockerfile
FROM openjdk:17-jdk-slim
WORKDIR /app
# news-*.jar 자동 감지하여 app.jar로 복사
COPY build/libs/news-*.jar app.jar
# docker 프로파일로 실행
ENTRYPOINT ["java", "-Dspring.profiles.active=main", "-jar", "app.jar"]
