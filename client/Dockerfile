FROM openjdk:17-jdk-slim

ENV APP_NAME=client-0.0.1-SNAPSHOT.jar

COPY build/libs/${APP_NAME} app.jar

WORKDIR /app

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]