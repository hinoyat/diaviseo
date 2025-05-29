# Gateway Service 개요

**디아비서(DiaViseo) 프로젝트의 API Gateway 서비스**로, 모든 마이크로서비스의 진입점 역할을 하며 라우팅, 인증, 로깅, 보안을 담당합니다.

---

## 📌 서비스 개요

### 역할과 책임
- **API 라우팅**: 클라이언트 요청을 적절한 마이크로서비스로 전달
- **JWT 인증/인가**: Access Token 검증 및 블랙리스트 관리
- **보안 필터링**: 인증이 필요한 API와 공개 API 구분
- **요청/응답 로깅**: 모든 API 호출에 대한 로깅 및 모니터링
- **에러 핸들링**: 전역 예외 처리 및 표준화된 오류 응답

### MSA 구조에서의 위치
```
Android App (Port: 외부)
        ↓
┌─────────────────────────────────────────────────────────────┐
│              Gateway Service (Port: 8080)                   │
├─────────────────────────────────────────────────────────────┤
│  Global Filters: 로깅 → JWT 인증 → 에러 처리 → 응답시간         │
└─────────────────────────────────────────────────────────────┘
        ↓ (라우팅)
┌─────────────────────────────────────────────────────────────┐
│  Auth Service     │  User Service   │  Health Service      │
│  (인증 불필요)       │  (일부 인증 불필요) │  (인증 필요)          │
└─────────────────────────────────────────────────────────────┘
        ↑
   Redis (블랙리스트)
```

Gateway Service는 **모든 외부 요청의 단일 진입점**으로, 보안과 라우팅을 중앙에서 관리합니다.

---

## ⚙️ 기술 스택 & 의존성

| 구분 | 기술 | 버전 | 용도 |
|-----|-----|-----|-----|
| **Framework** | Spring Boot | 3.4.5 | 기본 프레임워크 |
| **Gateway** | Spring Cloud Gateway | 2024.0.1 | 라우팅 및 필터 |
| **Reactive** | Spring WebFlux | - | 비동기 처리 |
| **JWT** | JJWT | 0.11.5 | JWT 토큰 검증 |
| **Cache** | Redis (Reactive) | - | 블랙리스트 관리 |
| **Service Discovery** | Eureka Client | - | 서비스 발견 |
| **Config** | Spring Cloud Config | - | 중앙 설정 관리 |
| **Monitoring** | Micrometer Prometheus | - | 메트릭 수집 |

### 주요 특징
- **WebFlux 기반**: 비동기/논블로킹 처리로 높은 성능
- **Reactive Redis**: 비동기 Redis 연동으로 블랙리스트 관리
- **Load Balancing**: Eureka 기반 서비스 발견 및 로드밸런싱

---

## 🔧 핵심 기능

### 1. 라우팅 관리
**공개 API (인증 불필요)**
- `/api/auth/**`: Auth Service - OAuth 로그인, 토큰 관리  
- `/api/users/signup`: User Service - 회원가입
- `/api/users/verify/**`: User Service - SMS 인증

**인증 필요 API**
- `/api/users/**`: User Service - 회원 관리
- `/api/exercises/**`: Health Service - 운동 관리
- `/api/bodies/**`: Health Service - 신체 정보
- `/api/foods/**`: Health Service - 음식 정보
- `/api/meals/**`: Health Service - 식단 관리
- `/api/food-sets/**`: Health Service - 음식 세트

### 2. JWT 인증 시스템
**토큰 검증 과정**
1. **Authorization 헤더 추출**: `Bearer {token}` 형식 확인
2. **블랙리스트 확인**: Redis에서 로그아웃된 토큰 여부 체크
3. **JWT 서명 검증**: 토큰 서명 및 만료시간 확인
4. **토큰 타입 검증**: `tokenType`이 `access`인지 확인
5. **사용자 ID 추출**: `X-USER-ID` 헤더로 백엔드 서비스에 전달

### 3. 블랙리스트 관리
- **Redis 기반**: 로그아웃된 토큰을 블랙리스트로 관리
- **SHA-256 해싱**: 토큰을 해시하여 안전하게 저장
- **비동기 처리**: ReactiveRedisTemplate로 논블로킹 조회

### 4. 전역 필터 체인
```
요청 → RequestLoggingFilter → JwtAuthFilter → 비즈니스 로직 → ResponseTimeFilter → 응답
              ↓                    ↓                               ↓
         요청 로깅              JWT 검증 실패 시                 처리시간 측정
                            GlobalErrorHandlingFilter
                                   ↓
                              에러 응답 반환
```

---

## 🏗️ 시스템 아키텍처

### Gateway Service 내부 구조
```
┌─────────────────────────────────────────────────────────────┐
│                    Gateway Service                          │
├─────────────────────────────────────────────────────────────┤
│  Filter Layer (Global Filters)                             │
│  ├── RequestLoggingFilter (Order: 1)                       │
│  ├── ResponseTimeFilter (Order: MAX)                       │
│  ├── GlobalErrorHandlingFilter (Order: MAX)                │
├─────────────────────────────────────────────────────────────┤
│  Authentication Layer                                        │
│  ├── JwtAuthFilter (Route별 적용)                           │
│  ├── TokenBlacklistService (Redis 연동)                    │
├─────────────────────────────────────────────────────────────┤
│  Routing Layer                                               │
│  ├── Spring Cloud Gateway Routes                           │
│  ├── Load Balancer (Eureka 기반)                          │
├─────────────────────────────────────────────────────────────┤
│  Utility Layer                                              │
│  ├── JwtUtil (토큰 검증/파싱)                               │
│  ├── GatewayResponseUtil (응답 생성)                       │
│  ├── RedisConfig (ReactiveRedis 설정)                      │
└─────────────────────────────────────────────────────────────┘
```

### 외부 의존성
- **Config Service**: 중앙 설정 관리 (`configserver:http://localhost:8888`)
- **Eureka Server**: 서비스 발견 및 로드밸런싱
- **Redis**: 블랙리스트 토큰 저장소
- **각 마이크로서비스**: 실제 비즈니스 로직 처리

---

## 🔄 요청 처리 플로우

### 1. 인증 불필요 API 플로우
```
1. 클라이언트 → Gateway: POST /api/auth/oauth/login
2. RequestLoggingFilter: 요청 로깅
3. 라우팅: lb://auth-service로 전달 (JwtAuthFilter 적용 안함)
4. Auth Service: OAuth 로그인 처리
5. ResponseTimeFilter: 응답시간 측정 및 로깅
6. 클라이언트 ← Gateway: 로그인 응답 (토큰 포함)
```

### 2. 인증 필요 API 플로우 (성공)
```
1. 클라이언트 → Gateway: GET /api/users/profile (Authorization: Bearer token)
2. RequestLoggingFilter: 요청 및 토큰 로깅 (마스킹)
3. JwtAuthFilter:
   - 토큰 추출: Bearer token
   - 블랙리스트 확인: Redis 조회 → 정상
   - JWT 검증: 서명/만료/타입 확인 → 유효
   - 사용자 ID 추출: userId = 123
4. 라우팅: lb://user-service로 전달 (X-USER-ID: 123 헤더 추가)
5. User Service: 프로필 조회 처리
6. ResponseTimeFilter: 응답시간 측정
7. 클라이언트 ← Gateway: 사용자 프로필 응답
```

### 3. 인증 실패 API 플로우
```
1. 클라이언트 → Gateway: GET /api/users/profile (잘못된 토큰)
2. RequestLoggingFilter: 요청 로깅
3. JwtAuthFilter:
   - 토큰 추출: 성공
   - 블랙리스트 확인: Redis 조회 → 블랙리스트에 존재!
4. GatewayResponseUtil: 에러 응답 생성
5. 클라이언트 ← Gateway: 401 Unauthorized
   {
     "timestamp": "2025-05-26T...",
     "status": 401,
     "message": "해당 토큰은 로그아웃된 상태입니다.",
     "data": { "gateway": true }
   }
```

---

## 🗂️ 주요 구성 요소

### Filter Layer
| 클래스 | 순서 | 책임 | 주요 기능 |
|-------|------|------|----------|
| **RequestLoggingFilter** | 1 | 요청 로깅 | HTTP 메서드, 경로, 토큰(마스킹) 로깅 |
| **JwtAuthFilter** | Route별 | JWT 인증 | 토큰 검증, 블랙리스트 확인, 사용자 ID 추출 |
| **ResponseTimeFilter** | MAX | 성능 측정 | API 처리 시간 측정 및 로깅 |
| **GlobalErrorHandlingFilter** | MAX | 예외 처리 | 전역 예외 처리 및 표준화된 오류 응답 |

### Service Layer
| 클래스 | 책임 | 주요 메서드 |
|-------|------|----------|
| **TokenBlacklistService** | 블랙리스트 관리 | `isBlacklisted(token)` |

### Utility Layer
| 클래스 | 책임 | 주요 메서드 |
|-------|------|----------|
| **JwtUtil** | JWT 토큰 처리 | `validateToken()`, `getUserId()`, `parseClaims()` |
| **GatewayResponseUtil** | 응답 생성 | `writeJsonError()` |

### Configuration
| 클래스 | 책임 | 주요 설정 |
|-------|------|----------|
| **RedisConfig** | Redis 연결 | ReactiveRedisTemplate, ConnectionFactory |

---

## 🛣️ 라우팅 규칙 상세

### 인증 불필요 라우트
| 라우트 ID | 경로 패턴 | 대상 서비스 | 용도 |
|----------|----------|-----------|------|
| **auth-service** | `/api/oauth2/**`, `/api/login/**`, `/api/auth/**` | lb://auth-service | OAuth 로그인, 토큰 관리 |
| **signup-service** | `/api/users/signup` | lb://user-service | 회원가입 |
| **sms-service** | `/api/users/verify/**` | lb://user-service | SMS 인증 |

### 인증 필요 라우트 (JwtAuthFilter 적용)
| 라우트 ID | 경로 패턴 | 대상 서비스 | 용도 |
|----------|----------|-----------|------|
| **user-service** | `/api/users/**` | lb://user-service | 회원 관리 |
| **exercise-service** | `/api/exercises/**` | lb://health-service | 운동 관리 |
| **body-service** | `/api/bodies/**` | lb://health-service | 신체 정보 |
| **food-service** | `/api/foods/**` | lb://health-service | 음식 정보 |
| **meal-service** | `/api/meals/**` | lb://health-service | 식단 관리 |
| **food-set-service** | `/api/food-sets/**` | lb://health-service | 음식 세트 |

### Load Balancing
```yaml
uri: lb://service-name  # Eureka 기반 로드밸런싱
```
- **Round Robin**: 기본 로드밸런싱 알고리즘
- **Health Check**: Eureka에서 건강한 인스턴스만 라우팅
- **동적 포트**: 모든 서비스가 동적 포트 사용 (port: 0)

---

## 💾 Redis 연동 (블랙리스트)

### 블랙리스트 데이터 구조
```
Key: blacklist:{sha256(accessToken)}
Value: "blacklisted"
TTL: Auth Service에서 설정 (토큰 남은 만료시간)
```

### Reactive Redis 설정
```java
// RedisConfig.java
@Bean
public ReactiveRedisTemplate<String, Object> reactiveRedisTemplate() {
    // String 키 + JSON 값 직렬화
    StringRedisSerializer keySerializer = new StringRedisSerializer();
    Jackson2JsonRedisSerializer<Object> valueSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
    // 비동기 Redis 템플릿 반환
}
```

### 블랙리스트 조회 로직
```java
public Mono<Boolean> isBlacklisted(String token) {
    // 1. 토큰을 SHA-256으로 해싱
    String tokenHash = DigestUtils.sha256Hex(token);
    String key = "blacklist:" + tokenHash;
    
    // 2. Redis에서 키 존재 여부 확인 (비동기)
    return redisTemplate.hasKey(key)
        .onErrorResume(e -> Mono.just(false)); // 오류 시 허용
}
```

---

## 🔐 보안 고려사항

### JWT 토큰 보안
- **토큰 타입 검증**: Access Token만 허용, Refresh Token 차단
- **서명 검증**: HMAC-SHA256 기반 토큰 서명 확인
- **만료시간 검증**: 자동으로 만료된 토큰 차단
- **블랙리스트**: 로그아웃된 토큰 실시간 차단

### 토큰 처리 보안
- **토큰 마스킹**: 로그에 토큰 전체 노출 방지 (앞 20자만 로깅)
- **SHA-256 해싱**: 블랙리스트 저장 시 원본 토큰 보호
- **헤더 변조 방지**: 검증된 사용자 ID만 백엔드에 전달

### 네트워크 보안
- **내부 통신**: 마이크로서비스 간 내부 네트워크 통신
- **CORS 미설정**: 별도 CORS 필터 없음 (필요 시 추가 가능)
- **Rate Limiting**: 현재 미적용 (향후 추가 고려)

---

## 📊 모니터링 및 로깅

### Actuator 엔드포인트
| 엔드포인트 | URL | 용도 |
|-----------|-----|------|
| **Health Check** | `/actuator/health` | Gateway 상태 확인 |
| **Gateway Routes** | `/actuator/gateway/routes` | 라우팅 규칙 조회 |
| **Metrics** | `/actuator/prometheus` | Prometheus 메트릭 |
| **All Endpoints** | `/actuator` | 모든 모니터링 정보 |

### 로깅 정보
**요청 로깅**
```
[요청 로그] GET /api/users/profile
[요청 헤더] Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
[요청 헤더] X-USER-ID: 123
```

**인증 로깅**
```
[인증 완료] 유효한 JWT → X-USER-ID: 123
[JWT 필터] 블랙리스트 토큰 접근 차단
[JWT 필터] 유효하지 않은 토큰
```

**성능 로깅**
```
[처리 시간] /api/users/profile → 245 ms
```

### 메트릭 수집
- **HTTP 요청 수**: 라우트별 API 호출 횟수
- **응답 시간**: API별 평균/최대 처리 시간
- **에러율**: HTTP 상태코드별 에러 발생률
- **JWT 검증**: 성공/실패 비율

---

## ⚡ 성능 최적화

### WebFlux 비동기 처리
- **Non-blocking I/O**: 높은 동시성 처리 가능
- **Reactive Redis**: 블랙리스트 조회도 비동기 처리
- **Backpressure**: 과부하 시 자동으로 흐름 제어

### 메모리 효율성
- **토큰 해싱**: 원본 토큰을 메모리에 보관하지 않음
- **필터 체인**: 인증 실패 시 조기 반환으로 리소스 절약
- **Connection Pool**: Redis 연결 풀 관리

### 확장성
- **Stateless**: Gateway 자체는 상태를 저장하지 않음
- **Load Balancing**: 다중 Gateway 인스턴스 배포 가능
- **Service Discovery**: 백엔드 서비스 확장 시 자동 감지

---

## 🚀 확장 및 개선 방안

### 보안 강화
**Rate Limiting 추가**
```java
// 향후 구현 예정
@Component
public class RateLimitingFilter implements GlobalFilter {
    // IP별 요청 제한
    // 사용자별 요청 제한
    // Sliding Window 알고리즘 적용
}
```

**CORS 설정**
```yaml
spring:
  cloud:
    gateway:
      globalcors:
        cors-configurations:
          '[/**]':
            allowedOrigins: "https://diaviseo.com"
            allowedMethods: "*"
            allowedHeaders: "*"
```

### 모니터링 강화
- **분산 트레이싱**: Spring Cloud Sleuth + Zipkin 연동
- **알림 시스템**: 에러율 임계치 초과 시 알림
- **대시보드**: Grafana 기반 실시간 모니터링

### 캐싱 최적화
- **JWT 검증 결과 캐싱**: 동일 토큰 반복 검증 방지
- **라우팅 캐싱**: 자주 사용되는 라우팅 규칙 캐싱
- **블랙리스트 캐싱**: Redis 부하 감소

---

## 🧪 테스트 가이드

### 라우팅 테스트
```bash
# 인증 불필요 API
curl -X POST http://localhost:8080/api/auth/oauth/login \
  -H "Content-Type: application/json" \
  -d '{"provider":"google","idToken":"test-token"}'

# 인증 필요 API (유효한 토큰)
curl -X GET http://localhost:8080/api/users/profile \
  -H "Authorization: Bearer valid-jwt-token"

# 인증 필요 API (잘못된 토큰)
curl -X GET http://localhost:8080/api/users/profile \
  -H "Authorization: Bearer invalid-token"
```

### 블랙리스트 테스트
```bash
# 1. 로그인 → 토큰 획득
# 2. API 호출 → 성공
# 3. 로그아웃 → 토큰 블랙리스트 등록
# 4. 동일 토큰으로 API 호출 → 401 에러
```

### 모니터링 테스트
```bash
# Gateway 상태 확인
curl http://localhost:8080/actuator/health

# 라우팅 규칙 확인
curl http://localhost:8080/actuator/gateway/routes

# 메트릭 확인
curl http://localhost:8080/actuator/prometheus
```

---

## 📝 참고사항

### 현재 구조의 장단점

**장점:**
- **중앙집중식 보안**: 모든 인증을 Gateway에서 처리
- **일관된 로깅**: 모든 API 호출이 표준화된 로그 생성
- **높은 성능**: WebFlux 기반 비동기 처리
- **확장성**: 마이크로서비스 추가 시 라우팅 규칙만 추가

**개선 필요사항:**
- **Rate Limiting**: 현재 미적용, DoS 공격 방어 필요
- **CORS 설정**: 웹 클라이언트 지원 시 CORS 정책 필요
- **Circuit Breaker**: 백엔드 서비스 장애 시 Circuit Breaker 패턴 적용
- **분산 트레이싱**: 요청 추적을 위한 Trace ID 생성 필요

### JWT 토큰 타입 제한
현재 Gateway는 **Access Token만 허용**하도록 구현되어 있습니다:
```java
// JwtUtil.validateToken()
String tokenType = (String) claims.get("tokenType");
if (!"access".equals(tokenType)) {
    return Mono.just(false); // Refresh Token 차단
}
```

이는 보안상 올바른 설계로, Refresh Token은 오직 Auth Service에서만 사용되어야 합니다.

### 포트 설정
```yaml
server:
  port: 8080  # Gateway는 고정 포트 사용
```
Gateway는 외부 접점이므로 고정 포트(8080)를 사용하며, 내부 서비스들은 동적 포트를 사용합니다.