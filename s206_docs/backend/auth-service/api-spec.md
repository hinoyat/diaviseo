# Auth Service API 명세

**Auth Service는 토큰 발급, 재발급, 무효화를 담당하는 인증 전용 서비스입니다.**

---

## 📌 API 개요

### Base URL
```
http://auth-service/api/auth
```

### Auth Service의 역할
- **토큰 발급**: OAuth 인증 후 JWT Access/Refresh Token 생성
- **토큰 재발급**: Refresh Token으로 새 토큰 발급
- **토큰 무효화**: 로그아웃 시 블랙리스트 등록 및 Refresh Token 삭제
- **Redis 토큰 관리**: Refresh Token 저장/삭제, 블랙리스트 등록

### 공통 응답 형식
모든 API는 Common Module의 `ResponseDto` 형식을 사용합니다.

```json
{
  "timestamp": "2025-05-25T10:30:00",
  "status": "OK", 
  "message": "성공 메시지",
  "data": { /* 실제 데이터 */ }
}
```

### 공통 HTTP 상태코드
| 상태코드 | 설명 | 응답 예시 |
|---------|------|----------|
| **200** | 성공 | `"message": "요청 성공"` |
| **400** | 잘못된 요청 | `"message": "필수 파라미터가 누락되었습니다."` |
| **401** | 인증 실패 | `"message": "유효하지 않은 토큰입니다."` |
| **500** | 서버 오류 | `"message": "서버 오류가 발생했습니다."` |

---

## 🔐 토큰 발급 API

### 1. OAuth 로그인

Google ID Token을 검증하여 JWT Access/Refresh Token을 발급합니다.

#### `POST /api/auth/oauth/login`

**Request Headers**
```http
Content-Type: application/json
```

**Request Body**
```json
{
  "provider": "google",
  "idToken": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

| 필드 | 타입 | 필수 | 설명 |
|-----|------|------|------|
| `provider` | String | ✅ | OAuth 제공자 ("google") |
| `idToken` | String | ✅ | 앱에서 받은 Google ID Token |

**처리 과정**
1. **Google API 토큰 검증**: ID Token을 Google OAuth API로 검증
2. **사용자 정보 추출**: 이메일, 이름 정보 획득
3. **User Service 조회**: 기존 회원 여부 확인
4. **토큰 발급**: 기존 회원인 경우 JWT 발급
5. **Redis 저장**: Refresh Token을 Redis에 저장

**Response (기존 회원)**
```json
{
  "timestamp": "2025-05-25T10:30:00",
  "status": "OK",
  "message": "소셜 로그인 성공",
  "data": {
    "isNewUser": false,
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  }
}
```

**Response (신규 회원)**
```json
{
  "timestamp": "2025-05-25T10:30:00",
  "status": "OK",
  "message": "소셜 로그인 성공",
  "data": {
    "isNewUser": true,
    "accessToken": null,
    "refreshToken": null
  }
}
```

**에러 응답**
| 상태코드 | 에러 케이스 | 응답 메시지 |
|---------|-----------|-----------|
| **400** | 지원하지 않는 provider | `"지원하지 않는 소셜 로그인 방식입니다: {provider}"` |
| **401** | Google 토큰 검증 실패 | `"구글 토큰 검증 실패"` |
| **500** | User Service 통신 오류 | `"UserService 응답이 올바르지 않습니다."` |

---

### 2. 테스트 로그인 (개발용)

개발/테스트 환경에서 OAuth 없이 이메일만으로 토큰을 발급합니다.

#### `POST /api/auth/test/login`

**Request Headers**
```http
Content-Type: application/json
```

**Request Body**
```json
{
  "email": "test@example.com",
  "provider": "google"
}
```

**처리 과정**
1. **User Service 조회**: 해당 이메일의 회원 존재 여부 확인
2. **토큰 발급**: 기존 회원인 경우 JWT 발급 (OAuth 검증 생략)
3. **Redis 저장**: Refresh Token을 Redis에 저장

**Response**
OAuth 로그인과 동일한 형식

**주의사항**
- **개발/테스트 환경 전용**
- 실제 Google OAuth 검증을 하지 않음
- 프로덕션 환경에서는 비활성화 권장

---

## 🔄 토큰 재발급 API

### 3. 토큰 재발급

Refresh Token을 사용하여 새로운 Access Token과 Refresh Token을 발급합니다.

#### `POST /api/auth/reissue`

**Request Headers**
```http
Authorization: Bearer {refreshToken}
Content-Type: application/json
```

**처리 과정**
1. **Refresh Token 추출**: Authorization 헤더에서 토큰 추출
2. **Redis 검증**: Refresh Token이 Redis에 존재하는지 확인
3. **토큰 타입 검증**: JWT에서 tokenType이 "refresh"인지 확인
4. **기존 토큰 삭제**: 해당 사용자의 모든 Refresh Token 삭제 (Token Rotation)
5. **새 토큰 발급**: 새 Access Token + Refresh Token 생성
6. **Redis 저장**: 새 Refresh Token을 Redis에 저장

**Response**
```json
{
  "timestamp": "2025-05-25T10:30:00",
  "status": "OK",
  "message": "토큰 재발급 성공",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  }
}
```

**에러 응답**
| 상태코드 | 에러 케이스 | 응답 메시지 |
|---------|-----------|-----------|
| **400** | Access Token 전송 | `"Refresh Token이 아닙니다."` |
| **401** | 헤더 없음 | `"Authorization 헤더가 비어 있습니다."` |
| **401** | Redis에 없는 토큰 | `"유효하지 않은 리프레시 토큰입니다."` |

---

## 🚪 로그아웃 API

### 4. 로그아웃

Access Token을 블랙리스트에 등록하고 Refresh Token을 삭제합니다.

#### `POST /api/auth/logout`

**Request Headers**
```http
Authorization: Bearer {accessToken}
Refresh-Token: {refreshToken}
Content-Type: application/json
```

**처리 과정**
1. **토큰 추출**: 헤더에서 Access Token, Refresh Token 추출
2. **Refresh Token 검증**: Redis에서 Refresh Token 유효성 확인
3. **Access Token 블랙리스트 등록**: Redis에 Access Token 해시값 저장 (TTL: 토큰 남은 만료시간)
4. **Refresh Token 삭제**: Redis에서 Refresh Token 삭제

**Response**
```json
{
  "timestamp": "2025-05-25T10:30:00",
  "status": "OK",
  "message": "로그아웃 성공",
  "data": null
}
```

**에러 응답**
| 상태코드 | 에러 케이스 | 응답 메시지 |
|---------|-----------|-----------|
| **401** | 헤더 없음 | `"Authorization 헤더가 비어 있습니다."` |
| **401** | 유효하지 않은 Refresh Token | `"유효하지 않은 리프레시 토큰입니다."` |

---

## 🔒 JWT 토큰 구조

### Access Token Payload
```json
{
  "userId": 123,
  "name": "홍길동", 
  "roles": ["ROLE_USER"],
  "tokenType": "access",
  "iat": 1716630600,
  "exp": 1716634200
}
```

### Refresh Token Payload
```json
{
  "userId": 123,
  "name": "홍길동",
  "tokenType": "refresh",
  "iat": 1716630600,
  "exp": 1716990600
}
```

**토큰 만료시간**
- **Access Token**: 1시간 (3,600,000ms)
- **Refresh Token**: 100시간 (360,000,000ms)

---

## 💾 Redis 데이터 구조

### Refresh Token 저장
```
Key: refresh:{userId}:{refreshToken}
Value: Hash
  ├── userId: "123"
  ├── name: "홍길동"
TTL: 100시간
```

### Access Token Blacklist
```
Key: blacklist:{sha256(accessToken)}
Value: "blacklisted"
TTL: 토큰 남은 만료시간
```

---

## 📊 API 사용 시나리오

### 1. 신규 회원 플로우
```
1. POST /api/auth/oauth/login
   → { "isNewUser": true, "accessToken": null }
   
2. 앱에서 회원가입 진행
   
3. 회원가입 완료 후 다시 OAuth 로그인
   → { "isNewUser": false, "accessToken": "...", "refreshToken": "..." }
```

### 2. 기존 회원 로그인 플로우
```
1. POST /api/auth/oauth/login
   → { "isNewUser": false, "accessToken": "...", "refreshToken": "..." }
   
2. 토큰 저장 후 다른 API 호출
```

### 3. 토큰 갱신 플로우
```
1. 다른 API 호출 시 401 응답 (Gateway에서 토큰 만료 감지)
   
2. POST /api/auth/reissue
   → { "accessToken": "...", "refreshToken": "..." }
   
3. 새 토큰으로 API 재호출
```

### 4. 로그아웃 플로우
```
1. POST /api/auth/logout
   → Auth Service: 블랙리스트 등록 + Refresh Token 삭제
   
2. 이후 API 호출 시 Gateway에서 블랙리스트 체크로 차단
```

---

## 🛡️ 보안 특징

### Token Rotation
- **Refresh Token 사용 시마다 새 토큰 발급**
- **기존 Refresh Token 즉시 무효화**
- 토큰 탈취 시 한 번만 사용 가능

### 블랙리스트 관리
- **로그아웃된 Access Token 무효화**
- **SHA256 해시로 안전하게 저장**
- **TTL로 자동 정리** (토큰 만료시간까지)

### 역할 분담
- **Auth Service**: 토큰 발급/무효화, Redis 관리
- **Gateway**: 실제 토큰 검증, 블랙리스트 확인, 라우팅

---

## 🧪 테스트 가이드

### 토큰 발급 테스트
```http
POST http://localhost:8080/api/auth/oauth/login
Content-Type: application/json

{
  "provider": "google",
  "idToken": "실제_구글_ID_토큰"
}
```

### 토큰 재발급 테스트
```http
POST http://localhost:8080/api/auth/reissue
Authorization: Bearer {refreshToken}
```

### 로그아웃 테스트
```http
POST http://localhost:8080/api/auth/logout
Authorization: Bearer {accessToken}
Refresh-Token: {refreshToken}
```

### 개발용 테스트
```http
POST http://localhost:8080/api/auth/test/login
Content-Type: application/json

{
  "email": "test@example.com",
  "provider": "google"
}
```

---

## 📝 연동 정보

### Gateway 라우팅
```
Client → Gateway → Auth Service
```
Gateway에서 `/api/auth/**` 경로를 Auth Service로 라우팅

### User Service 연동
OAuth 로그인 시 User Service API 호출:
```http
GET http://user-service/api/users/exist?email={email}&provider={provider}
```

### Google API 연동
ID Token 검증을 위해 Google OAuth API 호출:
```http
GET https://oauth2.googleapis.com/tokeninfo?id_token={idToken}
```

### Redis 의존성
모든 토큰 기능이 Redis에 의존하므로 Redis 서버 필수