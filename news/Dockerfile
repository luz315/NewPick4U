# Dockerfile
FROM openjdk:17-jdk-slim
WORKDIR /app
# news-*.jar 자동 감지하여 app.jar로 복사
COPY build/libs/news-*.jar app.jar
# docker 프로파일로 실행
ENTRYPOINT ["sh", "-c", "java -Dspring.profiles.active=${SPRING_PROFILES_ACTIVE} -jar app.jar"]
