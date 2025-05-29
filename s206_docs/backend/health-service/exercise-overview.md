# Health Service - Exercise Domain Overview

**디아비서(DiaViseo) 프로젝트의 건강 관리 서비스 중 운동(Exercise) 도메인을 담당하는 모듈입니다.**

이 모듈은 사용자 맞춤형 운동 기록 관리, 즐겨찾기 운동, 운동 통계, 운동 유형/카테고리 관리 등 전반적인 운동 데이터를 다루며, MSA 구조에서 핵심 건강 정보 흐름을 담당합니다.

---

## 📌 서비스 역할 및 책임

| 영역                 | 설명                                                          |
| ------------------ | ----------------------------------------------------------- |
| ● **운동 기록 관리**     | 사용자가 수행한 운동의 일자, 시간, 칼로리 등을 기록하고 수정 및 삭제할 수 있음              |
| ● **운동 유형 / 카테고리** | 운동 유형 및 카테고리를 분리하여 구조화된 운동 기록을 지원함. 자주 사용하는 운동은 즐겨찾기로 관리 가능 |
| ● **즐겨찾기 기능**      | 사용자 맞춤형으로 자주 사용하는 운동을 즐겨찾기 등록/해제할 수 있음. 운동 추천에도 활용 가능       |
| ● **운동 통계 분석**     | 일별/주별/월별 기준으로 누적 칼로리, 횟수, 평균값 등의 통계 정보를 제공함                 |
| ● **연동 서비스 통합**    | 사용자 인증을 위한 User Service 연동 및 운동 알림 전송을 위한 Alert Service 연동  |

---

## 🔄 마이크로서비스 연동 구조

```
Android App
   ↓
Gateway Service
   ↓
Health Service (Exercise 도메인)
   ├── User Service 연동: 사용자 ID, 프로필 정보 (X-USER-ID)
   └── Alert Service 연동: 운동 생성/수정 이벤트 발생 시 알림 전송 (RabbitMQ)
```

* 사용자 인증은 Gateway에서 처리 → X-USER-ID 헤더를 통해 Health Service에 전달
* 운동 수행 데이터 생성 시, `ExerciseNotificationPublisher`를 통해 메시지 전파
* 통계 조회는 내부 DB 기준이며 외부 호출 없음

---

## 🔄 전형적인 비즈니스 플로우 예시

### 1. 운동 기록 등록 및 통계 반영 흐름

```
1. 사용자가 앱에서 운동 기록 등록 요청 (POST /api/exercises)
2. Health Service는 해당 운동의 칼로리 계산 (운동 유형 기준)
3. DB에 운동 기록 저장 → 알림 발송을 위한 메시지 전파 (RabbitMQ)
4. 사용자 통계(BMR, TDEE)는 별도 API 호출 시 계산됨 (DB 기준)
```

### 2. 즐겨찾기 운동 등록

```
1. 사용자가 앱에서 특정 운동에 즐겨찾기 요청 (POST /favorites/{exerciseNumber})
2. Health Service는 FavoriteExercise 테이블에 userId + exerciseNumber 저장
3. 이후 운동 유형 조회 시 isFavorite=true로 응답됨
```

### 3. 통계 데이터 제공 흐름

```
1. 클라이언트가 일/주/월 통계 요청 (GET /api/exercises/daily 등)
2. Health Service는 해당 날짜 범위의 운동 기록을 group by 후 계산
3. 누적 칼로리, 평균 시간, 수행 횟수 등 가공하여 응답
```

---

## ⚠️ 주의할 설계적 고려사항

* **BMR/TDEE 계산은 사용자 신체정보 기반이며, User Service DB에 의존**

* → Health Service 내에서 가장 최신 신체정보를 필요 시 User로부터 받아와야 함 (현재는 userId 기반 DB 캐시)

* **알림 발송은 RabbitMQ 메시지 발행만 수행, FCM 전송은 Alert Service가 담당**

* **MinIO는 운동 관련 OCR 이미지 저장용이나, 현재 코드 기반으로는 아직 OCR 추출 기능이 직접 연결되어 있지 않음** (보류됨)

---

## ⚙️ 기술 스택

| 구분           | 기술                    | 설명                                 |
| ------------ | --------------------- | ---------------------------------- |
| ● Framework  | Spring Boot 3.4.5     | 기본 프레임워크                           |
| ● Web Layer  | Spring MVC + WebFlux  | REST API + 비동기 동작을 위한 WebClient 병용 |
| ● ORM        | Spring Data JPA       | 엔티티 기반의 DB 매핑                      |
| ● DB         | MySQL (`health_db`)   | 운동 및 통계 데이터 저장소                    |
| ● Messaging  | RabbitMQ              | 운동 알림 발송을 위한 비동기 메시징 처리            |
| ● Search     | Elasticsearch         | 운동 검색 고도화 및 향후 유사 운동 추천 기능 확장 예정   |
| ● Storage    | MinIO                 | OCR 이미지 저장 및 관리                    |
| ● OCR        | Tesseract (`tess4j`)  | 운동 관련 이미지에서 텍스트 추출 가능              |
| ● Monitoring | Prometheus + Actuator | 메트릭 수집 및 상태 모니터링                   |

---

## 📂 DB 테이블 구조 (MySQL - `health_db`)

### 1. `exercise_tb`

* 사용자 운동 기록 저장
* 주요 컬럼: `exercise_id`, `user_id`, `exercise_number`, `exercise_time`, `exercise_date`, `exercise_calorie`, `health_connect_uuid`

### 2. `exercise_type_tb`

* 운동 유형 정의 (ex. 스쿼트, 플랭크, 걷기)
* 주요 컬럼: `exercise_type_id`, `exercise_category_id`, `exercise_number`, `exercise_name`, `exercise_calorie`

### 3. `exercise_category_tb`

* 운동 유형 분류용 카테고리 테이블
* 주요 컬럼: `exercise_category_id`, `exercise_category_name`

### 4. `favorite_exercise_tb`

* 사용자별 즐겨찾기 운동 관리 테이블
* 주요 컬럼: `user_id`, `exercise_number` (복합 유니크)

---

## 🧱 내부 컴포넌트 구조

```
Health Service
├── ExerciseController              ← 운동 CRUD API
├── ExerciseStatsController         ← 운동 통계 API
├── FavoriteExerciseController      ← 즐겨찾기 등록/해제/조회 API
├── ExerciseService                 ← 운동 생성, 수정, 삭제, 유형 조회 등
├── ExerciseStatsService            ← 운동 통계 계산 전담
├── FavoriteExerciseService         ← 즐겨찾기 관리 전담
├── ExerciseRepository              ← 운동 기록 DB 접근
├── ExerciseTypeRepository          ← 운동 유형/카테고리 DB 접근
├── FavoriteExerciseRepository      ← 즐겨찾기 DB 접근
└── ExerciseNotificationPublisher   ← RabbitMQ 메시지 발행
```

---

## ❗ 개선이 필요한 핵심 문제점 요약

### 1. Service 책임 과다

* ExerciseService가 CRUD, 유형 조회, 통계, 알림 발송까지 과도하게 담당
* → CQRS 설계 분리 필요: `ExerciseCommandService`, `ExerciseQueryService`, `ExerciseEventService` 등으로 분리 권장

### 2. DTO 설계 문제

* 단일 DTO(`ExerciseListResponse`)로 단건/목록/상세 모두 처리 중
* 칼로리를 클라이언트가 직접 입력하는 설계는 보안상 부적절함
* → DTO 세분화 필요: CreateRequest / SummaryResponse / DetailResponse 등 분리

### 3. 통계 계산을 Java 코드에서 수행

* Java 로직 기반 계산 시 데이터가 많아질수록 성능 저하 발생
* → 쿼리 기반 group by 집계 전환 필요, `(user_id, date)` 인덱싱 최적화 필요

### 4. JPA 매핑 이슈

* 현재 코드에는 `@ManyToOne` 등 객체 매핑이 생략되어 있으며, `exerciseTypeId`, `userId` 등의 외래키 필드만 직접 사용하는 ID 기반 구조임
* 이로 인해 직접적인 Lazy 로딩은 발생하지 않지만, 객체 지향적인 모델링이 부족하여 타입 안정성, 도메인 모델 추론이 어려움
* 반대로 실제 매핑을 도입할 경우에는 `JOIN FETCH`, DTO Projection, EntityGraph 등의 최적화 기술을 반드시 병행해야 N+1 문제를 피할 수 있음
* → 설계 선택에 따라 단순 ID 기반 구조 유지 vs 엔티티 기반 매핑 도입 여부를 명확히 해야 함

---
