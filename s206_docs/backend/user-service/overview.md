# User Service 개요

**디아비서(DiaViseo) 프로젝트의 사용자 관리 서비스**로, 회원가입, 프로필 관리, SMS 인증, 신체정보 관리를 담당하는 핵심 서비스입니다.

---

## 📌 서비스 개요

### 역할과 책임
- **회원 관리**: 사용자 가입, 프로필 조회/수정, 탈퇴 처리
- **SMS 인증**: CoolSMS 기반 휴대폰 번호 인증 시스템
- **신체정보 관리**: 키, 몸무게, 생일, 목표 등 건강 데이터 기록
- **Auth Service 연동**: 소셜 로그인 시 사용자 존재 여부 확인
- **FCM 토큰 관리**: 푸시 알림을 위한 Firebase 토큰 관리
- **알림 설정**: 사용자별 알림 허용/차단 설정

### MSA 구조에서의 위치
```
Android App → Gateway → User Service
                   ↕
           Auth Service (사용자 존재 확인)
                   ↕
          Health Service (신체정보 활용)
                   ↕
              MySQL (user_db)
                   ↕
        Redis (SMS 인증 코드 저장)
                   ↕
          CoolSMS API (SMS 발송)
```

User Service는 **사용자 데이터의 중심점**으로, 모든 개인정보를 안전하게 관리하고 다른 서비스에 필요한 사용자 정보를 제공합니다.

---

## ⚙️ 기술 스택 & 의존성

| 구분 | 기술 | 버전 | 용도 |
|-----|-----|-----|-----|
| **Framework** | Spring Boot | 3.4.5 | 기본 프레임워크 |
| **Database** | MySQL | 8.0 | 사용자 데이터 저장 |
| **ORM** | Spring Data JPA | - | 데이터베이스 접근 |
| **Cache** | Redis | - | SMS 인증 코드 저장 |
| **SMS** | CoolSMS SDK | 4.3.0 | SMS 인증 서비스 |
| **Security** | Spring Security | - | 기본 보안 설정 |
| **Validation** | Spring Validation | - | 입력값 검증 |
| **Common** | Common Module | - | 공통 예외처리, 응답 형식 |
| **Service Discovery** | Eureka Client | - | 마이크로서비스 등록 |
| **Config** | Spring Cloud Config | - | 중앙 설정 관리 |
| **Monitoring** | Micrometer Prometheus | - | 메트릭 수집 |

### 주요 의존성
- **Common Module**: ResponseDto, 예외처리, 암호화 기능
- **CoolSMS**: 실제 SMS 발송 (현재 실제 발송 활성화)
- **Redis**: SMS 인증 코드 임시 저장 (TTL 3분)
- **MySQL**: 사용자 정보 영구 저장

---

## 🔧 핵심 기능

### 1. 회원 관리 시스템
- **회员가입**: 소셜 로그인 후 상세 정보 등록
- **프로필 조회**: 사용자 상세 정보 조회 (`/api/users/me`)
- **프로필 수정**: 닉네임, 전화번호, 신체정보, 목표 등 수정
- **회원 탈퇴**: 논리적 삭제 (soft delete) 처리

### 2. SMS 인증 시스템
- **인증번호 발송**: CoolSMS API 기반 6자리 인증번호 발송
- **인증번호 검증**: Redis 저장된 코드와 비교 검증
- **실패 제한**: 5회 실패 시 3분간 차단
- **TTL 관리**: 인증번호 3분 후 자동 만료

### 3. 신체정보 관리
- **기본 정보**: 키, 몸무게, 생일, 성별
- **목표 설정**: 체중 감량/증량/유지
- **이력 관리**: 날짜별 신체정보 변화 추적
- **BMR/TDEE 계산**: 기초대사율 및 권장 칼로리 자동 계산

### 4. Auth Service 연동
- **사용자 존재 확인**: `/api/users/exist` - 소셜 로그인 시 기존 회원 여부 확인
- **Gateway 헤더 연동**: `X-USER-ID` 헤더로 사용자 식별

### 5. FCM 푸시 알림
- **토큰 등록**: 앱에서 받은 FCM 토큰 저장
- **알림 설정**: 사용자별 알림 허용/차단 관리
- **토큰 조회**: Alert Service에서 푸시 발송을 위한 토큰 제공

---

## 🏗️ 시스템 아키텍처

### User Service 내부 구조
```
┌─────────────────────────────────────────────────────────────┐
│                    User Service                             │
├─────────────────────────────────────────────────────────────┤
│  Controller Layer                                           │
│  ├── UserController (회원 관리)                             │
│  ├── UserVerificationController (SMS 인증)                 │
│  ├── UserPhysicalInfoController (신체정보)                 │
├─────────────────────────────────────────────────────────────┤
│  Service Layer                                              │
│  ├── UserService (핵심 비즈니스 로직)                        │
│  ├── UserVerificationService (SMS 인증 로직)               │
│  ├── UserPhysicalInfoService (신체정보 로직)               │
│  ├── SmsService (CoolSMS API 연동)                         │
├─────────────────────────────────────────────────────────────┤
│  Repository Layer                                           │
│  ├── UserRepository (사용자 데이터)                         │
│  ├── UserPhysicalInfoRepository (신체정보 데이터)          │
├─────────────────────────────────────────────────────────────┤
│  External Integration                                        │
│  ├── CoolSMS API (SMS 발송)                                │
│  ├── Redis (인증 코드 저장)                                │
│  ├── MySQL (영구 데이터 저장)                              │
└─────────────────────────────────────────────────────────────┘
```

### 데이터 저장소 구조
- **MySQL (user_db)**: 사용자 정보, 신체정보 영구 저장
- **Redis**: SMS 인증 코드 임시 저장 (TTL 3분)

---

## 🗂️ 데이터베이스 설계

### 1. User 엔티티 (user_tb)
| 필드 | 타입 | 제약조건 | 설명 |
|-----|------|----------|------|
| **userId** | Integer | PK, Auto Increment | 사용자 고유 ID |
| **name** | String(50) | NOT NULL | 실명 |
| **nickname** | String(50) | NOT NULL | 별명 |
| **gender** | Enum(M/F) | NOT NULL | 성별 |
| **goal** | Enum | NOT NULL | 목표 (WEIGHT_LOSS/GAIN/MAINTENANCE) |
| **height** | BigDecimal(6,2) | NOT NULL | 키 (cm) |
| **weight** | BigDecimal(6,2) | NOT NULL | 몸무게 (kg) |
| **birthday** | LocalDate | NOT NULL | 생년월일 |
| **phone** | String(20) | UNIQUE, NOT NULL | 전화번호 |
| **email** | String(50) | NOT NULL | 이메일 |
| **provider** | String(50) | NOT NULL | 소셜 로그인 제공자 |
| **consentPersonal** | Boolean | NOT NULL | 개인정보 처리 동의 |
| **locationPersonal** | Boolean | NOT NULL | 위치정보 처리 동의 |
| **notificationEnabled** | Boolean | DEFAULT true | 알림 허용 여부 |
| **fcmToken** | String | NULL | FCM 푸시 토큰 |
| **isDeleted** | Boolean | DEFAULT false | 삭제 여부 |
| **createdAt** | LocalDateTime | NOT NULL | 생성일시 |
| **updatedAt** | LocalDateTime | NOT NULL | 수정일시 |
| **deletedAt** | LocalDateTime | NULL | 삭제일시 |

### 2. UserPhysicalInfo 엔티티 (user_physical_info_tb)
| 필드 | 타입 | 제약조건 | 설명 |
|-----|------|----------|------|
| **userPhysicalInfoId** | Long | PK, Auto Increment | 신체정보 ID |
| **user** | User | FK, NOT NULL | 사용자 참조 |
| **height** | BigDecimal(6,2) | NOT NULL | 키 (cm) |
| **weight** | BigDecimal(6,2) | NOT NULL | 몸무게 (kg) |
| **birthday** | LocalDate | NOT NULL | 생년월일 |
| **goal** | Enum | NOT NULL | 목표 |
| **date** | LocalDate | NOT NULL | 기록 날짜 |
| **createdAt** | LocalDateTime | NOT NULL | 생성일시 |
| **updatedAt** | LocalDateTime | NOT NULL | 수정일시 |

**제약조건**: `UNIQUE(user_id, date)` - 사용자별 날짜당 하나의 기록만 허용

### 3. 데이터베이스 관계
```
User (1) ────────── (N) UserPhysicalInfo
    │
    └── userId로 연결
```

---

## 🔄 주요 비즈니스 플로우

### 1. 회원가입 플로우
```
1. Android App → Auth Service: OAuth 소셜 로그인
2. Auth Service → User Service: GET /api/users/exist (이메일 확인)
3. User Service → Auth Service: { "exists": false } (신규 회원)
4. Auth Service → Android App: { "isNewUser": true }
5. Android App → Gateway: POST /api/users/verify/phone (SMS 인증)
6. User Service → CoolSMS API: SMS 발송
7. Android App → Gateway: POST /api/users/verify/phone/confirm (코드 검증)
8. Android App → Gateway: POST /api/users/signup (회원가입)
9. User Service: User 엔티티 생성 + 신체정보 저장
10. Android App ← Gateway: 회원가입 완료
```

### 2. SMS 인증 플로우
```
1. 사용자 → User Service: POST /api/users/verify/phone
2. UserVerificationService:
   - 기존 전화번호 중복 체크
   - 6자리 랜덤 코드 생성
   - Redis 코드 저장 (TTL 3분)
   - CoolSMS API 호출
3. CoolSMS → 사용자 휴대폰: "[디아비서] 인증번호는 [123456] 입니다."
4. 사용자 → User Service: POST /api/users/verify/phone/confirm
5. UserVerificationService:
   - Redis에서 코드 조회
   - 입력 코드와 비교
   - 성공 시 Redis 코드 삭제
   - 실패 시 실패 횟수 증가 (5회 초과 시 3분 차단)
```

### 3. 신체정보 관리 플로우
```
1. 회원가입 또는 프로필 수정 시:
   - UserService.saveIfRelevantInfoChanged() 호출
   - UserPhysicalInfoService.saveOrUpdate() 실행
   - 당일 기록 존재 시 업데이트
   - 기록 없으면 새로 생성

2. 신체정보 조회 시:
   - 특정 날짜 이전 최신 기록 조회
   - BMR/TDEE 계산 (성별, 나이 고려)
   - 목표에 따른 권장 칼로리/운동량 계산
```

### 4. Auth Service 연동 플로우
```
1. 소셜 로그인 시:
   Auth Service → User Service: GET /api/users/exist?email=xxx&provider=google
   
2. User Service 응답:
   - 기존 회원: { "userId": 123, "name": "홍길동", "exists": true }
   - 신규 회원: { "userId": null, "name": null, "exists": false }
   
3. Gateway 인증 후:
   Gateway → User Service: GET /api/users/me (X-USER-ID 헤더 포함)
```

---

## 🔐 SMS 인증 시스템 상세

### CoolSMS 연동 설정
```yaml
coolsms:
  api-key: NCSJTLVYLEHGKS7D
  api-secret: NSHGJP6JGLYJCMK8GJXFKNXJNFGD6KON
  from-number: 01028992564
```

### 인증 프로세스
1. **중복 체크**: 이미 가입된 전화번호 확인
2. **제한 확인**: 진행 중인 인증 요청 차단
3. **코드 생성**: 100,000~999,999 범위 6자리 랜덤
4. **Redis 저장**: `phone:{전화번호}` 키로 3분 TTL
5. **SMS 발송**: CoolSMS API 호출
6. **실패 처리**: 전송 실패 시 Redis 정리

### 보안 기능
- **재전송 방지**: 기존 코드가 있으면 새 요청 차단
- **실패 제한**: 5회 실패 시 `phone:fail:{전화번호}` 키로 3분 차단
- **자동 만료**: 인증번호 3분 후 자동 삭제
- **성공 시 정리**: 인증 성공 시 코드 및 실패 기록 모두 삭제

### Redis 데이터 구조
```
# 인증번호 저장
Key: phone:01012345678
Value: "123456"
TTL: 180초 (3분)

# 실패 횟수 저장
Key: phone:fail:01012345678
Value: "3"
TTL: 180초 (3분)
```

---

## 📊 신체정보 계산 로직

### BMR (기초대사율) 계산
```java
// Mifflin-St Jeor 방정식 사용
double bmr = 10 * weight + 6.25 * height - 5 * age + (남성 ? 5 : -161);
```

### TDEE (총 에너지 소비량) 계산
```java
// 활동량 계수 1.375 (가벼운 활동) 적용
int tdee = Math.round(bmr * 1.375);
```

### 목표별 권장 칼로리
| 목표 | 권장 칼로리 | 권장 운동량 |
|-----|------------|------------|
| **체중 감량** | TDEE - 400 | TDEE * 0.15 + 150 |
| **체중 증량** | TDEE + 250 | TDEE * 0.15 - 70 |
| **체중 유지** | TDEE | TDEE * 0.15 |

---

## 🛡️ 보안 고려사항

### 개인정보 보호
- **Soft Delete**: 회원 탈퇴 시 물리적 삭제 대신 isDeleted 플래그 사용
- **민감정보 로깅**: 전화번호, 이메일 등 개인정보 로그 출력 최소화
- **입력값 검증**: Validation 어노테이션으로 잘못된 데이터 차단

### SMS 보안
- **API 키 보호**: 설정 파일에서 환경변수로 관리 (현재는 하드코딩)
- **발송 제한**: 동일 번호 재전송 방지 및 실패 횟수 제한
- **TTL 적용**: 인증번호 자동 만료로 무효화

### 데이터베이스 보안
- **유니크 제약**: 전화번호 중복 방지
- **NOT NULL 제약**: 필수 필드 누락 방지
- **타입 안전성**: Enum 사용으로 잘못된 값 입력 차단

---

## ⚡ 성능 최적화

### 데이터베이스 최적화
- **인덱스**: userId, email+provider 조합에 자동 인덱스
- **페이징**: 대용량 데이터 조회 시 페이징 적용 가능
- **쿼리 최적화**: @Query 어노테이션으로 필요한 필드만 조회

### Redis 최적화
- **TTL 활용**: 불필요한 데이터 자동 정리
- **키 네이밍**: 명확한 키 구조로 관리 효율성 향상
- **메모리 절약**: String 값만 저장하여 메모리 사용량 최소화

### 트랜잭션 최적화
- **@Transactional**: 필요한 메서드에만 적용
- **readOnly**: 조회 전용 메서드에 readOnly 설정
- **롤백**: SMS 전송 실패 시 Redis 정리로 일관성 유지

---

## 📋 모니터링 및 운영

### Actuator 엔드포인트
| 엔드포인트 | URL | 용도 |
|-----------|-----|------|
| **Health Check** | `/actuator/health` | 서비스 상태 확인 |
| **Metrics** | `/actuator/prometheus` | Prometheus 메트릭 |
| **Info** | `/actuator/info` | 애플리케이션 정보 |

### 주요 메트릭
- **회원가입 수**: 일별/월별 신규 가입자 추이
- **SMS 발송률**: 인증번호 발송 성공/실패 비율
- **인증 성공률**: SMS 인증 성공/실패 비율
- **API 응답시간**: 각 엔드포인트별 처리 시간

### 로깅 전략
```java
// 중요 이벤트 로깅
log.info("Creating user: {}", userCreateRequest.toString());
log.info("인증번호 [{}]가 Redis에 저장됨. Key = {}", code, key);
log.info("인증 성공 - phone: {}", phone);

// 에러 상황 로깅
log.error("CoolSMS 요청 실패: {}", e.getMessage(), e);
```

---

## 🚀 확장 및 개선 방안

### 보안 강화
**SMS API 키 보안**
```yaml
# 현재 (개선 필요)
coolsms:
  api-key: NCSJTLVYLEHGKS7D  # 하드코딩

# 개선안
coolsms:
  api-key: ${COOLSMS_API_KEY}  # 환경변수
```

**개인정보 암호화**
```java
// 향후 구현 예정
@Column(nullable = false)
private String encryptedPhone;  // 전화번호 암호화 저장

@Column(nullable = false) 
private String encryptedEmail;  // 이메일 암호화 저장
```

### 성능 최적화
**캐싱 도입**
```java
@Cacheable(value = "users", key = "#userId")
public UserDetailResponse getUserByUserId(Integer userId) {
    // 자주 조회되는 사용자 정보 캐싱
}
```

**비동기 처리**
```java
@Async
public CompletableFuture<Void> sendSmsAsync(String phone, String code) {
    // SMS 발송을 비동기로 처리하여 응답시간 개선
}
```

### 기능 확장
**소셜 로그인 확장**
- Kakao, Naver, Apple 로그인 지원
- provider 필드 활용하여 다중 소셜 로그인 관리

**알림 시스템 고도화**
- 푸시 알림 스케줄링
- 사용자별 알림 선호도 세분화
- 이메일 알림 추가

---

## 🧪 테스트 가이드

### 회원가입 테스트
```bash
# 1. SMS 인증번호 요청
curl -X POST http://localhost:8080/api/users/verify/phone \
  -H "Content-Type: application/json" \
  -d '{"phone":"01012345678"}'

# 2. SMS 인증번호 확인
curl -X POST http://localhost:8080/api/users/verify/phone/confirm \
  -H "Content-Type: application/json" \
  -d '{"phone":"01012345678","code":"123456"}'

# 3. 회원가입
curl -X POST http://localhost:8080/api/users/signup \
  -H "Content-Type: application/json" \
  -d '{
    "name":"홍길동",
    "nickname":"길동이",
    "gender":"M",
    "goal":"WEIGHT_LOSS",
    "height":175.5,
    "weight":70.0,
    "birthday":"1990-01-01",
    "phone":"01012345678",
    "email":"test@example.com",
    "provider":"google",
    "consentPersonal":true,
    "locationPersonal":true
  }'
```

### 인증된 API 테스트
```bash
# 프로필 조회 (Gateway 인증 필요)
curl -X GET http://localhost:8080/api/users/me \
  -H "Authorization: Bearer {valid-jwt-token}"

# 프로필 수정
curl -X PUT http://localhost:8080/api/users \
  -H "Authorization: Bearer {valid-jwt-token}" \
  -H "Content-Type: application/json" \
  -d '{"nickname":"새별명","weight":69.5}'
```

### Auth Service 연동 테스트
```bash
# 사용자 존재 확인
curl -X GET "http://localhost:8080/api/users/exist?email=test@example.com&provider=google"
```

---

## 📝 참고사항

### 현재 구조의 장단점

**장점:**
- **완전한 SMS 인증**: 실제 CoolSMS API 연동으로 프로덕션 준비 완료
- **신체정보 이력 관리**: 날짜별 변화 추적으로 건강 관리 지원
- **Auth Service 연동**: 소셜 로그인과 완벽한 협력
- **FCM 토큰 관리**: 푸시 알림 인프라 구축 완료
- **입력값 검증**: Validation을 통한 안전한 데이터 처리

**개선 필요사항:**

### 🔧 보안 개선 필요
1. **SMS API 키 하드코딩**
   ```yaml
   # 현재 상태 (보안 위험)
   coolsms:
     api-key: NCSJTLVYLEHGKS7D
     api-secret: NSHGJP6JGLYJCMK8GJXFKNXJNFGD6KON
     from-number: 01028992564
   ```
   **개선 방안**: 환경변수 또는 Config Server 암호화 적용

2. **개인정보 암호화 미적용**
   - 전화번호, 이메일 평문 저장
   - Common Module의 CryptoUtils 활용 필요

### 🚀 성능 개선 필요
1. **SMS 발송 동기 처리**
   - 현재 SMS 발송이 API 응답을 블로킹
   - 비동기 처리로 응답시간 개선 필요

2. **사용자 조회 캐싱 부재**
   - 자주 조회되는 사용자 정보 캐싱 미적용
   - Redis 기반 캐싱 도입 고려

### 📊 기능 확장 가능성
1. **다중 소셜 로그인 지원**
   - 현재 Google만 지원
   - Kakao, Naver, Apple 추가 가능

2. **알림 시스템 고도화**
   - 현재 단순 on/off 설정
   - 알림 유형별 세분화 필요

### 🗃️ 데이터 관리
1. **Soft Delete 정책**
   - 탈퇴 회원 데이터 영구 보관
   - GDPR 준수를 위한 완전 삭제 정책 필요

2. **신체정보 데이터 증가**
   - 사용자 증가 시 신체정보 테이블 부하 예상
   - 파티셔닝 또는 아카이빙 전략 필요

---

## 🔄 연동 서비스 정보

### Auth Service 연동
- **엔드포인트**: `GET /api/users/exist`
- **목적**: 소셜 로그인 시 기존 회원 여부 확인
- **응답**: UserExistResponse (userId, name, exists)

### Gateway 연동
- **헤더**: `X-USER-ID` - JWT 토큰에서 추출한 사용자 ID
- **인증 불필요**: `/api/users/signup`, `/api/users/verify/**`
- **인증 필요**: 나머지 모든 사용자 API

### Health Service 연동
- **신체정보 제공**: UserPhysicalInfo 엔티티 데이터 활용
- **BMR/TDEE 계산**: Health Service에서 칼로리 계산 시 참조

### Alert Service 연동
- **FCM 토큰 제공**: 푸시 알림 발송을 위한 토큰 조회
- **알림 허용 사용자**: 알림 설정된 사용자 목록 제공