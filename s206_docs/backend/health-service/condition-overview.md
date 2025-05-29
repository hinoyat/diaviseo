# Health Service - Condition 모듈 개요

**디아비서(DiaViseo) 프로젝트의 건강 상태 관리 모듈**로, 사용자의 알레르기와 질환 정보를 관리하여 개인화된 건강 서비스 제공의 기반이 되는 핵심 모듈입니다.

---

## 📌 모듈 개요

### 역할과 책임
- **알레르기 관리**: 사용자별 음식 알레르기 등록/해제 및 조회
- **질환 관리**: 사용자별 기존 질환 등록/해제 및 조회
- **기준 데이터 제공**: 표준 알레르기/질환 목록 제공
- **건강 상태 기반 서비스 연동**: Nutrition/Exercise Service에 개인 건강 정보 제공
- **토글 방식 상태 관리**: 직관적인 등록/해제 인터페이스 제공

### MSA 구조에서의 위치
```
Android App → Gateway → Health Service (Condition Module)
                            ↕
                    Nutrition Service (알레르기 고려 식단)
                            ↕
                    Exercise Service (질환 고려 운동)
                            ↕
                        MySQL (health_db)
                            ↕
                    User Service (사용자 정보)
```

Condition 모듈은 **개인 건강 상태의 핵심 데이터**를 관리하여, 다른 건강 관련 서비스들이 사용자 맞춤형 서비스를 제공할 수 있도록 지원합니다.

---

## ⚙️ 기술 스택 & 의존성

| 구분 | 기술 | 버전 | 용도 |
|-----|-----|-----|-----|
| **Framework** | Spring Boot | 3.4.5 | 기본 프레임워크 |
| **Database** | MySQL | 8.0 | 건강 상태 데이터 저장 |
| **ORM** | Spring Data JPA | - | 데이터베이스 접근 |
| **Security** | Spring Security | - | 기본 보안 설정 |
| **Validation** | Spring Validation | - | 입력값 검증 |
| **Common** | Common Module | - | 공통 예외처리, 응답 형식 |
| **Service Discovery** | Eureka Client | - | 마이크로서비스 등록 |
| **Config** | Spring Cloud Config | - | 중앙 설정 관리 |
| **Monitoring** | Micrometer Prometheus | - | 메트릭 수집 |

### 주요 의존성
- **Common Module**: ResponseDto, 예외처리 표준화
- **MySQL**: 알레르기/질환 기준 데이터 및 사용자 건강 상태 영구 저장
- **JPA**: 복합 유니크 제약을 통한 데이터 무결성 보장

---

## 🔧 핵심 기능

### 1. 알레르기 관리 시스템
- **표준 알레르기 목록**: 식약처 기준 26개 주요 알레르기 항목 제공
- **사용자별 등록**: 개인 알레르기 정보 등록/해제
- **토글 방식**: 하나의 API로 등록과 해제를 모두 처리
- **중복 방지**: 동일 알레르기 중복 등록 차단

### 2. 질환 관리 시스템
- **표준 질환 목록**: 40개 주요 질환을 9개 카테고리로 분류 제공
- **카테고리 분류**: 심혈관계, 대사성, 내분비, 호흡기 등 체계적 분류
- **개인 질환 기록**: 사용자별 기존 질환 정보 관리
- **의료 표준 준수**: KCD-8 질병분류 기준 적용

### 3. 토글 기반 상태 관리
- **직관적 인터페이스**: 체크박스 방식의 간편한 등록/해제
- **상태 동기화**: 클라이언트-서버 간 상태 불일치 방지
- **원자적 처리**: 단일 트랜잭션으로 상태 변경 보장
- **명확한 피드백**: 수행된 동작과 현재 상태 명시

### 4. 서비스 간 연동
- **Nutrition Service 지원**: 알레르기 고려 식단 추천
- **Exercise Service 지원**: 질환 고려 운동 프로그램 제공
- **User Service 연동**: 사용자 식별 및 권한 관리
- **Gateway 인증**: JWT 토큰 기반 사용자 인증

---

## 🏗️ 시스템 아키텍처

### Condition 모듈 내부 구조
```
┌─────────────────────────────────────────────────────────────┐
│                    Condition Module                         │
├─────────────────────────────────────────────────────────────┤
│  Controller Layer (REST API)                               │
│  ├── UserAllergyController (알레르기 관리)                  │
│  ├── UserDiseaseController (질환 관리)                      │
├─────────────────────────────────────────────────────────────┤
│  Service Layer (비즈니스 로직)                               │
│  ├── UserAllergyService (알레르기 토글 로직)                │
│  ├── UserDiseaseService (질환 토글 로직)                    │
├─────────────────────────────────────────────────────────────┤
│  Repository Layer (데이터 접근)                             │
│  ├── FoodAllergyRepository (알레르기 기준 데이터)           │
│  ├── UserAllergyRepository (사용자 알레르기)                │
│  ├── DiseaseRepository (질환 기준 데이터)                   │
│  ├── UserDiseaseRepository (사용자 질환)                    │
├─────────────────────────────────────────────────────────────┤
│  Data Loader (초기화)                                       │
│  ├── FoodAllergyLoader (알레르기 기준 데이터 로딩)          │
│  ├── DiseaseLoader (질환 기준 데이터 로딩)                  │
├─────────────────────────────────────────────────────────────┤
│  External Integration                                        │
│  ├── MySQL (영구 데이터 저장)                               │
│  ├── Gateway (사용자 인증)                                  │
└─────────────────────────────────────────────────────────────┘
```

### 데이터 저장소 구조
- **MySQL (health_db)**: 알레르기/질환 기준 데이터 및 사용자 건강 상태 영구 저장
- **메모리**: 애플리케이션 시작 시 기준 데이터 자동 로딩

---

## 🗂️ 데이터베이스 설계

### 1. 알레르기 관련 테이블
**FoodAllergy (food_allergy_tb)**
- 26개 표준 알레르기 항목 저장
- 식약처 알레르기 유발식품 기준 적용
- 애플리케이션 시작 시 자동 로딩

**UserAllergy (user_allergy_tb)**
- 사용자별 알레르기 등록 정보
- (user_id, allergy_id) 복합 유니크 제약
- 생성일시 자동 기록

### 2. 질환 관련 테이블
**Disease (disease_tb)**
- 40개 표준 질환 정보 저장
- 카테고리별 분류 (9개 의료 분야)
- KCD-8 질병분류 기준 준수

**UserDisease (user_disease_tb)**
- 사용자별 질환 등록 정보
- (user_id, disease_id) 복합 유니크 제약
- 생성일시 자동 기록

### 3. 데이터 무결성 보장
```
FoodAllergy (1) ────────── (N) UserAllergy
     │                         │
     └── 표준 알레르기 정보        └── 사용자별 개인화

Disease (1) ────────── (N) UserDisease  
     │                         │
     └── 표준 질환 정보            └── 사용자별 개인화
```

**제약조건:**
- **유니크 제약**: 동일 사용자의 중복 등록 방지
- **외래키 제약**: 존재하지 않는 알레르기/질환 참조 방지
- **NOT NULL 제약**: 필수 데이터 누락 방지

---

## 🔄 주요 비즈니스 플로우

### 1. 초기 건강 상태 등록 플로우
```
1. 회원가입 후 건강 상태 설정 화면 진입
2. Android App → Condition Module: 전체 알레르기 목록 조회
3. Android App → Condition Module: 전체 질환 목록 조회
4. 사용자가 해당 알레르기/질환 선택
5. Android App → Condition Module: 선택된 항목들 토글 API 호출
6. Condition Module: 각 항목별 등록 처리 및 응답
7. 개인화된 건강 프로필 완성
```

### 2. 토글 방식 상태 관리 플로우
```
1. 사용자 → Condition Module: 알레르기/질환 토글 요청
2. Condition Service:
   - 기존 등록 여부 확인
   - 등록된 경우: 엔티티 삭제 (해제)
   - 미등록 경우: 엔티티 생성 (등록)
3. 트랜잭션 커밋 후 결과 응답
4. Android App에서 UI 상태 동기화
```

### 3. 서비스 간 연동 플로우
```
1. 사용자가 식단 추천 요청
2. Nutrition Service → Condition Module: 사용자 알레르기 조회
3. Nutrition Service → Condition Module: 사용자 질환 조회
4. Nutrition Service에서 건강 상태 고려한 식단 생성:
   - 알레르기 식재료 제외
   - 질환별 영양 가이드 적용
5. 개인화된 식단 추천 제공
```

### 4. 데이터 초기화 플로우
```
1. Health Service 애플리케이션 시작
2. ApplicationReadyEvent 발생
3. FoodAllergyLoader.loadFoodAllergies() 실행:
   - 기존 데이터 존재 여부 확인
   - 미존재 시 26개 표준 알레르기 데이터 생성
4. DiseaseLoader.loadDiseases() 실행:
   - 기존 데이터 존재 여부 확인
   - 미존재 시 40개 표준 질환 데이터 생성
5. 기준 데이터 로딩 완료, 서비스 준비 상태
```

---

## 🔐 보안 및 데이터 관리

### 사용자 인증 및 권한
```
1. Gateway에서 JWT 토큰 검증
2. X-USER-ID 헤더로 사용자 식별 정보 전달
3. Condition 모듈에서 사용자별 데이터 격리
4. 본인 데이터만 접근 가능하도록 제한
```

### 데이터 무결성 보장
- **복합 유니크 제약**: (user_id, allergy_id), (user_id, disease_id) 중복 방지
- **트랜잭션 관리**: 토글 작업의 원자성 보장
- **외래키 제약**: 존재하지 않는 기준 데이터 참조 차단
- **입력값 검증**: 잘못된 파라미터 사전 차단

### 의료 데이터 표준 준수
- **알레르기**: 식품의약품안전처 알레르기 유발식품 표시 기준
- **질환**: 한국표준질병·사인분류(KCD-8) 참고
- **카테고리**: 대한의사협회 질환 분류 체계 적용

---

## ⚡ 성능 최적화

### 데이터베이스 최적화
- **자동 인덱스**: Primary Key, Unique 제약으로 조회 성능 향상
- **지연 로딩**: @ManyToOne(LAZY) 적용으로 불필요한 조인 방지
- **배치 로딩**: 애플리케이션 시작 시 기준 데이터 일괄 로딩

### 응답 최적화
- **DTO 변환**: 엔티티 직접 노출 방지, 필요한 필드만 응답
- **스트림 API**: Java 8 Stream으로 데이터 변환 처리
- **트랜잭션 분리**: 읽기 전용 트랜잭션으로 성능 향상

### 메모리 효율성
- **소량 데이터**: 알레르기 26개, 질환 40개로 메모리 부담 최소
- **정적 데이터**: 기준 데이터는 변경이 거의 없어 캐싱 효과적
- **지연 초기화**: ApplicationReadyEvent 활용으로 필요시에만 로딩

---

## 📊 모니터링 및 운영

### Actuator 엔드포인트
| 엔드포인트 | URL | 용도 |
|-----------|-----|------|
| **Health Check** | `/actuator/health` | 모듈 상태 확인 |
| **Metrics** | `/actuator/prometheus` | Prometheus 메트릭 |
| **Info** | `/actuator/info` | 애플리케이션 정보 |

### 주요 메트릭
- **건강 상태 등록률**: 전체 사용자 중 알레르기/질환 등록 비율
- **토글 성공률**: 알레르기/질환 토글 API 성공률
- **서비스 연동률**: 다른 서비스에서 건강 상태 조회 빈도
- **데이터 품질**: 유니크 제약 위반 및 오류 발생률

### 로깅 전략
```java
// 비즈니스 로직 로깅
log.info("사용자 {}가 알레르기 {} 상태를 {}로 변경", userId, allergyName, action);
log.info("사용자 {}가 질환 {} 상태를 {}로 변경", userId, diseaseName, action);

// 시스템 로깅
log.info("알레르기 기준 데이터 {} 건 로딩 완료", count);
log.info("질환 기준 데이터 {} 건 로딩 완료", count);

// 에러 로깅
log.error("존재하지 않는 알레르기 ID {} 접근 - 사용자: {}", allergyId, userId);
```
