# Config Service 개요

**디아비서(DiaViseo) 프로젝트의 중앙 설정 관리 서비스**로, 모든 마이크로서비스의 설정 정보를 중앙에서 관리하고 배포합니다.

---

## 📌 서비스 개요

### 역할과 책임
- **중앙 설정 관리**: 모든 마이크로서비스의 설정 파일 중앙 관리
- **환경별 설정 분리**: 개발/테스트/운영 환경별 설정 제공
- **설정 암호화**: 민감한 정보(DB 비밀번호, API 키) 암호화 관리
- **동적 설정 배포**: 서비스 재시작 없이 설정 변경 배포
- **설정 버전 관리**: 설정 변경 이력 추적

### MSA 구조에서의 위치
```
Config Service (Port: 8888)
        ↓
┌─────────────────────────────────────┐
│  Configurations (classpath:/configurations)  │
├─────────────────────────────────────┤
│ ├── application.yml     (공통 설정)    │
│ ├── auth-service.yml                │
│ ├── user-service.yml                │
│ ├── health-service.yml              │
│ ├── gateway-service.yml             │
│ ├── alert-service.yml               │
│ └── rabbitmq.yml                    │
└─────────────────────────────────────┘
        ↓
   All Microservices
```

---

## ⚙️ 기술 스택 & 의존성

| 구분 | 기술 | 버전 | 용도 |
|-----|-----|-----|-----|
| **Framework** | Spring Boot | 3.4.5 | 기본 프레임워크 |
| **Config** | Spring Cloud Config Server | 2024.0.1 | 설정 서버 |
| **Storage** | Native File System | - | 로컬 파일 기반 설정 저장 |
| **Monitoring** | Spring Actuator | - | 서비스 상태 모니터링 |
| **Metrics** | Micrometer Prometheus | - | 메트릭 수집 |
| **Security** | Spring Security Crypto | - | 설정 값 암호화 |

### 설정 저장 방식
- **Native Mode**: 로컬 파일 시스템 (`classpath:/configurations`)
- **Version Control**: Git 연동 없이 로컬 파일 관리
- **Profile 지원**: 환경별 설정 프로파일 관리

---

## 🗂️ 관리 대상 서비스

### 1. 비즈니스 서비스
| 서비스 | 설정 파일 | 주요 설정 내용 |
|-------|----------|--------------|
| **Auth Service** | `auth-service.yml` | JWT 설정, Redis, OAuth2 클라이언트 |
| **User Service** | `user-service.yml` | MySQL, Redis, CoolSMS API |
| **Health Service** | `health-service.yml` | MySQL, Elasticsearch, MinIO, RabbitMQ |
| **Alert Service** | `alert-service.yml` | MySQL, Redis, RabbitMQ |

### 2. 인프라 서비스
| 서비스 | 설정 파일 | 주요 설정 내용 |
|-------|----------|--------------|
| **Gateway Service** | `gateway-service.yml` | 라우팅 규칙, JWT 필터, Redis |
| **RabbitMQ** | `rabbitmq.yml` | 메시지 큐 연결 설정 |

### 3. 공통 설정
| 설정 파일 | 내용 |
|----------|------|
| **application.yml** | Eureka, 암호화 키, 공통 설정 |

---

## 🔧 핵심 기능

### 1. Native 설정 관리
```yaml
spring:
  profiles:
    active: native
  cloud:
    config:
      server:
        native:
          search-locations: classpath:/configurations
```

**특징:**
- **로컬 파일 기반**: Git 없이 클래스패스에서 설정 로드
- **즉시 반영**: 파일 변경 후 서버 재시작으로 설정 적용
- **단순한 구조**: 복잡한 Git 워크플로우 없이 직관적 관리

### 2. 설정 암호화
```yaml
security:
  encryption:
    password: Y5FJCdXFTP5behIfgrzg205PddmvR+Wun8JKSZMVGAc=
    salt: T7fZECWdaOfwyW5faUdxzg==
```

**암호화 대상:**
- **데이터베이스 비밀번호**: MySQL, Redis 패스워드
- **외부 API 키**: CoolSMS, OAuth2 클라이언트 시크릿
- **JWT 시크릿**: 토큰 서명 키
- **메시지 큐 인증**: RabbitMQ 계정 정보

### 3. 동적 포트 관리
```yaml
server:
  port: 0  # 모든 서비스가 동적 포트 사용
```

**Eureka 인스턴스 구분:**
```yaml
eureka:
  instance:
    instance-id: ${spring.cloud.client.ip-address}:${random.value}
```

### 4. 모니터링 설정 표준화
```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,prometheus
  metrics:
    export:
      prometheus:
        enabled: true
```

**모든 서비스에 공통 적용:**
- Prometheus 메트릭 수집
- Health Check 엔드포인트
- 애플리케이션 정보 노출

---

## 🌐 설정 배포 구조

### Config Server 설정
```yaml
server:
  port: 8888  # 고정 포트

spring:
  application:
    name: config-service
```

### 클라이언트 연동
각 마이크로서비스는 다음 설정으로 Config Server에 연결:
```yaml
spring:
  config:
    import: configserver:http://localhost:8888
```

### 설정 로드 순서
1. **Config Server 시작** (Port: 8888)
2. **각 서비스 시작** 시 Config Server에서 설정 로드
3. **Profile별 설정 병합**: `application.yml` + `{service-name}.yml`
4. **암호화된 값 복호화**: 자동으로 평문 변환

---

## 📊 주요 설정 카테고리

### 1. 데이터베이스 설정
```yaml
# 서비스별 DB 분리
datasource:
  url: jdbc:mysql://localhost:3306/{service}_db
  username: root
  password: [ENCRYPTED]
```

**DB 분리 전략:**
- `user_db`: 사용자 정보
- `health_db`: 건강 데이터
- `notification_db`: 알림 데이터

### 2. 외부 서비스 연동
| 서비스 | 용도 | 설정 위치 |
|-------|------|----------|
| **Redis** | 캐시, 세션 | 모든 서비스 |
| **Elasticsearch** | 식단 검색 | health-service |
| **MinIO** | 이미지 저장 | health-service |
| **RabbitMQ** | 메시지 큐 | health-service, alert-service |
| **CoolSMS** | SMS 인증 | user-service |

### 3. Gateway 라우팅 설정
```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: auth-service
          uri: lb://auth-service
          predicates:
            - Path=/api/auth/**
        # ... 기타 라우팅 규칙
```

**인증 필터 적용:**
- **인증 불요**: `/api/auth/**`, `/api/users/signup`
- **인증 필요**: 나머지 모든 API (`JwtAuthFilter` 적용)

---

## 🛡️ 보안 고려사항

### 설정 파일 보안
- **민감 정보 암호화**: DB 패스워드, API 키 등 암호화 저장
- **환경 변수 연동**: 운영 환경에서는 환경 변수로 암호화 키 관리
- **접근 제한**: Config Server 엔드포인트 접근 제한

### 네트워크 보안
- **Internal Network**: Config Server는 내부 네트워크에서만 접근
- **HTTPS 권장**: 운영 환경에서는 HTTPS 통신 적용
- **방화벽**: Config Server 포트(8888) 외부 접근 차단

### 설정 유출 방지
- **로깅 제외**: 민감한 설정값 로그 출력 금지
- **버전 관리**: 설정 파일을 Git에 올릴 때 민감 정보 제외
- **권한 관리**: 설정 파일 접근 권한 최소화

---

## 📋 모니터링 및 운영

### Actuator 엔드포인트
| 엔드포인트 | URL | 용도 |
|-----------|-----|------|
| **Health Check** | `/actuator/health` | 서비스 상태 확인 |
| **Environment** | `/actuator/env` | 현재 설정값 조회 |
| **Metrics** | `/actuator/prometheus` | Prometheus 메트릭 |
| **Info** | `/actuator/info` | 애플리케이션 정보 |

### 설정 변경 감지
```yaml
logging:
  level:
    org.springframework.cloud.config.server: INFO
```

**로그 모니터링:**
- 설정 파일 로드 성공/실패
- 암호화/복호화 과정
- 클라이언트 설정 요청 이력

### 설정 검증
- **시작 시 검증**: 필수 설정값 누락 체크
- **암호화 키 검증**: 복호화 가능 여부 확인
- **외부 서비스 연결**: DB, Redis 등 연결 상태 확인

---

## 🔄 확장 및 개선 방안

### Git 기반 설정 관리
현재 Native 모드에서 Git 기반으로 전환 시:
```yaml
spring:
  cloud:
    config:
      server:
        git:
          uri: https://github.com/your-org/config-repo
          search-paths: configurations
```

**Git 모드 장점:**
- 설정 변경 이력 추적
- 브랜치별 환경 관리
- 코드 리뷰를 통한 설정 검토

### 설정 자동 갱신
```yaml
management:
  endpoints:
    web:
      exposure:
        include: refresh
```

**Spring Cloud Bus 연동:**
- 설정 변경 시 모든 서비스에 자동 알림
- 서비스 재시작 없이 설정 적용

### 환경별 프로파일 관리
```
configurations/
├── application-dev.yml
├── application-staging.yml
├── application-prod.yml
└── {service-name}-{profile}.yml
```

---

## 🧪 테스트 및 검증

### Config Server 접근 테스트
```bash
# 전체 서비스 설정 조회
curl http://localhost:8888/application/default

# 특정 서비스 설정 조회  
curl http://localhost:8888/auth-service/default

# 암호화 테스트
curl -X POST http://localhost:8888/encrypt -d "test-password"

# 복호화 테스트
curl -X POST http://localhost:8888/decrypt -d "{cipher}AQA..."
```

### 설정 로드 검증
각 서비스 시작 로그에서 확인:
```
Located property source: [BootstrapPropertySource {name='bootstrapProperties-configClient'}]
Located property source: [BootstrapPropertySource {name='bootstrapProperties-auth-service.yml'}]
```

---

## 🔧 운영 가이드

### Config Server 시작 순서
1. **Config Server 먼저 시작** (Port: 8888)
2. **Eureka Server 시작** (Service Discovery)
3. **각 마이크로서비스 순차 시작**

### 설정 변경 절차
1. **설정 파일 수정** (`resources/configurations/` 폴더)
2. **Config Server 재시작** (변경사항 반영)
3. **대상 서비스 재시작** (새 설정 적용)

### 트러블슈팅
**설정 로드 실패 시:**
- Config Server 실행 상태 확인
- 네트워크 연결 상태 점검
- 설정 파일 문법 오류 확인
- 암호화 키 일치 여부 검증

---

## 📝 참고사항

### 현재 구조의 장단점

**장점:**
- **단순한 구조**: Native 모드로 복잡도 최소화
- **빠른 개발**: Git 워크플로우 없이 즉시 반영
- **중앙 관리**: 모든 설정을 한 곳에서 관리

**개선 필요사항:**
- **버전 관리**: Git 기반으로 변경 이력 추적
- **환경 분리**: 개발/스테이징/운영 환경별 프로파일
- **보안 강화**: 설정 파일 접근 권한 세분화
- **자동 갱신**: 설정 변경 시 자동 배포 기능

### 민감 정보 관리 주의사항
문서 작성 시 다음 정보들은 마스킹 처리:
- 데이터베이스 비밀번호
- API 키 (CoolSMS, OAuth2 등)
- JWT 시크릿 키
- 암호화 키 및 솔트값