# --- 1단계: 애플리케이션 빌드 ---
# Gradle과 JDK 17이 포함된 이미지를 빌드 환경으로 사용합니다.
FROM gradle:8.5-jdk17-jammy AS build

# 작업 디렉토리를 설정합니다.
WORKDIR /home/gradle/src

# 먼저 빌드에 필요한 파일들만 복사하여 Docker 캐시를 활용합니다.
# 이 파일들이 변경되지 않으면 의존성 다운로드 단계를 건너뜁니다.
COPY build.gradle settings.gradle ./
COPY gradle ./gradle

# 소스 코드를 복사합니다.
COPY src ./src

# Gradle을 사용하여 애플리케이션을 빌드합니다. (테스트는 생략하여 빌드 속도 향상)
RUN gradle build -x test

# --- 2단계: 최종 이미지 생성 ---
# 실제 애플리케이션을 실행할 가볍고 깨끗한 이미지를 사용합니다.
FROM eclipse-temurin:17-jdk-jammy

# 1단계(build 스테이지)에서 생성된 JAR 파일을 최종 이미지로 복사합니다.
COPY --from=build /home/gradle/src/build/libs/*.jar app.jar

# 애플리케이션을 실행합니다.
ENTRYPOINT ["java","-jar","/app.jar"]