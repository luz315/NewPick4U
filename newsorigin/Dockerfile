# 1. Amazon Corretto 17 (경량 Alpine 기반)
FROM amazoncorretto:17-alpine

# 2. 애플리케이션 디렉토리 설정
WORKDIR /app

# 3. 빌드된 JAR 파일 복사 (정확한 경로 및 파일명은 프로젝트에 맞게 수정)
COPY build/libs/newsorigin-0.0.1-SNAPSHOT.jar app.jar

# 4. 실행 시 .env.comment 파일이 SpringBoot에 의해 import 되도록 설정
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]