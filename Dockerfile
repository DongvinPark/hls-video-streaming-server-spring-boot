FROM openjdk:17-jdk-alpine AS builder
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
COPY src src
RUN chmod +x gradlew
RUN ./gradlew bootJAR


FROM openjdk:17-oracle
COPY --from=builder build/libs/*.jar app.jar

# 루트 디렉토리 바로 아래에 존재하는 hls-videos 디렉토리 내부의 모든 파일을 도커 컨테이너 내부의
# 루트 디렉토리 바로 아래에 hls-videos 라는 디렉토리를 만들어서 전부 복사하라는 명령어
COPY hls-videos ./hls-videos

ENV TZ=Asia/Seoul
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]