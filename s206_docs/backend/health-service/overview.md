# Health Service - 통합 개요

**디아비서(DiaViseo) Health Service는 사용자의 종합적인 건강 관리를 위한 MSA 기반 핵심 서비스입니다.**

체성분 관리, 알레르기/질환 정보, 운동 기록, 영양 분석, 걸음 수 추적 등 5개 주요 도메인과 6개 지원 모듈을 통해 개인 맞춤형 건강 솔루션을 제공합니다.

---

## 📌 서비스 개요

### Health Service의 역할
Health Service는 **개인 건강 데이터의 중앙 집중식 관리 허브**로서, 사용자의 건강 여정 전반을 디지털화하고 분석하여 개인화된 인사이트를 제공합니다.

- **통합 건강 데이터 관리**: 체성분, 식단, 운동, 걸음 수 등 모든 건강 지표 통합 관리
- **개인화된 분석**: 사용자별 건강 상태 기반 맞춤형 권장사항 제공
- **실시간 모니터링**: 일일/주간/월간 건강 패턴 추적 및 트렌드 분석
- **스마트 알림 시스템**: 사용자 행동 패턴 기반 개인화된 건강 관리 알림
- **안전한 데이터 보호**: 민감한 건강 정보의 암호화 저장 및 권한 관리

### 핵심 가치 제안
1. **One-Stop 건강 플랫폼**: 분산된 건강 정보를 하나의 서비스에서 통합 관리
2. **데이터 기반 인사이트**: 개인 건강 패턴 분석을 통한 맞춤형 건강 가이드
3. **편리한 기록 방식**: OCR, 이미지 업로드, 자동 계산 등 사용자 친화적 입력
4. **지속적인 동기부여**: 스마트 알림과 통계를 통한 건강 관리 습관 형성

---

## 🏗️ 전체 시스템 아키텍처

### MSA 구조에서의 위치
```
┌─────────────────────────────────────────────────────────────┐
│                    DiaViseo Ecosystem                      │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  Android App ──────────► Gateway Service                   │
│                              │                             │
│                              ├─── User Service            │
│                              ├─── Alert Service           │
│                              └─── Health Service ◄────────┤
│                                        │                   │
│                              ┌─────────▼─────────┐        │
│                              │   Health Service   │        │
│                              │                   │        │
│                              │ ┌─ BodyInfo      │        │
│                              │ ├─ Condition     │        │
│                              │ ├─ Exercise      │ ◄──────┤
│                              │ ├─ Nutrition     │        │
│                              │ └─ Step          │        │
│                              │                   │        │
│                              │ ┌─ Client        │        │
│                              │ ├─ Config        │        │
│                              │ ├─ Elastic       │        │
│                              │ ├─ Persistence   │        │
│                              │ └─ Notification  │        │
│                              └───────────────────┘        │
└─────────────────────────────────────────────────────────────┘
```

### 데이터 저장소 통합 구조
```
┌─────────────────────────────────────────────────────────────┐
│                  Health Service Data Layer                  │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  ┌─────────────┐    ┌─────────────┐    ┌─────────────┐    │
│  │   MySQL     │    │Elasticsearch│    │   MinIO     │    │
│  │ (health_db) │    │             │    │             │    │
│  │             │    │             │    │             │    │
│  │ • 체성분     │    │ • 음식검색   │    │ • 식단이미지 │    │
│  │ • 알레르기   │    │ • 한글분석   │    │ • OCR이미지  │    │
│  │ • 질환정보   │    │ • 자동동기화 │    │ • 썸네일     │    │
│  │ • 운동기록   │    │             │    │             │    │
│  │ • 식단데이터 │    │             │    │             │    │
│  │ • 걸음수     │    │             │    │             │    │
│  └─────────────┘    └─────────────┘    └─────────────┘    │
│                                                             │
│  ┌─────────────────────────────────────────────────────────┤
│  │              Message Queue (RabbitMQ)                   │
│  │                                                         │
│  │  Health Service ──► alert-exchange ──► Alert Service   │
│  │  (알림 요청)                            (푸시 발송)     │
│  └─────────────────────────────────────────────────────────┘
└─────────────────────────────────────────────────────────────┘
```

---

## 🔧 주요 도메인 모듈

### 1. BodyInfo 모듈
**체성분 정보 관리 및 OCR 자동 입력 시스템**

**핵심 기능**:
- **OCR 기반 자동 입력**: Tesseract 엔진으로 인바디 결과지 자동 인식
- **정확한 지수 계산**: BMI, BMR 해리스-베네딕트 공식 기반 계산
- **변화 추적**: 일별/주별/월별 체성분 변화 통계 및 트렌드 분석
- **User Service 연동**: 신체정보 변경 시 자동 동기화

**기술적 특징**:
- Tesseract OCR 엔진 + 비동기 처리 (30초 타임아웃)
- BigDecimal 정밀 계산 + 입력값 범위 검증
- Soft Delete 기반 안전한 데이터 관리

### 2. Condition 모듈  
**개인 건강 상태 기반 맞춤형 서비스 제공**

**핵심 기능**:
- **알레르기 관리**: 식약처 기준 26개 주요 알레르기 토글 방식 등록
- **질환 관리**: KCD-8 기준 40개 질환을 9개 카테고리로 분류 관리
- **서비스 연동**: Nutrition/Exercise Service에 개인 건강 정보 제공
- **의료 표준 준수**: 공식 의료 분류 체계 기반 정확한 기준 데이터

**기술적 특징**:
- 복합 유니크 제약으로 중복 등록 방지
- ApplicationReadyEvent 기반 기준 데이터 자동 로딩
- 토글 방식 직관적 UI/UX 설계

### 3. Exercise 모듈
**운동 기록 관리 및 통계 분석 시스템**

**핵심 기능**:
- **운동 기록 관리**: 일자별 운동 시간, 칼로리, 횟수 기록
- **운동 유형/카테고리**: 구조화된 운동 분류 및 즐겨찾기 기능
- **통계 분석**: 일별/주별/월별 누적 칼로리, 평균값 제공
- **알림 연동**: RabbitMQ 기반 운동 알림 시스템

**기술적 특징**:
- 3계층 운동 데이터 구조 (Exercise ↔ ExerciseType ↔ ExerciseCategory)
- 즐겨찾기 배치 쿼리 최적화
- ExerciseNotificationPublisher를 통한 비동기 알림

### 4. Nutrition 모듈
**종합적인 영양 관리 및 식단 분석 플랫폼**

**핵심 기능**:
- **음식 정보 관리**: 10,000+ 음식 데이터베이스 기반 영양소 정보
- **개인화된 식단 관리**: 즐겨찾기, 음식 세트, 이미지 기반 식사 기록
- **실시간 영양 분석**: 일일/주간/월간 영양소 섭취량 계산
- **시각적 식단 기록**: MinIO 기반 식사 이미지 업로드 및 관리

**기술적 특징**:
- 3계층 식단 구조 (Meal → MealTime → MealFood)
- MinIO 객체 스토리지 + UUID 기반 이미지 관리
- 복잡한 MySQL 집계 쿼리 + 대체 로직 구현

### 5. Step 모듈
**걸음 수 추적 및 활동량 모니터링**

**핵심 기능**:
- **일일 걸음 수 기록**: 날짜별 걸음 수 등록 및 자동 업데이트
- **주간 통계 분석**: 7일간 걸음 수 패턴 및 평균 계산
- **유연한 데이터 관리**: 배치 등록, 특정 날짜 조회, 전체 기록 조회
- **빈 데이터 처리**: 기록 없는 날짜는 0값으로 기본 응답

**기술적 특징**:
- 날짜 기반 유니크 제약으로 중복 방지
- WeekFields 활용한 정확한 주간 계산
- Upsert 패턴으로 등록/수정 통합 처리

---

## 🛠️ 지원 모듈 시스템

### 1. Client 모듈
**외부 서비스 연동 및 통신 관리**

**핵심 기능**:
- **User Service 연동**: OpenFeign 기반 사용자 정보 조회 및 체성분 동기화
- **타입 안전성**: 강타입 DTO 기반 서비스 간 통신
- **장애 처리**: Feign 기본 재시도 및 타임아웃 관리

**연동 API**:
- `getUserByUserId()`: BMI/BMR 계산용 사용자 정보 조회
- `updateBodyComposition()`: 체성분 변경 시 User Service 동기화
- `getUsersWithNotificationsEnabled()`: 알림 대상 사용자 목록 조회

### 2. Config 모듈
**시스템 설정 및 인프라 구성 통합 관리**

**핵심 구성요소**:
- **AsyncConfig**: OCR 처리용 전용 스레드풀 (ThreadPoolTaskExecutor)
- **MinioConfig**: 이미지 스토리지 설정 + SSL 검증 비활성화
- **RabbitConfig**: JSON 메시지 컨버터 + alert-exchange 설정
- **SecurityConfig**: Stateless 보안 설정 + API 경로 권한 관리
- **ElasticsearchConfig**: 음식 검색용 Elasticsearch 클라이언트 설정

**기술적 특징**:
- 환경별 설정 분리 (Config Server 연동)
- SSL 우회 설정으로 개발환경 편의성 제공
- 마이크로서비스 간 통신 최적화

### 3. Elastic 모듈
**고성능 음식 검색 및 데이터 동기화 시스템**

**핵심 기능**:
- **한글 음식 검색**: Nori 분석기 기반 유연한 한글 검색
- **자동 데이터 동기화**: MySQL → Elasticsearch 실시간 동기화
- **다중 검색 전략**: Wildcard + Match Phrase Prefix 조합 검색
- **즐겨찾기 통합**: 검색 결과에 사용자별 즐겨찾기 상태 포함

**기술적 특징**:
- ApplicationReadyEvent 기반 초기 데이터 동기화
- ElasticFood 도큐먼트 + MySQL Food 엔티티 매핑
- 검색 성능 최적화를 위한 인덱스 설정

### 4. Persistence 모듈  
**민감한 건강 데이터 보안 및 암호화**

**핵심 기능**:
- **자동 데이터 암호화**: JPA AttributeConverter 기반 투명한 암호화
- **타입별 컨버터**: BigDecimal, String 타입별 전용 암호화 처리
- **성능 최적화**: Common Module CryptoUtils 재사용

**보안 특징**:
- DB 저장 시 자동 암호화, 조회 시 자동 복호화
- 체중, 신장 등 민감 정보 보호
- 암호화 키 관리는 Common Module에서 중앙 관리

### 5. Notification 모듈
**스마트 알림 시스템 및 사용자 참여 유도**

**핵심 기능**:
- **스케줄링 기반 자동 알림**: Cron 표현식으로 시간대별 맞춤 알림
- **개인화된 알림 로직**: 사용자별 기록 상태 기반 선별적 알림 발송
- **다양한 알림 유형**: 식단/운동/체중/비활성 사용자 알림
- **RabbitMQ 메시징**: 비동기 알림 처리 및 Alert Service 연동

**스케줄링 정책**:
- **14:00**: 점심 기록 미완료 사용자 알림
- **21:00**: 저녁 기록 미완료 사용자 알림  
- **22:00**: 당일 운동 기록 없는 사용자 알림
- **07:30**: 체중 업데이트 필요 사용자 알림
- **08:00**: 2일 이상 비활성 사용자 복귀 유도 알림

**기술적 특징**:
- Spring Scheduler + 한국 시간대(Asia/Seoul) 적용
- 복잡한 사용자 필터링 로직 (미기록자 추출)
- NotificationProducer를 통한 메시지 발행

---
## 🔄 서비스 간 연동 구조

### 전체 연동 흐름
```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Android App   │◄──►│   Gateway Svc   │◄──►│   User Service  │
└─────────┬───────┘    └─────────┬───────┘    └─────────────────┘
          │                      │                      ▲
          │            ┌─────────▼───────┐              │
          └───────────►│  Health Service │──────────────┘
                       │                 │
                       │  ┌─────────────┤
                       │  │BodyInfo     │──── 체성분 동기화
                       │  │Condition    │
                       │  │Exercise     │──┐
                       │  │Nutrition    │  │ RabbitMQ
                       │  │Step         │  │ 메시징
                       │  │             │  ▼
                       │  │Notification │ ┌─────────────────┐
                       │  └─────────────┤ │  Alert Service  │
                       └─────────────────┘ │  (FCM 푸시)     │
                                           └─────────────────┘
```

### User Service 연동 상세

**연동 목적**: 사용자 기본 정보 및 체성분 데이터 동기화

**주요 연동 포인트**:
```java
// 1. BMI/BMR 계산을 위한 사용자 정보 조회
@GetMapping("/me")
ResponseDto<UserDetailResponse> getUserByUserId(@RequestHeader("X-USER-ID") Integer userId);

// 2. 체성분 변경 시 User Service 동기화  
@PostMapping("/body-composition")
void updateBodyComposition(@RequestBody BodyCompositionRequest request);

// 3. 알림 대상 사용자 목록 조회
@GetMapping("/notifications-enabled") 
ResponseDto<List<UserResponse>> getUsersWithNotificationsEnabled();
```

**연동 시나리오**:
1. **체성분 등록/수정 시**: BodyInfo → User Service 자동 동기화
2. **BMI/BMR 계산 시**: User Service에서 나이, 성별, 생일 정보 조회
3. **알림 발송 시**: 알림 활성화된 사용자 목록 실시간 조회

### Alert Service 연동 상세

**연동 목적**: 개인화된 건강 관리 알림 및 동기부여 메시지 발송

**RabbitMQ 메시징 구조**:
```
Health Service → alert-exchange → Alert Service
                (Topic Exchange)
                     │
    ┌────────────────┼────────────────┐
    │                │                │
Routing Key:    Routing Key:    Routing Key:
"alert.push.    "alert.push.    "alert.push.
exercise.       meal.           weight.
requested"      requested"      requested"
```

**알림 메시지 구조**:
```java
NotificationMessageDto {
    Integer userId;
    NotificationType notificationType; // DIET, EXERCISE, WEIGHT, INACTIVE
    String message;                    // 개인화된 메시지
    LocalDateTime sentAt;
    NotificationChannel channel;       // PUSH, INAPP, BOTH
}
```

**알림 발송 시나리오**:
- **운동 기록 시**: Exercise Service → RabbitMQ → Alert Service
- **식단 등록 시**: Nutrition Service → RabbitMQ → Alert Service  
- **스케줄 알림**: Notification Service → RabbitMQ → Alert Service

---

## 📊 데이터 흐름 및 비즈니스 플로우

### 1. 신규 사용자 온보딩 플로우
```
1. 회원가입 완료 (User Service)
2. 건강 프로필 설정 화면 진입
3. Health Service → Condition Module:
   - 알레르기 목록 조회 (/api/bodies/allergies)
   - 질환 목록 조회 (/api/bodies/diseases)
4. 사용자 알레르기/질환 선택 후 토글 등록
5. Health Service → BodyInfo Module:
   - 기본 체성분 정보 입력
   - User Service 동기화
   - BMI/BMR 자동 계산
6. 개인화된 건강 프로필 완성
```

### 2. 일일 건강 기록 플로우
```
아침 (07:30) - 체중 업데이트 알림
 ↓
사용자 체성분 기록 (BodyInfo)
 ↓  
점심 (14:00) - 식사 기록 알림
 ↓
식단 기록 + 이미지 업로드 (Nutrition)
 ↓
운동 수행 후 기록 (Exercise)
 ↓
걸음 수 자동 동기화 (Step)
 ↓
저녁 (21:00) - 저녁 식사 기록 알림
 ↓
운동 기록 확인 알림 (22:00)
```

### 3. 개인화된 분석 제공 플로우
```
1. 사용자 데이터 수집 (모든 도메인 모듈)
2. 개인 건강 상태 분석:
   - Condition: 알레르기/질환 고려
   - BodyInfo: BMI/BMR 기반 권장 칼로리
   - Nutrition: 영양소 섭취 분석
   - Exercise: 운동량 및 소모 칼로리
   - Step: 일일 활동량 평가
3. 통합 건강 인사이트 생성
4. 맞춤형 권장사항 제공
5. 개선 목표 설정 지원
```

### 4. 데이터 검색 및 추천 플로우
```
1. 음식 검색 요청
2. Elastic Module → Elasticsearch:
   - Nori 분석기 기반 한글 검색
   - Wildcard + Match Phrase Prefix 조합
3. 검색 결과 + 즐겨찾기 상태 매핑
4. 개인화된 검색 결과 제공
5. 자주 검색하는 음식 → 자동 즐겨찾기 추천
```

---

## ⚙️ 기술 스택 및 의존성

### 핵심 기술 스택
| 구분 | 기술 | 버전 | 역할 |
|-----|-----|-----|-----|
| **Framework** | Spring Boot | 3.4.5 | 기본 애플리케이션 프레임워크 |
| **Cloud** | Spring Cloud | 2024.0.1 | 마이크로서비스 구성 |
| **Database** | MySQL | 8.0 | 주요 데이터 영구 저장소 |
| **ORM** | Spring Data JPA | - | 객체-관계 매핑 및 데이터 접근 |
| **Search** | Elasticsearch | - | 음식 검색 및 전문 검색 엔진 |
| **Storage** | MinIO | 8.5.2 | 이미지 및 파일 객체 스토리지 |
| **Messaging** | RabbitMQ | - | 비동기 메시지 처리 |
| **OCR** | Tesseract (tess4j) | 5.7.0 | 이미지 텍스트 인식 |
| **Security** | Spring Security | - | 인증 및 권한 관리 |
| **Monitoring** | Prometheus + Actuator | - | 메트릭 수집 및 모니터링 |

### 주요 의존성 구성
```gradle
dependencies {
    // Core Framework
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
    implementation 'org.springframework.boot:spring-boot-starter-security'
    implementation 'org.springframework.boot:spring-boot-starter-validation'
    
    // Microservice Infrastructure  
    implementation 'org.springframework.cloud:spring-cloud-starter-netflix-eureka-client'
    implementation 'org.springframework.cloud:spring-cloud-starter-config'
    implementation 'org.springframework.cloud:spring-cloud-starter-openfeign'
    implementation 'org.springframework.cloud:spring-cloud-starter-loadbalancer'
    
    // Database & Search
    runtimeOnly 'com.mysql:mysql-connector-j'
    implementation 'org.springframework.boot:spring-boot-starter-data-elasticsearch'
    
    // Messaging & Storage
    implementation 'org.springframework.boot:spring-boot-starter-amqp'
    implementation 'io.minio:minio:8.5.2'
    
    // Specialized Libraries
    implementation 'net.sourceforge.tess4j:tess4j:5.7.0'  // OCR
    implementation 'org.apache.poi:poi:5.2.3'             // Excel 처리
    
    // Monitoring
    implementation 'org.springframework.boot:spring-boot-starter-actuator'
    implementation 'io.micrometer:micrometer-registry-prometheus'
    
    // Common Module (Internal)
    implementation files('../common-module/build/libs/common-module-0.0.1-SNAPSHOT-plain.jar')
}
```

### 설정 관리 구조
```yaml
# Application Properties 계층 구조
application.yml                 # 기본 설정
├── spring.application.name: health-service
├── server.port: 0 (동적 포트)
├── eureka 서비스 등록 설정
└── management 메트릭 설정

bootstrap.yml                   # Config Server 연동
├── spring.config.import: configserver:http://localhost:8888
└── 중앙 설정 관리

Environment-specific configs    # 환경별 설정
├── application-dev.yml
├── application-prod.yml  
└── application-test.yml
```

---

## ⚡ 성능 최적화 전략

### 데이터베이스 최적화

**인덱스 전략**:
```sql
-- 사용자별 데이터 접근 최적화
CREATE INDEX idx_user_date ON meal_tb(user_id, meal_date);
CREATE INDEX idx_user_measurement ON body_tb(user_id, measurement_date);
CREATE INDEX idx_user_exercise ON exercise_tb(user_id, exercise_date);
CREATE INDEX idx_user_step ON step_count_tb(user_id, step_date);

-- 복합 유니크 제약 (중복 방지)
ALTER TABLE user_allergy_tb ADD UNIQUE KEY uk_user_allergy(user_id, allergy_id);
ALTER TABLE user_disease_tb ADD UNIQUE KEY uk_user_disease(user_id, disease_id);
```

**쿼리 최적화**:
- **배치 쿼리**: 즐겨찾기 상태 조회 시 IN 절 활용으로 N+1 문제 해결
- **집계 쿼리**: MySQL 레벨 GROUP BY 우선, 실패 시 애플리케이션 레벨 대체
- **페이징**: 대용량 데이터 조회 시 Pageable 적용
- **지연 로딩**: JPA 연관관계 LAZY 로딩으로 불필요한 조인 방지

### 애플리케이션 레벨 최적화

**비동기 처리**:
```java
// OCR 전용 스레드풀 구성
@Bean("ocrTaskExecutor")  
public ThreadPoolTaskExecutor ocrTaskExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(2);
    executor.setMaxPoolSize(4);
    executor.setQueueCapacity(10);
    return executor;
}

// 30초 타임아웃 비동기 OCR 처리
@Async("ocrTaskExecutor")
CompletableFuture<String> processOcr(MultipartFile image) {
    // Tesseract OCR 처리 로직
}
```

**메모리 관리**:
- **DTO 최적화**: 필요한 필드만 포함한 Response DTO 설계
- **Stream API**: 대용량 데이터 처리 시 효율적인 스트림 처리
- **BigDecimal 정밀도**: 체성분 수치 정확한 계산을 위한 BigDecimal 사용
- **객체 풀링**: MinIO 클라이언트 재사용으로 연결 오버헤드 최소화

### 외부 시스템 성능 최적화

**Elasticsearch 최적화**:
```json
// elastic-settings.json
{
  "analysis": {
    "analyzer": {
      "nori_analyzer": {
        "type": "custom",
        "tokenizer": "nori_tokenizer",
        "filter": ["lowercase", "nori_part_of_speech"]
      }
    }
  }
}
```

**MinIO 최적화**:
- **병렬 업로드**: 다중 이미지 업로드 시 병렬 처리
- **이미지 압축**: 업로드 전 클라이언트 사이드 이미지 최적화
- **CDN 준비**: 외부 엔드포인트 설정으로 CDN 연동 기반 마련

---

## 🛡️ 보안 및 데이터 관리

### 데이터 보안 전략

**민감 정보 암호화**:
```java
@Convert(converter = DecimalEncryptConverter.class)
private BigDecimal weight;  // 체중 정보 자동 암호화

@Convert(converter = StringEncryptConverter.class)  
private String personalInfo;  // 개인정보 자동 암호화
```

**접근 권한 관리**:
- **사용자별 데이터 격리**: 모든 API에서 X-USER-ID 헤더 기반 소유권 검증
- **Soft Delete**: 중요한 건강 데이터는 논리적 삭제로 복구 가능성 확보
- **입력값 검증**: Spring Validation 기반 모든 입력값 유효성 검사

### 시스템 보안

**인증 및 권한**:
```java
// Gateway에서 JWT 검증 후 사용자 ID 헤더 전달
@GetMapping("/api/bodies/info")
public ResponseEntity<BodyInfoResponse> getBodyInfo(
    @RequestHeader("X-USER-ID") Integer userId) {
    // 사용자별 데이터 접근 제어
}
```

**네트워크 보안**:
- **HTTPS 통신**: 모든 외부 통신 HTTPS 강제
- **내부 통신**: 마이크로서비스 간 Service Discovery 기반 보안 통신
- **방화벽 정책**: 필요한 포트만 개방 (MySQL: 3306, Elasticsearch: 9200, etc.)

### 데이터 무결성 보장

**트랜잭션 관리**:
```java
@Transactional
public void updateBodyInfoWithUserSync(BodyInfoRequest request) {
    // 1. BodyInfo 업데이트
    // 2. User Service 동기화  
    // 3. 실패 시 롤백 보장
}
```

**제약 조건**:
- **복합 유니크**: (user_id, date) 조합으로 중복 데이터 방지
- **외래키 제약**: 존재하지 않는 참조 데이터 방지
- **NOT NULL 제약**: 필수 데이터 누락 방지
- **범위 검증**: 체중(0-500kg), 체지방률(0-100%) 등 현실적 범위 검증

---

## 📈 모니터링 및 운영 관리

### 메트릭 수집 및 모니터링

**Prometheus 메트릭**:
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
    tags:
      application: health-service
```

**핵심 모니터링 지표**:
- **비즈니스 메트릭**: 일일 사용자 활성도, 모듈별 기록 완성률
- **시스템 메트릭**: API 응답 시간, 데이터베이스 연결 상태
- **OCR 성능**: 처리 시간, 성공률, 오류율
- **알림 효과**: 알림 발송 건수, 사용자 반응률

### 로깅 전략

**구조화된 로깅**:
```java
// 비즈니스 이벤트 로깅
log.info("사용자 {}가 체성분 정보 등록: 체중 {}kg, BMI {}", userId, weight, bmi);
log.info("OCR 처리 완료: 사용자 {}, 처리시간 {}ms, 성공여부 {}", userId, duration, success);

// 시스템 상태 로깅  
log.info("Elasticsearch 동기화 완료: {} 건의 음식 데이터 처리", syncCount);
log.info("알림 스케줄 실행: {} 명에게 {} 알림 발송", userCount, notificationType);
```

**에러 추적**:
- **예외 분류**: 비즈니스 예외 vs 시스템 예외 구분
- **사용자 친화적 메시지**: Common Module 기반 일관된 에러 응답
- **알림 실패 처리**: RabbitMQ 메시지 실패 시 재시도 로직

### 운영 자동화

**데이터 관리 자동화**:
- **기준 데이터 로딩**: ApplicationReadyEvent 기반 자동 초기화
- **Elasticsearch 동기화**: 애플리케이션 시작 시 MySQL → Elasticsearch 자동 동기화
- **이미지 정리**: 주기적 미사용 이미지 파일 정리 (예정)

**백업 및 복구**:
- **데이터베이스 백업**: 일일 자동 백업 스케줄
- **이미지 백업**: MinIO 데이터 정기 백업
- **설정 백업**: Config Server 설정 버전 관리

---

## 🚀 확장 및 개선 방안

### 단기 개선

**성능 최적화**:
- **Redis 캐싱**: 자주 조회되는 음식 정보, 기준 데이터 캐싱
- **Connection Pool 튜닝**: HikariCP 설정 최적화
- **쿼리 성능 개선**: 슐로우 쿼리 분석 및 인덱스 추가

**기능 개선**:
- **배치 API**: 대량 데이터 입력을 위한 배치 처리 API
- **이미지 최적화**: 자동 리사이징 및 썸네일 생성
- **알림 개인화**: 사용자 패턴 학습 기반 알림 시간 최적화

### 중기 개선

**AI/ML 기능 도입**:
- **음식 인식**: 이미지 기반 자동 음식 인식 및 영양소 추정
- **개인화 추천**: 사용자 건강 목표 기반 식단/운동 추천
- **건강 위험도 예측**: 개인 건강 데이터 기반 위험 요소 사전 감지

**고도화된 분석**:
- **통합 건강 점수**: 모든 건강 지표를 종합한 개인 건강 점수 산출  
- **목표 관리**: SMART 목표 설정 및 달성도 추적
- **소셜 기능**: 사용자 간 건강 관리 동기부여 및 경쟁 요소

### 장기 개선

**플랫폼 확장**:
- **웨어러블 연동**: 스마트워치, 체성분 측정기 등 IoT 기기 연동
- **의료진 연동**: 병원/영양사와의 데이터 공유 및 전문 상담 지원
- **보험 연동**: 건강 관리 실적 기반 보험료 할인 서비스

**글로벌 확장**:
- **다국어 지원**: 음식 데이터베이스 및 UI 다국어화
- **지역별 맞춤화**: 국가별 식단 문화 및 영양 기준 적용
- **규제 대응**: GDPR, HIPAA 등 국제 의료 데이터 규제 준수

**기술 혁신**:
- **실시간 스트리밍**: Kafka 기반 실시간 건강 데이터 스트리밍
- **GraphQL 도입**: 클라이언트 맞춤형 데이터 조회 최적화  
- **서버리스 확장**: 특정 기능의 서버리스 아키텍처 전환

---
