# Eureka Service 개요

**디아비서(DiaViseo) 프로젝트의 서비스 디스커버리 서버**로, 모든 마이크로서비스의 등록, 발견, 상태 관리를 담당합니다.

---

## 📌 서비스 개요

### 역할과 책임
- **서비스 등록**: 마이크로서비스들의 인스턴스 정보 등록 관리
- **서비스 발견**: 클라이언트가 필요한 서비스 위치 정보 제공
- **Health Check**: 등록된 서비스들의 생존 상태 모니터링
- **Load Balancing 지원**: 같은 서비스의 여러 인스턴스 간 로드 밸런싱
- **장애 복구**: 네트워크 장애 시 서비스 레지스트리 보호

### MSA 구조에서의 중심 역할
```
                    Eureka Server (Port: 8761)
                           ↑ ↓
        ┌─────────────────────────────────────────────┐
        │              Service Registry                │
        │  ┌─────────────────────────────────────────┐ │
        │  │ auth-service    : 192.168.1.10:35001   │ │
        │  │ user-service    : 192.168.1.10:35002   │ │
        │  │ health-service  : 192.168.1.10:35003   │ │
        │  │ gateway-service : 192.168.1.10:8080    │ │
        │  │ alert-service   : 192.168.1.10:35004   │ │
        │  └─────────────────────────────────────────┘ │
        └─────────────────────────────────────────────┘
                           ↑ ↓
              All Microservices (Eureka Clients)
```

---

## ⚙️ 기술 스택 & 의존성

| 구분 | 기술 | 버전 | 용도 |
|-----|-----|-----|-----|
| **Framework** | Spring Boot | 3.4.5 | 기본 프레임워크 |
| **Service Discovery** | Netflix Eureka Server | 2024.0.1 | 서비스 레지스트리 |
| **Monitoring** | Spring Actuator | - | 서버 상태 모니터링 |
| **Metrics** | Micrometer Prometheus | - | 메트릭 수집 |

### 핵심 의존성
```gradle
implementation 'org.springframework.cloud:spring-cloud-starter-netflix-eureka-server'
implementation 'org.springframework.boot:spring-boot-starter-actuator'
implementation 'io.micrometer:micrometer-registry-prometheus'
```

---

## 🔧 핵심 설정

### 1. Eureka Server 설정
```yaml
spring:
  application:
    name: eureka-service

server:
  port: 8761  # Eureka 표준 포트

eureka:
  client:
    register-with-eureka: false    # 자기 자신을 등록하지 않음
    fetch-registry: false          # 레지스트리를 로컬에 캐싱하지 않음
    service-url:
      defaultZone: http://localhost:8761/eureka
```

**설정 의미:**
- **register-with-eureka: false**: Eureka Server 자체는 클라이언트로 등록되지 않음
- **fetch-registry: false**: 다른 Eureka Server에서 정보를 가져오지 않음 (Single Node)
- **defaultZone**: 클라이언트들이 접속할 URL

### 2. 서비스 보호 및 복구 설정
```yaml
eureka:
  server:
    eviction-interval-timer-in-ms: 30000      # 30초마다 죽은 서비스 정리
    enable-self-preservation: true           # 자기 보호 모드 활성화
```

**Self-Preservation 모드:**
- **활성화 조건**: 80-85% 이상의 서비스가 heartbeat를 보내지 않을 때
- **보호 동작**: 네트워크 장애로 판단하여 서비스 제거를 중단
- **복구 목적**: 네트워크 문제 해결 후 시스템 정상화 지원

### 3. 로깅 설정
```yaml
logging:
  level:
    com.netflix.eureka: INFO      # Eureka 관련 로그
    com.netflix.discovery: INFO   # 서비스 디스커버리 로그
```

---

## 🌐 서비스 등록 및 발견 과정

### 1. 서비스 등록 (Registration)
```
1. Microservice 시작
2. Eureka Client가 Eureka Server에 자신의 정보 등록
   - 서비스명, IP, 포트, 상태 정보
3. 등록 성공 시 Service Registry에 추가
```

### 2. Health Check (Heartbeat)
```
1. 등록된 서비스는 30초마다 heartbeat 전송
2. Eureka Server는 heartbeat 수신하여 서비스 생존 확인
3. heartbeat 없으면 해당 서비스를 비활성 상태로 마킹
```

### 3. 서비스 발견 (Discovery)
```
1. 클라이언트가 특정 서비스 호출 필요
2. Eureka Server에서 해당 서비스의 인스턴스 목록 조회
3. Load Balancer가 사용 가능한 인스턴스 중 하나 선택
4. 선택된 인스턴스로 요청 전송
```

### 4. 서비스 제거 (Eviction)
```
1. 30초(eviction-interval-timer) 주기로 비활성 서비스 정리
2. Self-Preservation 모드가 비활성화된 경우에만 제거
3. 제거된 서비스는 Service Registry에서 삭제
```

---

## 📊 등록된 서비스 현황

### 비즈니스 서비스
| 서비스명 | 포트 | 인스턴스 ID 패턴 | 상태 확인 |
|---------|------|-----------------|----------|
| **auth-service** | 동적 | `{ip}:random` | `/actuator/health` |
| **user-service** | 동적 | `{ip}:random` | `/actuator/health` |
| **health-service** | 동적 | `{ip}:random` | `/actuator/health` |
| **alert-service** | 동적 | `{ip}:random` | `/actuator/health` |

### 인프라 서비스
| 서비스명 | 포트 | 인스턴스 ID 패턴 | 상태 확인 |
|---------|------|-----------------|----------|
| **gateway-service** | 8080 | `{ip}:random` | `/actuator/health` |

### 인스턴스 ID 생성 규칙
```yaml
eureka:
  instance:
    instance-id: ${spring.cloud.client.ip-address}:${random.value}
```

**특징:**
- **IP 기반**: 서비스가 실행되는 서버의 IP 주소 사용
- **랜덤 값**: 같은 서버에서 여러 인스턴스 실행 시 구분
- **동적 포트**: `server.port: 0`으로 자동 할당된 포트 사용

---

## 🛡️ 장애 복구 및 보호 메커니즘

### Self-Preservation 모드
```yaml
eureka:
  server:
    enable-self-preservation: true
```

**작동 원리:**
1. **정상 상황**: 모든 서비스가 정기적으로 heartbeat 전송
2. **네트워크 장애**: 대부분의 서비스에서 heartbeat 중단
3. **자기 보호 활성화**: 80% 이상 서비스 장애 시 보호 모드 진입
4. **서비스 보존**: 네트워크 복구까지 서비스 레지스트리 유지
5. **정상 복구**: 네트워크 복구 후 정상 운영 재개

### Eviction 정책
```yaml
eureka:
  server:
    eviction-interval-timer-in-ms: 30000  # 30초 주기
```

**균형 전략:**
- **적극적 정리**: 30초마다 죽은 서비스 제거
- **보수적 보호**: Self-Preservation 모드로 대량 장애 상황 대응
- **네트워크 장애 vs 서비스 장애** 구분하여 적절한 대응

---

## 🔍 모니터링 및 관리

### Eureka Dashboard
```
URL: http://localhost:8761
```

**제공 정보:**
- **등록된 서비스 목록**: 서비스명, 인스턴스 수, 상태
- **인스턴스 상세**: IP, 포트, 상태, 마지막 heartbeat 시간
- **General Info**: 환경 정보, 데이터센터 정보
- **Instance Info**: 인스턴스별 메타데이터

### Health Check 정보
각 서비스의 Health Check URL:
```
http://{service-ip}:{service-port}/actuator/health
```

**Health Check 응답 예시:**
```json
{
  "status": "UP",
  "components": {
    "db": { "status": "UP" },
    "redis": { "status": "UP" },
    "eureka": { "status": "UP" }
  }
}
```

### 로그 모니터링
**중요 로그 패턴:**
```
# 서비스 등록
Registered instance AUTH-SERVICE/192.168.1.10:auth-service:35001

# Heartbeat 수신
Renewed lease for AUTH-SERVICE/192.168.1.10:auth-service:35001

# 서비스 제거
Cancelled instance AUTH-SERVICE/192.168.1.10:auth-service:35001

# Self-Preservation 활성화
THE SELF PRESERVATION MODE IS TURNED ON
```

---

## 🚀 성능 및 확장성

### 현재 구성 (Single Node)
```
단일 Eureka Server
├── 장점: 단순한 구조, 빠른 응답
└── 단점: 단일 장애점 (SPOF)
```

### 고가용성 확장 방안
```yaml
# eureka-server-1 (Primary)
eureka:
  client:
    service-url:
      defaultZone: http://eureka-server-2:8762/eureka

# eureka-server-2 (Secondary)  
eureka:
  client:
    service-url:
      defaultZone: http://eureka-server-1:8761/eureka
```

**Multi-Node 장점:**
- **장애 허용**: 한 대가 다운되어도 서비스 계속 운영
- **부하 분산**: 등록/조회 요청 분산 처리
- **데이터 동기화**: Eureka Server 간 레지스트리 동기화

---

## 📋 운영 가이드

### 서비스 시작 순서
```
1. Eureka Server 시작 (Port: 8761)
2. Config Server 시작 (Eureka에 등록되지 않음)
3. Gateway Service 시작 (Eureka Client)
4. 각 비즈니스 서비스 시작 (Eureka Client)
```

**중요:** Eureka Server가 먼저 실행되어야 다른 서비스들이 정상 등록됩니다.

### 서비스 상태 확인
```bash
# Eureka Dashboard 접근
curl http://localhost:8761

# 등록된 서비스 목록 API
curl http://localhost:8761/eureka/apps

# 특정 서비스 정보
curl http://localhost:8761/eureka/apps/AUTH-SERVICE

# Eureka Server 상태
curl http://localhost:8761/actuator/health
```

### 트러블슈팅

**서비스 등록 실패:**
1. **Eureka Server 실행 확인**: `http://localhost:8761` 접근 가능 여부
2. **네트워크 연결**: 클라이언트에서 Eureka Server 접근 가능 여부
3. **설정 확인**: `defaultZone` URL 정확성 검증

**서비스 발견 실패:**
1. **Service Registry 확인**: 대상 서비스가 등록되어 있는지 확인
2. **Load Balancer 설정**: `@LoadBalanced` 어노테이션 적용 여부
3. **서비스명 일치**: 호출하는 서비스명과 등록된 서비스명 일치 여부

**Self-Preservation 모드:**
```
THE SELF PRESERVATION MODE IS TURNED ON. REPLICATION MAY BE IMPACTED.
```
- **원인**: 대부분의 서비스에서 heartbeat 중단
- **대응**: 네트워크 연결 상태 점검, 서비스 상태 확인
- **해제**: 네트워크 복구 시 자동으로 정상 모드로 전환

---

## 🔧 설정 최적화

### 개발 환경 설정
```yaml
eureka:
  server:
    eviction-interval-timer-in-ms: 10000    # 빠른 정리 (10초)
    enable-self-preservation: false         # 개발 시 비활성화
  client:
    registry-fetch-interval-seconds: 5      # 빠른 갱신
```

### 운영 환경 설정
```yaml
eureka:
  server:
    eviction-interval-timer-in-ms: 60000    # 안정적 정리 (60초)
    enable-self-preservation: true          # 필수 활성화
  client:
    registry-fetch-interval-seconds: 30     # 안정적 갱신
```

---

## 📊 메트릭 및 알람

### Prometheus 메트릭
```
# 등록된 서비스 수
eureka_server_registry_size

# 실행 중인 인스턴스 수  
eureka_server_instances_up

# Self-Preservation 상태
eureka_server_self_preservation_enabled
```

### 모니터링 대상
- **Service Registry 크기**: 등록된 서비스 수 모니터링
- **Heartbeat 실패율**: 서비스 Health 상태 추적
- **Self-Preservation 활성화**: 네트워크 장애 감지
- **응답 시간**: Eureka Server 성능 모니터링

---

## 🔄 향후 개선 방안

### 1. 고가용성 구성
- **Multi-Zone 배포**: 여러 가용 영역에 Eureka Server 배치
- **로드 밸런서**: Eureka Server 앞단에 로드 밸런서 구성

### 2. 보안 강화
- **인증/인가**: Eureka Server 접근 제어
- **HTTPS**: 암호화된 통신 적용
- **방화벽**: 내부 네트워크에서만 접근 허용

### 3. 성능 최적화
- **캐싱 전략**: 클라이언트 측 레지스트리 캐싱 최적화
- **네트워크 최적화**: Heartbeat 주기 및 timeout 튜닝
- **메모리 관리**: 대규모 서비스 등록 시 메모리 사용량 최적화

---

## 📝 참고사항

### Netflix Eureka 특징
- **AP (Availability & Partition tolerance)**: CAP 이론에서 가용성과 분할 내성 우선
- **Eventually Consistent**: 최종 일관성 보장
- **Self-Healing**: 네트워크 장애 복구 시 자동으로 정상화

### 대안 기술 비교
| 기술 | 장점 | 단점 |
|-----|------|------|
| **Eureka** | 간단한 설정, Self-Preservation | Netflix에서 개발 중단 |
| **Consul** | 강력한 기능, 보안 강화 | 복잡한 설정 |
| **Zookeeper** | 강한 일관성 | 운영 복잡도 높음 |

현재 프로젝트에서는 **단순함과 안정성**을 위해 Eureka를 선택했습니다.