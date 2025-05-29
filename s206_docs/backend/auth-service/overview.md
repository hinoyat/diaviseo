# Auth Service 개요

**디아비서(DiaViseo) 프로젝트의 인증/인가 서비스**로, 사용자 로그인부터 토큰 관리까지 모든 인증 관련 기능을 담당합니다.

---

## 📌 서비스 개요

### 역할과 책임
- **모바일 OAuth 로그인** 처리 (Google 소셜 로그인)
- **JWT 토큰 발급 및 관리** (Access Token / Refresh Token)
- **토큰 재발급** 및 **로그아웃** 처리
- **Redis 기반 토큰 저장소** 관리
- **다른 서비스와의 인증 정보 연동** (User Service)

### MSA 구조에서의 위치
```
Android App → Gateway → Auth Service ↔ User Service
                   ↓
                 Redis (토큰 저장소)
```

Auth Service는 **인증의 중심점**으로, 모든 서비스의 사용자 인증을 책임집니다.

---

## ⚙️ 기술 스택 & 의존성

| 구분 | 기술 | 버전 | 용도 |
|-----|-----|-----|-----|
| **Framework** | Spring Boot | 3.4.5 | 기본 프레임워크 |
| **Security** | Spring Security | - | 보안 설정 (CORS, CSRF) |
| **JWT** | jjwt | - | JWT 토큰 생성/검증 |
| **Cache** | Redis | - | Refresh Token 저장 |
| **Communication** | WebFlux | - | 외부 API 호출 |
| **Service Discovery** | Eureka Client | - | 마이크로서비스 등록 |
| **Config** | Spring Cloud Config | - | 중앙 설정 관리 |

### 주요 의존성
- **Common Module**: 공통 예외처리, ResponseDto, 암호화 기능
- **User Service**: 회원 존재 여부 확인 및 사용자 정보 조회
- **Redis**: Refresh Token, Access Token Blacklist 저장

---

## 🔧 핵심 기능

### 1. Mobile OAuth 로그인
- **Google OAuth 2.0** 기반 소셜 로그인
- Android 앱에서 받은 **ID Token 검증**
- Google API를 통한 사용자 정보 확인

### 2. JWT 토큰 관리
- **Access Token**: API 접근용 (1시간 유효)
- **Refresh Token**: 토큰 갱신용 (100시간 유효)
- **토큰 타입 구분**: access/refresh 타입 명시

### 3. 토큰 생명주기 관리
- **토큰 재발급**: Refresh Token 사용 시 새 Access Token + 새 Refresh Token 모두 발급
- **로그아웃**: Access Token 블랙리스트 등록, Refresh Token 삭제
- **중복 로그인 방지**: 기존 Refresh Token 자동 삭제

### 4. Redis 기반 저장소
- **Refresh Token 저장**: 사용자별 토큰 관리
- **Blacklist 관리**: 로그아웃된 Access Token 관리
- **TTL 자동 관리**: 토큰 만료시간에 맞춰 자동 삭제

---

## 🏗️ 시스템 아키텍처

### Auth Service 내부 구조
```
┌─────────────────────────────────────────────────────────────┐
│                    Auth Service                             │
├─────────────────────────────────────────────────────────────┤
│  Controller Layer                                           │
│  ├── AuthController (OAuth 로그인, 토큰 재발급, 로그아웃)        │
├─────────────────────────────────────────────────────────────┤
│  Service Layer                                              │
│  ├── AuthService (핵심 비즈니스 로직)                          │
│  ├── TokenBlacklistService (블랙리스트 관리)                   │
├─────────────────────────────────────────────────────────────┤
│  External Integration                                        │
│  ├── GoogleTokenVerifier (Google API 토큰 검증)               │
│  ├── UserServiceClient (User Service 통신)                   │
├─────────────────────────────────────────────────────────────┤
│  JWT & Security                                             │
│  ├── JwtProvider (토큰 생성/검증)                             │
│  ├── RefreshTokenRedisRepository (Redis 토큰 저장)           │
└─────────────────────────────────────────────────────────────┘
```

### 외부 서비스 연동
- **User Service**: `GET /api/users/exist` - 회원 존재 여부 확인
- **Google OAuth API**: `https://oauth2.googleapis.com/tokeninfo` - ID Token 검증
- **Redis**: Refresh Token 저장 및 Access Token 블랙리스트 관리

---

## 🔄 인증 플로우

### 1. Mobile OAuth 로그인 플로우
```
1. Android App → Google OAuth 처리 → ID Token 획득
2. App → Auth Service: POST /api/auth/oauth/login (ID Token 전송)
3. Auth Service → Google API: ID Token 검증 및 사용자 정보 획득
4. Auth Service → User Service: 기존 회원 여부 확인
5. 기존 회원인 경우:
   - Refresh Token 생성 → Redis 저장
   - Access Token, Refresh Token 발급
6. 신규 회원인 경우:
   - isNewUser: true 응답 (토큰 발급 안함)
```

### 2. 토큰 재발급 플로우
```
1. App → Auth Service: POST /api/auth/reissue (Refresh Token)
2. Auth Service: Refresh Token 유효성 검증 (Redis 조회)
3. 기존 Refresh Token 삭제
4. 새 Access Token, Refresh Token 발급
5. 새 Refresh Token → Redis 저장
```

### 3. 로그아웃 플로우
```
1. App → Auth Service: POST /api/auth/logout (Access + Refresh Token)
2. Auth Service: Refresh Token 유효성 검증
3. Access Token → 블랙리스트 등록 (Redis)
4. Refresh Token → Redis에서 삭제
```

---

## 🗂️ 주요 구성 요소

### Controller Layer
| 클래스 | 책임 | 주요 메서드 |
|-------|------|----------|
| **AuthController** | HTTP 요청 처리 | `oauthLogin()`, `reissue()`, `logout()` |

### Service Layer
| 클래스 | 책임 | 주요 메서드 |
|-------|------|----------|
| **AuthService** | 핵심 인증 로직 | `oauthLogin()`, `reissue()`, `logout()` |
| **TokenBlacklistService** | 블랙리스트 관리 | `addToBlacklist()`, `isBlacklisted()` |

### External Integration
| 클래스 | 책임 | 주요 메서드 |
|-------|------|----------|
| **GoogleTokenVerifier** | Google 토큰 검증 | `verify()` |
| **UserServiceClient** | User Service 통신 | `getUserInfo()` |
아! 맞네요. 주요 구성 요소에서 빠진 게 많아요!

**추가해야 할 구성 요소들:**

### Configuration
| 클래스 | 책임 | 주요 설정 |
|-------|------|----------|
| **SecurityConfig** | Spring Security 설정 | CORS, CSRF, 인증 경로 설정 |
| **RedisConfig** | Redis 연결 설정 | RedisTemplate, ConnectionFactory |

### DTO Classes
| 클래스 | 용도 |
|-------|------|
| **OAuthLoginRequest** | OAuth 로그인 요청 DTO |
| **TestLoginRequest** | 테스트 로그인 요청 DTO |
| **OAuthLoginResponse** | 로그인 응답 DTO |
| **ReissueResponse** | 토큰 재발급 응답 DTO |
| **UserExistResponse** | User Service 응답 DTO |

### Legacy Components (미사용)
| 클래스 | 상태 | 비고 |
|-------|------|------|
| **CustomOAuth2UserService** | 미사용 | Web OAuth 전용 |
| **CustomOAuth2User** | 미사용 | Web OAuth 전용 |
| **OAuth2SuccessHandler** | 미사용 | Web OAuth 전용 |

### JWT & Security
| 클래스 | 책임 | 주요 메서드 |
|-------|------|----------|
| **JwtProvider** | JWT 생성/검증 | `createAccessToken()`, `createRefreshToken()` |
| **RefreshTokenRedisRepository** | Redis 토큰 저장 | `save()`, `findByRefreshToken()`, `delete()` |

---

## 💾 데이터 저장소 (Redis)

### Refresh Token 저장 구조
```
Key: refresh:{userId}:{refreshToken}
Value: Hash
  ├── userId: {사용자ID}
  ├── name: {사용자명}
TTL: 100시간 (360,000,000ms)
```

### Access Token Blacklist 구조
```
Key: blacklist:{token_hash}
Value: "blacklisted"
TTL: 토큰 남은 만료시간
```

### Redis 키 관리 특징
- **자동 TTL 관리**: 토큰 만료시간에 맞춰 자동 삭제
- **사용자별 토큰 관리**: userId로 해당 사용자의 모든 토큰 조회/삭제 가능
- **보안**: Access Token은 SHA256 해시로 저장하여 원본 노출 방지

---

## 🔐 보안 고려사항

### JWT 토큰 보안
- **토큰 타입 구분**: access/refresh 타입을 토큰에 명시
- **짧은 Access Token 수명**: 1시간으로 제한
- **Refresh Token 순환**: 재발급 시 기존 토큰 무효화

### Redis 보안
- **토큰 해싱**: 블랙리스트 저장 시 SHA256 해시 사용
- **TTL 관리**: 불필요한 데이터 자동 삭제
- **패스워드 인증**: Redis 접근 시 패스워드 필수

### API 보안
- **CORS 설정**: 허용된 Origin만 접근 가능
- **CSRF 비활성화**: Stateless JWT 방식 사용
- **Actuator 보안**: 필요한 엔드포인트만 노출

---

## 📋 참고사항 (코드 정리 필요)

### ❌ Web OAuth 관련 코드 (현재 미사용)
프로젝트 초기에 Web 기반 OAuth를 고려하여 구현된 코드들이 남아있습니다:

**미사용 클래스들:**
- `CustomOAuth2UserService` - Spring Security OAuth2 Web 전용 서비스
- `CustomOAuth2User` - OAuth2User 커스텀 구현체  
- `OAuth2SuccessHandler` - OAuth2 로그인 성공 핸들러

**미사용 설정:**
```java
// SecurityConfig.java 내
.oauth2Login(oauth2 -> oauth2
    .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
    .successHandler(oAuth2SuccessHandler));
```

**미사용 application.yml 설정:**
```yaml
spring:
  security:
    oauth2:
      client:
        registration:
          google: # 전체 OAuth2 클라이언트 설정
```

### ✅ 실제 사용되는 Mobile OAuth
Android 환경에서는 다음 방식을 사용합니다:
- **GoogleTokenVerifier**: 앱에서 받은 ID Token을 Google API로 직접 검증
- **직접 토큰 검증**: Spring Security OAuth2 라이브러리 대신 WebClient 사용
- **RESTful API**: `/api/auth/oauth/login` 엔드포인트로 토큰 기반 인증

### 🔧 향후 개선사항
1. **미사용 Web OAuth 코드 제거**: 코드 복잡도 감소
2. **SecurityConfig 단순화**: Mobile OAuth만 고려한 설정으로 변경
3. **설정 파일 정리**: 불필요한 OAuth2 클라이언트 설정 제거

---

## ⚙️ 환경 설정

### 필수 환경변수
| 변수명 | 설명 | 예시 |
|-------|------|------|
| `spring.data.redis.host` | Redis 호스트 | localhost |
| `spring.data.redis.port` | Redis 포트 | 6379 |
| `spring.data.redis.password` | Redis 패스워드 | 1234 |
| `jwt.secret` | JWT 서명 키 | (Base64 인코딩된 키) |
| `jwt.expiration` | Access Token 만료시간 | 3600000 (1시간) |
| `jwt.refresh-expiration` | Refresh Token 만료시간 | 360000000 (100시간) |

### Service Discovery
```yaml
eureka:
  instance:
    instance-id: ${spring.cloud.client.ip-address}:${random.value}
```

### Config Server 연동
```yaml
  config:
    import: configserver:http://localhost:8888
```

### Actuator 설정
```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,prometheus
```

---

## 🔄 확장 가능성

### 추가 OAuth 프로바이더
현재 Google만 지원하지만, 다음과 같이 확장 가능:
- **Kakao, Naver**: 각 프로바이더별 TokenVerifier 추가
- **Apple**: iOS 지원을 위한 Apple OAuth 추가

### 보안 강화
- **토큰 암호화**: JWT 페이로드 암호화 적용
- **디바이스 바인딩**: 특정 디바이스에서만 토큰 사용 가능
- **생체 인증 연동**: 지문/얼굴 인식과 JWT 연동

### 성능 최적화
- **Redis 클러스터링**: 고가용성 확보
- **토큰 캐싱**: 자주 사용되는 토큰 정보 캐싱
- **비동기 처리**: 외부 API 호출 최적화