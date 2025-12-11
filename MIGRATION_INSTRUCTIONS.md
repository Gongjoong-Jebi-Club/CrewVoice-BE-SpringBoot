# Database Migration Instructions

## Issue
`ClassCastException: Character cannot be cast to String` 에러 발생

## Root Cause
- DB의 `gender` 컬럼이 `CHAR(1)` 타입 (단일 문자)
- JPA `@Enumerated(EnumType.STRING)`은 String 타입을 기대
- Hibernate가 Character를 String으로 변환할 수 없음

## Solution

### 1. Database 컬럼 타입 변경

PostgreSQL에서 실행:

```sql
-- 1. 현재 상태 확인
SELECT column_name, data_type, character_maximum_length
FROM information_schema.columns
WHERE table_name = 'users' AND column_name = 'gender';

SELECT DISTINCT gender, COUNT(*) FROM users GROUP BY gender;

-- 2. 컬럼 타입 변경 (CHAR(1) → VARCHAR(10))
ALTER TABLE users ALTER COLUMN gender TYPE VARCHAR(10);

-- 3. role 컬럼도 동일하게 처리 (필요시)
ALTER TABLE users ALTER COLUMN role TYPE VARCHAR(10);

-- 4. 검증
SELECT user_id, name, email, gender, role FROM users LIMIT 5;
```

### 2. 실행 방법

#### Option 1: psql 사용
```bash
psql -h <host> -U <username> -d <database> -f migration_gender_enum.sql
```

#### Option 2: DBeaver/pgAdmin 등 GUI 도구 사용
1. DB 연결
2. `migration_gender_enum.sql` 파일 열기
3. 스크립트 실행

### 3. 데이터 확인

마이그레이션 후 기존 데이터:
- `'M'` → `Gender.M` (자동 매핑)
- `'F'` → `Gender.F` (자동 매핑)

### 4. 애플리케이션 재시작

```bash
./gradlew bootRun
```

## Rollback (필요시)

```sql
-- 원래 타입으로 되돌리기
ALTER TABLE users ALTER COLUMN gender TYPE CHAR(1);
ALTER TABLE users ALTER COLUMN role TYPE VARCHAR(20);
```

## 검증

애플리케이션 재시작 후 로그인 API 테스트:

```bash
curl -X POST http://localhost:8080/api/v1/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "password123"
  }'
```

정상 동작하면 마이그레이션 성공!
