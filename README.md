# CrewVoice-Backend

### 커밋 네이밍 규칙

[ 깃모지 | #이슈넘버 ] 간단한 설명

ex: [ ✨ | #5 ] 메인페이지구현

자세한 설명은 이슈에서 작성하면 됩니다.
**커밋내용적는데 너무 신경쓰지 말고 다른데 시간투자하세요.**

| 커밋유형 | 깃모지 | 의미                                                |
| -------- | :----: | --------------------------------------------------- |
| Feat     |   ✨   | 새로운 기능 추가                                    |
| Fix      |   ‼️   | 버그 수정                                           |
| Docs     |   📝   | 문서수정                                            |
| Chore    |   🐛   | 코드 포맷팅, 세미콜론 누락, 자잘한 수정             |
| Refactor |   ♻️   | 코드 리팩토링                                       |
| Comment  |   💡   | 주석 추가 및 변경                                   |
| Rename   |   🚚   | 폴더 파일명 수정 혹은 옮기는 작업                   |
| Remove   |   🔥   | 파일을 삭제하는 작업                                |
| HotFix   |   🚑   | 급하게 치명적인 버그를 수정하느 경우(main작업 허용) |

---

# 실행 및 테스트 가이드 (Run & Test Guide)

이 문서는 CrewVoice Spring Boot 백엔드 애플리케이션을 로컬 환경에서 설정, 실행 및 테스트하는 방법을 설명합니다.

## 1. 사전 요구사항 (Prerequisites)

이 프로젝트를 실행하기 위해 다음 도구들이 설치되어 있어야 합니다:

*   **Java Development Kit (JDK) 17** 이상
*   **PostgreSQL** (데이터베이스)
*   **Redis** (토큰 관리 및 캐싱)
*   **Git**

## 2. 환경 설정 (Environment Setup)

프로젝트 루트 디렉토리에 `.env` 파일을 생성하고 다음 환경 변수들을 설정해야 합니다. `application.yml`에서 이 변수들을 참조합니다.

### `.env` 파일 예시

```properties
# Database (PostgreSQL)
DB_HOST=localhost
DB_NAME=crewvoice_db
DB_USERNAME=your_db_username
DB_PASSWORD=your_db_password

# Redis
REDIS_HOST=localhost
REDIS_PORT=6379

# AWS S3 (파일 업로드/다운로드)
AWS_ACCESS_KEY_ID=your_aws_access_key
AWS_SECRET_ACCESS_KEY=your_aws_secret_key
S3_BUCKET_NAME=your_s3_bucket_name

# JWT Security
# Base64로 인코딩된 시크릿 키 (임의의 문자열을 Base64 인코딩해서 사용)
BASE64_SECRET_KEY=your_base64_encoded_secret_key_at_least_32_bytes_long

# FastAPI 연동 (AI 평가 엔진)
# FastAPI 서버 주소
FASTAPI_BASE_URL=http://localhost:8000
# FastAPI가 평가 완료 후 결과를 보낼 콜백 URL (현재 서버 주소)
FASTAPI_CALLBACK_URL=http://localhost:8080/api/v1/evaluations/callback
# 콜백 요청 인증을 위한 API 키 (FastAPI와 공유)
CALLBACK_API_KEY=your_shared_callback_api_key
```

> **참고:** 로컬 개발 환경에서는 AWS 자격 증명이나 Redis가 없을 경우 실행에 실패할 수 있습니다. Docker를 사용하여 로컬에 PostgreSQL과 Redis를 띄우는 것을 권장합니다.

## 3. 빌드 및 실행 (Build & Run)

Gradle Wrapper를 사용하여 애플리케이션을 빌드하고 실행할 수 있습니다.

### 실행 (Run)

터미널에서 프로젝트 루트 경로로 이동한 후 다음 명령어를 실행하세요.

**Mac/Linux:**
```bash
./gradlew bootRun
```

**Windows:**
```cmd
gradlew.bat bootRun
```

실행이 성공하면 콘솔에 Spring Boot 로그가 출력되며, 기본적으로 `http://localhost:8080` 포트에서 서버가 시작됩니다.

### 빌드 (Build)

실행 가능한 JAR 파일을 생성하려면 다음 명령어를 사용합니다.

**Mac/Linux:**
```bash
./gradlew build -x test
```
(테스트를 건너뛰고 빌드하려면 `-x test` 옵션을 사용하세요. 환경 설정이 완벽하지 않은 경우 테스트가 실패할 수 있습니다.)

**Windows:**
```cmd
gradlew.bat build -x test
```

생성된 JAR 파일은 `build/libs/` 디렉토리에 위치합니다.

## 4. 테스트 (Testing)

단위 테스트 및 통합 테스트를 실행합니다. 테스트 실행 전 `.env` 파일이 올바르게 설정되어 있거나, 테스트 환경을 위한 별도의 설정이 필요할 수 있습니다.

**Mac/Linux:**
```bash
./gradlew test
```

**Windows:**
```cmd
gradlew.bat test
```

## 5. API 문서 (API Documentation)

서버가 실행 중일 때, Swagger UI를 통해 API 명세서를 확인하고 직접 요청을 테스트해볼 수 있습니다.

*   **Swagger UI:** [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
*   **OpenAPI Docs (JSON):** [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

> **팁:** Swagger UI에서 우측 상단의 `Authorize` 버튼을 클릭하고 `Bearer {Access_Token}` 형태로 토큰을 입력하면 인증이 필요한 API도 테스트할 수 있습니다. (로그인 API를 통해 토큰 발급 필요)

## 6. 문제 해결 (Troubleshooting)

*   **DB 연결 오류:** PostgreSQL 서버가 실행 중인지, `.env`의 DB 접속 정보가 정확한지 확인하세요.
*   **Redis 연결 오류:** Redis 서버가 실행 중인지 확인하세요. 로컬에 Redis가 없다면 Docker로 실행(`docker run -p 6379:6379 redis`)하세요.
*   **AWS 오류:** S3 관련 기능 사용 시 AWS 자격 증명이 유효한지 확인하세요.
*   **FastAPI 연결 오류:** 평가 요청 시 FastAPI 서버가 켜져 있지 않으면 오류가 발생할 수 있습니다.

---
**CrewVoice Team**