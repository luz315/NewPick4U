# 1. 베이스 이미지 설정 (경량 JDK)
FROM eclipse-temurin:17-jdk-alpine

# 2. 작업 디렉토리
WORKDIR /app

# 3. 서비스 JAR 복사
COPY tag/build/libs/tag-0.0.1-SNAPSHOT.jar ./tag-0.0.1-SNAPSHOT.jar

# 4. pinpoint-agent 복사 (common 모듈 기준 상대 경로)
COPY ../common/pinpoint-agent ./pinpoint-agent

# 5. 환경 변수로 JVM 옵션 설정
ENV JAVA_TOOL_OPTIONS="\
-javaagent:/app/pinpoint-agent/pinpoint-bootstrap.jar \
-Dpinpoint.config=/app/pinpoint-agent/pinpoint-root.config \
-Dpinpoint.agentId=tag-service-prod \
-Dpinpoint.applicationName=tag-service"

# 6. 컨테이너 실행 커맨드
ENTRYPOINT ["java", "-jar", "app.jar"]
