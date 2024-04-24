FROM openjdk:17-jdk
LABEL maintainer="TrinityForce"
ARG JAR_FILE=build/libs/sse-server-0.0.1-SNAPSHOT.jar
ADD ${JAR_FILE} sse-server.jar
ENTRYPOINT ["java", "-jar", "sse-server.jar"]