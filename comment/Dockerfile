FROM openjdk:17-jdk-slim

# 2. 애플리케이션 디렉토리 설정
WORKDIR /app

# - 로그 파일 저장을 위한 디렉토리 생성
RUN mkdir -p /app/comment/logs

# 3. 빌드된 JAR 파일 복사 (정확한 경로 및 파일명은 프로젝트에 맞게 수정)
COPY build/libs/comment-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]