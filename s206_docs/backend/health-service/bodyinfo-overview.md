# BodyInfo 모듈 개요

**디아비서(DiaViseo) Health Service 내 BodyInfo 모듈은 사용자의 체성분 정보 관리, OCR 기반 자동 입력, 신체 변화 추적을 담당하는 핵심 서비스입니다.**

---

## 📌 모듈 개요

### BodyInfo 모듈의 역할
**BodyInfo 모듈**은 Health Service의 핵심 구성요소로, 사용자의 체성분 변화를 정확하게 추적하고 관리하는 종합적인 신체 정보 관리 시스템입니다.

- **체성분 정보 관리**: 체중, 체지방률, 근육량, 신장 등 핵심 신체 지표 기록
- **OCR 자동 입력**: Tesseract 기반 인바디 결과지 자동 인식 및 데이터 추출
- **BMI/BMR 계산**: 해리스-베네딕트 공식 기반 정확한 신체지수 계산
- **변화 추적**: 일별/주별/월별 체성분 변화 통계 및 트렌드 분석
- **User Service 연동**: 신체정보 변경 시 User Service 자동 동기화
- **데이터 검증**: 입력값 범위 검증 및 예외 상황 처리

### 핵심 가치 제안
1. **편리한 데이터 입력**: OCR 기반 인바디 결과지 자동 인식으로 수동 입력 최소화
2. **정확한 지수 계산**: 과학적 공식 기반 BMI, BMR 자동 계산
3. **체계적인 변화 추적**: 시계열 데이터 기반 신체 변화 모니터링
4. **안전한 데이터 관리**: Soft Delete와 소유권 검증을 통한 데이터 보안

---

## 🏗️ 시스템 아키텍처

### 전체 구조
```
┌─────────────────────────────────────────────────────────────┐
│                    Health Service                           │
│  ┌─────────────────────────────────────────────────────────┤
│  │                BodyInfo Module                          │
│  ├─────────────────────────────────────────────────────────┤
│  │  Controller Layer                                       │
│  │  └── BodyInfoController (체성분 정보 관리)              │
│  ├─────────────────────────────────────────────────────────┤
│  │  Service Layer                                          │
│  │  ├── BodyInfoService (체성분 CRUD 로직)                │
│  │  └── InBodyOcrService (OCR 처리 로직)                  │
│  ├─────────────────────────────────────────────────────────┤
│  │  Repository Layer                                       │
│  │  └── BodyInfoRepository (체성분 데이터)                │
│  ├─────────────────────────────────────────────────────────┤
│  │  Utility Layer                                          │
│  │  ├── HealthCalculator (BMI/BMR 계산)                   │
│  │  └── BodyMapper (엔티티/DTO 변환)                      │
│  └─────────────────────────────────────────────────────────┘
└─────────────────────────────────────────────────────────────┘
```

### 외부 시스템 연동
```
                    ┌─────────────────┐
                    │   Android App   │
                    └─────────┬───────┘
                              │
                    ┌─────────▼───────┐
                    │    Gateway      │
                    └─────────┬───────┘
                              │
        ┌─────────────────────▼─────────────────────┐
        │              Health Service              │
        │          (BodyInfo Module)               │
        └─┬─────────────────────────────────────┬─┘
          │                                     │
    ┌─────▼─────┐                         ┌────▼────┐
    │User Service│◄──── 신체정보 동기화 ───┤Tesseract│
    │(체성분 업데이트)│                       │(OCR)    │
    └───────────┘                         └─────────┘
```

---

## 🔧 핵심 기능

### 1. 체성분 정보 관리
**개요**: 사용자의 핵심 신체 지표를 체계적으로 관리

**주요 기능**:
- **체성분 등록**: 체중, 체지방률, 근육량, 신장 정보 기록
- **정보 조회**: 날짜별/기간별 체성분 정보 조회
- **부분 수정**: 특정 필드만 선택적 업데이트
- **안전한 삭제**: Soft Delete를 통한 데이터 보존

**기술적 특징**:
- BigDecimal 사용으로 정확한 소수점 계산
- 입력값 범위 검증 (체중: 0-500kg, 체지방률: 0-100% 등)
- 소유권 검증을 통한 보안 강화

### 2. OCR 자동 입력 시스템
**개요**: Tesseract OCR 엔진 기반 인바디 결과지 자동 인식

**주요 기능**:
- **이미지 업로드**: 인바디 결과지 이미지 파일 업로드
- **자동 텍스트 추출**: OCR을 통한 텍스트 자동 인식
- **데이터 파싱**: 정규식 기반 체성분 데이터 추출
- **비동기 처리**: 30초 타임아웃 비동기 OCR 처리
- **결과 확인**: OCR 결과 확인 후 수정 가능

**기술적 특징**:
- Tesseract 4.0+ 엔진 활용
- OS별 자동 경로 설정 (Windows/Linux)
- 고속 OCR 설정으로 처리 시간 최적화
- CompletableFuture 기반 비동기 처리

### 3. 신체지수 계산 시스템
**개요**: 과학적 공식 기반 정확한 신체지수 자동 계산

**주요 기능**:
- **BMI 계산**: 체중(kg) ÷ 신장(m)² 공식
- **BMR 계산**: 해리스-베네딕트 공식 기반 기초대사율
- **성별/연령 고려**: 성별과 연령을 반영한 정확한 계산
- **실시간 계산**: 체성분 입력/수정 시 자동 재계산

**계산 공식**:
```
BMI = 체중(kg) ÷ 신장(m)²

BMR (남성) = 88.362 + (13.397 × 체중) + (4.799 × 신장) - (5.677 × 나이)
BMR (여성) = 447.593 + (9.247 × 체중) + (3.098 × 신장) - (4.330 × 나이)
```

### 4. 변화 추적 및 통계
**개요**: 시계열 데이터 기반 신체 변화 모니터링

**주요 기능**:
- **주간 데이터**: 최근 7일간 일별 체성분 변화
- **주별 평균**: 최근 7주간 주별 평균 체성분
- **월별 평균**: 최근 7개월간 월별 평균 체성분
- **빈 데이터 처리**: 데이터가 없는 날짜는 0값으로 채움

**기술적 특징**:
- 복잡한 날짜 범위 계산 로직
- 0값 제외 평균 계산으로 정확성 향상
- 메모리 효율적인 스트림 처리

### 5. User Service 연동
**개요**: 체성분 변경 시 User Service 자동 동기화

**주요 기능**:
- **자동 업데이트**: 체성분 등록 시 User Service에 자동 전송
- **OpenFeign 연동**: 선언적 HTTP 클라이언트로 안정적 통신
- **데이터 일관성**: 양방향 데이터 동기화 보장

**연동 데이터**:
- 사용자 기본 정보 (나이, 성별, 생일)
- 신장, 체중 정보
- BMI, BMR 계산을 위한 필수 데이터

---

## 💾 데이터 모델

### 핵심 엔티티 구조
```
User (외부) ──→ BodyInfo
                 │
                 ├── InputType (OCR/MANUAL)
                 ├── measurementDate
                 ├── weight, bodyFat, muscleMass, height
                 └── createdAt, updatedAt, deletedAt, isDeleted
```

### BodyInfo 엔티티 (body_tb)
| 필드 | 타입 | 제약조건 | 설명 |
|-----|------|----------|------|
| **bodyId** | Integer | PK, Auto Increment | 체성분 정보 ID |
| **userId** | Integer | NOT NULL | 사용자 ID |
| **inputType** | Enum | NOT NULL | 입력 방식 (OCR/MANUAL) |
| **height** | BigDecimal | NULL | 신장 (cm) |
| **weight** | BigDecimal | NOT NULL | 체중 (kg) |
| **bodyFat** | BigDecimal | NULL | 체지방률 (%) |
| **muscleMass** | BigDecimal | NULL | 근육량 (kg) |
| **measurementDate** | LocalDate | NOT NULL | 측정일 |
| **createdAt** | LocalDateTime | NOT NULL | 생성일시 |
| **updatedAt** | LocalDateTime | NOT NULL | 수정일시 |
| **deletedAt** | LocalDateTime | NULL | 삭제일시 |
| **isDeleted** | Boolean | DEFAULT false | 삭제 여부 |

### 입력 타입 열거형
```java
public enum InputType {
    OCR("OCR"),      // OCR 자동 입력
    MANUAL("MANUAL") // 수동 입력
}
```

---

## 🔄 서비스 연동

### User Service 연동
**목적**: 체성분 정보 변경 시 사용자 기본 정보 동기화

**연동 API**:
- `GET /api/users/{userId}`: 사용자 정보 조회 (BMI/BMR 계산용)
- `POST /api/users/body-composition`: 체성분 정보 업데이트

**연동 시점**:
- 체성분 정보 등록 시
- 체성분 정보 수정 시
- BMI/BMR 계산이 필요한 모든 조회 시

**연동 방식**:
```java
@FeignClient(name = "user-service")
public interface UserClient {
    @GetMapping("/api/users/{userId}")
    ResponseDto<UserDetailResponse> getUserByUserId(@PathVariable Integer userId);
    
    @PostMapping("/api/users/body-composition")
    void updateBodyComposition(@RequestBody BodyCompositionRequest request);
}
```

### Tesseract OCR 엔진 연동
**목적**: 인바디 결과지 이미지에서 자동으로 체성분 데이터 추출

**기술 스택**:
- **Tesseract 4.0+**: 고성능 OCR 엔진
- **tessdata**: 다국어 인식 데이터
- **정규식 파싱**: 추출된 텍스트에서 정확한 수치 파싱

**최적화 설정**:
```java
tesseract.setTessVariable("tessedit_ocr_engine_mode", "0");  // Legacy 엔진 (빠름)
tesseract.setTessVariable("tessedit_pageseg_mode", "6");     // 단일 텍스트 블록
tesseract.setTessVariable("classify_bln_numeric_mode", "1"); // 숫자 우선 인식
```

---

## ⚡ 성능 및 확장성

### 성능 최적화 전략

**OCR 처리 최적화**:
- 비동기 처리로 응답 시간 개선 (30초 타임아웃)
- 고속 OCR 설정으로 처리 속도 향상
- 메모리 효율적인 이미지 처리

**데이터베이스 최적화**:
- 사용자별 체성분 조회 인덱스 (userId, measurementDate)
- Soft Delete 쿼리 최적화 (isDeleted = false)
- 날짜 범위 쿼리 최적화

**메모리 효율성**:
- BigDecimal 사용으로 정확한 계산
- Stream API 활용한 효율적인 데이터 처리
- 불필요한 객체 생성 최소화

### 확장 가능성

**OCR 기능 확장**:
- 다양한 체성분 측정기 결과지 지원
- AI 기반 이미지 전처리로 인식률 향상
- 실시간 OCR 처리 성능 개선

**데이터 분석 확장**:
- 체성분 변화 트렌드 예측 알고리즘
- 개인화된 건강 목표 설정 및 추천
- 다른 건강 지표와의 상관관계 분석

**연동 서비스 확장**:
- 웨어러블 디바이스 연동 (스마트 체중계 등)
- 운동/영양 서비스와 데이터 연계
- 의료진 상담 서비스 연동

---

## 🛡️ 보안 및 안정성

### 데이터 보안
- **소유권 검증**: 모든 체성분 데이터 접근 시 사용자 권한 엄격 검증
- **입력값 검증**: 체성분 수치의 현실적 범위 검증
- **Soft Delete**: 중요한 건강 데이터 논리적 삭제로 복구 가능성 확보

### 시스템 안정성
- **예외 처리**: OCR 실패, 네트워크 오류 등 다양한 예외 상황 대응
- **타임아웃 관리**: OCR 처리 30초 타임아웃으로 무한 대기 방지
- **트랜잭션 관리**: 데이터 일관성 보장

### OCR 보안
- **이미지 임시 처리**: 업로드된 이미지 메모리 처리 후 즉시 삭제
- **화이트리스트**: OCR 인식 문자 제한으로 보안 강화
- **비동기 처리**: 시스템 리소스 효율적 사용

---
