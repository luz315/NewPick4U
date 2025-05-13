FROM openjdk:17-jdk-slim

# 2. 애플리케이션 디렉토리 설정
WORKDIR /app

# 3. 빌드된 JAR 파일 복사 (정확한 경로 및 파일명은 프로젝트에 맞게 수정)
COPY build/libs/tag-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["sh", "-c", "java -jar app.jar"]