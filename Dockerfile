# Base Image 설정 (Java 17 기준)
FROM openjdk:17-jdk-slim

# 작업 디렉토리 설정
WORKDIR /app

# JAR 파일을 컨테이너에 복사
COPY build/libs/*.jar app.jar

# 컨테이너에서 실행될 명령어 설정
CMD ["java", "-jar", "app.jar"]

# 컨테이너가 사용할 포트 지정
EXPOSE 8080
