# 1. 베이스 이미지 설정 (Java 17 슬림 버전)
FROM openjdk:17-jdk-slim

# 2. 빌드된 JAR 파일이 위치할 경로를 변수로 지정
ARG JAR_FILE=build/libs/*.jar

# 3. 빌드된 JAR 파일을 app.jar 라는 이름으로 컨테이너에 복사
COPY ${JAR_FILE} app.jar

# 4. 애플리케이션 실행
ENTRYPOINT ["java","-jar","/app.jar"]