# Health Service - Condition API 명세

**Health Service의 Condition 모듈은 사용자의 알레르기와 질환 정보를 관리하는 건강 상태 관리 서비스입니다.**

---

## 📌 API 개요

### Base URL
```
http://health-service/api/bodies
```

### Condition 모듈의 역할
- **알레르기 관리**: 사용자별 음식 알레르기 등록/해제
- **질환 관리**: 사용자별 기존 질환 등록/해제  
- **기준 데이터 제공**: 알레르기/질환 전체 목록 조회
- **토글 방식**: 등록/해제를 하나의 API로 처리
- **영양 연동**: 식단 추천 시 알레르기/질환 정보 활용

### 공통 응답 형식
모든 API는 Common Module의 `ResponseDto` 형식을 사용합니다.

```json
{
  "timestamp": "2025-05-27T10:30:00",
  "status": "OK", 
  "message": "성공 메시지",
  "data": { /* 실제 데이터 */ }
}
```

### 공통 HTTP 상태코드
| 상태코드 | 설명 | 응답 예시 |
|---------|------|----------|
| **200** | 성공 | `"message": "기준 알러지 목록 조회 완료"` |
| **400** | 잘못된 요청 | `"message": "필수 파라미터가 누락되었습니다."` |
| **401** | 인증 실패 | `"message": "유효하지 않은 토큰입니다."` |
| **404** | 데이터 없음 | `"message": "해당 알러지를 찾을 수 없습니다."` |
| **500** | 서버 오류 | `"message": "서버 내부 오류가 발생했습니다."` |

---

## 🥜 알레르기 관리 API

### 1. 전체 알레르기 목록 조회

시스템에 등록된 모든 음식 알레르기 목록을 조회합니다.

#### `GET /api/bodies/allergies`

**Request Headers**
```http
Content-Type: application/json
```

**처리 과정**
1. **기준 데이터 조회**: FoodAllergy 엔티티의 모든 데이터 조회
2. **응답 변환**: FoodAllergyResponse DTO로 변환
3. **목록 반환**: 알레르기 ID와 이름 리스트 반환

**Response (성공)**
```json
{
  "timestamp": "2025-05-27T10:30:00",
  "status": "OK",
  "message": "기준 알러지 목록 조회 완료",
  "data": [
    {
      "allergyId": 1,
      "allergyName": "우유"
    },
    {
      "allergyId": 2,
      "allergyName": "계란"
    },
    {
      "allergyId": 3,
      "allergyName": "메밀"
    },
    {
      "allergyId": 4,
      "allergyName": "땅콩"
    },
    {
      "allergyId": 5,
      "allergyName": "대두"
    }
  ]
}
```

**등록된 알레르기 목록**
| ID | 알레르기명 | ID | 알레르기명 |
|----|-----------|----|-----------| 
| 1 | 우유 | 14 | 쇠고기 |
| 2 | 계란 | 15 | 오징어 |
| 3 | 메밀 | 16 | 조개류(굴, 전복, 홍합) |
| 4 | 땅콩 | 17 | 아몬드 |
| 5 | 대두 | 18 | 잣 |
| 6 | 밀 | 19 | 캐슈넛 |
| 7 | 고등어 | 20 | 키위 |
| 8 | 게 | 21 | 바나나 |
| 9 | 새우 | 22 | 망고 |
| 10 | 복숭아 | 23 | 파인애플 |
| 11 | 토마토 | 24 | 감귤 |
| 12 | 호두 | 25 | 초콜릿 |
| 13 | 닭고기 | 26 | 꿀 |

---

### 2. 내 알레르기 목록 조회

로그인한 사용자가 등록한 알레르기 목록을 조회합니다.

#### `GET /api/bodies/allergies/my`

**Request Headers**
```http
Authorization: Bearer {accessToken}
X-USER-ID: {userId}  # Gateway에서 자동 추가
```

**처리 과정**
1. **사용자 검증**: X-USER-ID 헤더로 사용자 식별
2. **등록 알레르기 조회**: UserAllergy 엔티티에서 해당 사용자 데이터 조회
3. **조인 처리**: FoodAllergy 엔티티와 조인하여 알레르기 상세 정보 획득
4. **응답 변환**: UserAllergyResponse DTO로 변환 (isRegistered=true 고정)

**Response (성공)**
```json
{
  "timestamp": "2025-05-27T10:35:00",
  "status": "OK",
  "message": "내 알러지 목록 조회 완료",
  "data": [
    {
      "allergyId": 1,
      "allergyName": "우유",
      "isRegistered": true
    },
    {
      "allergyId": 4,
      "allergyName": "땅콩",
      "isRegistered": true
    },
    {
      "allergyId": 12,
      "allergyName": "호두",
      "isRegistered": true
    }
  ]
}
```

**Response (등록된 알레르기 없음)**
```json
{
  "timestamp": "2025-05-27T10:35:00",
  "status": "OK",
  "message": "내 알러지 목록 조회 완료",
  "data": []
}
```

---

### 3. 알레르기 등록/해제 토글

특정 알레르기를 등록하거나 해제합니다. 이미 등록된 경우 해제하고, 등록되지 않은 경우 등록합니다.

#### `POST /api/bodies/allergies/{allergyId}/toggle`

**Request Headers**
```http
Authorization: Bearer {accessToken}
X-USER-ID: {userId}  # Gateway에서 자동 추가
```

**Path Variables**
| 변수 | 타입 | 설명 |
|-----|------|------|
| `allergyId` | Long | 알레르기 ID (1-26) |

**처리 과정**
1. **기존 등록 확인**: UserAllergy 테이블에서 (userId, allergyId) 조합 조회
2. **등록된 경우**: 
   - UserAllergy 엔티티 삭제
   - isRegistered=false, message="알러지 해제 완료" 응답
3. **등록되지 않은 경우**:
   - FoodAllergy 존재 여부 확인
   - 새로운 UserAllergy 엔티티 생성
   - isRegistered=true, message="알러지 등록 완료" 응답

**Response (등록 성공)**
```json
{
  "timestamp": "2025-05-27T10:40:00",
  "status": "OK",
  "message": "알러지 상태 변경 완료",
  "data": {
    "allergyId": 1,
    "isRegistered": true,
    "message": "알러지 등록 완료"
  }
}
```

**Response (해제 성공)**
```json
{
  "timestamp": "2025-05-27T10:45:00",
  "status": "OK",
  "message": "알러지 상태 변경 완료",
  "data": {
    "allergyId": 1,
    "isRegistered": false,
    "message": "알러지 해제 완료"
  }
}
```

**에러 응답**
| 상태코드 | 에러 케이스 | 응답 메시지 |
|---------|-----------|-----------|
| **404** | 존재하지 않는 알레르기 ID | `"해당 알러지를 찾을 수 없습니다."` |

---

## 🏥 질환 관리 API

### 4. 전체 질환 목록 조회

시스템에 등록된 모든 질환 목록을 조회합니다.

#### `GET /api/bodies/diseases`

**Request Headers**
```http
Content-Type: application/json
```

**처리 과정**
1. **기준 데이터 조회**: Disease 엔티티의 모든 데이터 조회
2. **응답 변환**: DiseaseResponse DTO로 변환
3. **카테고리 포함**: 질환 ID, 이름, 카테고리 정보 반환

**Response (성공)**
```json
{
  "timestamp": "2025-05-27T11:00:00",
  "status": "OK",
  "message": "기준 질환 목록 조회 완료",
  "data": [
    {
      "diseaseId": 1,
      "diseaseName": "고혈압",
      "category": "심혈관계"
    },
    {
      "diseaseId": 2,
      "diseaseName": "당뇨병",
      "category": "대사성"
    },
    {
      "diseaseId": 3,
      "diseaseName": "고지혈증",
      "category": "대사성"
    },
    {
      "diseaseId": 4,
      "diseaseName": "심부전",
      "category": "심혈관계"
    }
  ]
}
```

**등록된 질환 목록 (카테고리별)**

**심혈관계**
- 고혈압, 심부전, 협심증, 심근경색

**대사성**
- 당뇨병, 고지혈증

**뇌혈관**
- 뇌졸중

**간질환**
- 간경변, 지방간

**신장질환**
- 신부전, 만성신염

**호흡기**
- 천식, 만성 폐쇄성 폐질환, 비염

**소화기**
- 위염, 위궤양, 대장염, 크론병

**내분비**
- 갑상선 기능 저하증, 갑상선 기능 항진증

**근골격계**
- 골다공증

**면역계**
- 류마티스 관절염, 루푸스  

**피부질환**
- 건선, 아토피 피부염

**신경계**
- 알츠하이머병, 파킨슨병, 간질

**정신과**
- 우울증, 공황장애, 불안장애, 수면장애

**암**
- 간암, 폐암, 위암, 유방암, 전립선암, 자궁경부암

**감염성**
- 결핵

**정신과/영양**
- 식이장애

---

### 5. 내 질환 목록 조회

로그인한 사용자가 등록한 질환 목록을 조회합니다.

#### `GET /api/bodies/diseases/my`

**Request Headers**
```http
Authorization: Bearer {accessToken}
X-USER-ID: {userId}  # Gateway에서 자동 추가
```

**처리 과정**
1. **사용자 검증**: X-USER-ID 헤더로 사용자 식별
2. **등록 질환 조회**: UserDisease 엔티티에서 해당 사용자 데이터 조회
3. **조인 처리**: Disease 엔티티와 조인하여 질환 상세 정보 획득
4. **응답 변환**: UserDiseaseResponse DTO로 변환 (isRegistered=true 고정)

**Response (성공)**
```json
{
  "timestamp": "2025-05-27T11:05:00",
  "status": "OK",
  "message": "내 질환 목록 조회 완료",
  "data": [
    {
      "diseaseId": 1,
      "diseaseName": "고혈압",
      "isRegistered": true
    },
    {
      "diseaseId": 2,
      "diseaseName": "당뇨병",
      "isRegistered": true
    }
  ]
}
```

**Response (등록된 질환 없음)**
```json
{
  "timestamp": "2025-05-27T11:05:00",
  "status": "OK",
  "message": "내 질환 목록 조회 완료",
  "data": []
}
```

---

### 6. 질환 등록/해제 토글

특정 질환을 등록하거나 해제합니다. 이미 등록된 경우 해제하고, 등록되지 않은 경우 등록합니다.

#### `POST /api/bodies/diseases/{diseaseId}/toggle`

**Request Headers**
```http
Authorization: Bearer {accessToken}
X-USER-ID: {userId}  # Gateway에서 자동 추가
```

**Path Variables**
| 변수 | 타입 | 설명 |
|-----|------|------|
| `diseaseId` | Long | 질환 ID |

**처리 과정**
1. **기존 등록 확인**: UserDisease 테이블에서 (userId, diseaseId) 조합 조회
2. **등록된 경우**: 
   - UserDisease 엔티티 삭제
   - isRegistered=false, message="질환 해제 완료" 응답
3. **등록되지 않은 경우**:
   - Disease 존재 여부 확인
   - 새로운 UserDisease 엔티티 생성
   - isRegistered=true, message="질환 등록 완료" 응답

**Response (등록 성공)**
```json
{
  "timestamp": "2025-05-27T11:10:00",
  "status": "OK",
  "message": "질환 상태 변경 완료",
  "data": {
    "diseaseId": 1,
    "isRegistered": true,
    "message": "질환 등록 완료"
  }
}
```

**Response (해제 성공)**
```json
{
  "timestamp": "2025-05-27T11:15:00",
  "status": "OK",
  "message": "질환 상태 변경 완료",
  "data": {
    "diseaseId": 1,
    "isRegistered": false,
    "message": "질환 해제 완료"
  }
}
```

**에러 응답**
| 상태코드 | 에러 케이스 | 응답 메시지 |
|---------|-----------|-----------|
| **404** | 존재하지 않는 질환 ID | `"해당 질환을 찾을 수 없습니다."` |

---

## 📊 API 사용 시나리오

### 1. 첫 회원가입 후 건강 상태 등록 플로우
```
1. Android App → Gateway: GET /api/bodies/allergies
   → 전체 알레르기 목록 조회하여 선택지 제공

2. Android App → Gateway: GET /api/bodies/diseases  
   → 전체 질환 목록 조회하여 선택지 제공

3. 사용자가 해당하는 알레르기들 선택 (우유, 땅콩, 호두)

4. Android App → Gateway: POST /api/bodies/allergies/1/toggle
   → "우유" 알레르기 등록

5. Android App → Gateway: POST /api/bodies/allergies/4/toggle
   → "땅콩" 알레르기 등록

6. Android App → Gateway: POST /api/bodies/allergies/12/toggle
   → "호두" 알레르기 등록

7. 사용자가 해당하는 질환들 선택 (고혈압, 당뇨병)

8. Android App → Gateway: POST /api/bodies/diseases/1/toggle
   → "고혈압" 질환 등록

9. Android App → Gateway: POST /api/bodies/diseases/2/toggle
   → "당뇨병" 질환 등록
```

### 2. 기존 사용자 건강 상태 조회 플로우
```
1. Android App → Gateway: GET /api/bodies/allergies/my
   → 내가 등록한 알레르기 목록 조회

2. Android App → Gateway: GET /api/bodies/diseases/my
   → 내가 등록한 질환 목록 조회

3. 화면에 등록된 알레르기/질환 정보 표시
   → 식단 추천, 운동 추천 시 이 정보 활용
```

### 3. 건강 상태 수정 플로우
```
1. 사용자가 "우유" 알레르기 해제 결정

2. Android App → Gateway: POST /api/bodies/allergies/1/toggle
   → 기존 등록된 "우유" 알레르기 해제
   → Response: { "isRegistered": false, "message": "알러지 해제 완료" }

3. 사용자가 "간경변" 질환 새로 등록

4. Android App → Gateway: POST /api/bodies/diseases/8/toggle
   → 새로운 "간경변" 질환 등록
   → Response: { "isRegistered": true, "message": "질환 등록 완료" }
```

### 4. 영양 서비스 연동 플로우
```
1. 사용자가 식단 추천 요청

2. Nutrition Service → Health Service: GET /api/bodies/allergies/my
   → 사용자 알레르기 정보 조회

3. Nutrition Service → Health Service: GET /api/bodies/diseases/my
   → 사용자 질환 정보 조회

4. Nutrition Service에서 알레르기/질환 고려한 식단 추천
   → 우유 알레르기 → 유제품 제외
   → 당뇨병 → 저당 식단 추천
   → 고혈압 → 저염 식단 추천
```

---

## 🗂️ 데이터베이스 설계

### 1. FoodAllergy 엔티티 (food_allergy_tb)
| 필드 | 타입 | 제약조건 | 설명 |
|-----|------|----------|------|
| **id** | Long | PK, Auto Increment | 알레르기 고유 ID |
| **name** | String(50) | UNIQUE, NOT NULL | 알레르기명 |

### 2. UserAllergy 엔티티 (user_allergy_tb)
| 필드 | 타입 | 제약조건 | 설명 |
|-----|------|----------|------|
| **id** | Integer | PK, Auto Increment | 사용자 알레르기 ID |
| **userId** | Integer | NOT NULL | 사용자 ID |
| **allergy** | FoodAllergy | FK, NOT NULL | 알레르기 참조 |
| **createdAt** | LocalDateTime | NOT NULL | 등록일시 |

**제약조건**: `UNIQUE(user_id, allergy_id)` - 사용자별 동일 알레르기 중복 등록 방지

### 3. Disease 엔티티 (disease_tb)
| 필드 | 타입 | 제약조건 | 설명 |
|-----|------|----------|------|
| **id** | Long | PK, Auto Increment | 질환 고유 ID |
| **name** | String(50) | UNIQUE, NOT NULL | 질환명 |
| **category** | String(50) | NOT NULL | 질환 카테고리 |

### 4. UserDisease 엔티티 (user_disease_tb)
| 필드 | 타입 | 제약조건 | 설명 |
|-----|------|----------|------|
| **id** | Integer | PK, Auto Increment | 사용자 질환 ID |
| **userId** | Integer | NOT NULL | 사용자 ID |
| **disease** | Disease | FK, NOT NULL | 질환 참조 |
| **createdAt** | LocalDateTime | NOT NULL | 등록일시 |

**제약조건**: `UNIQUE(user_id, disease_id)` - 사용자별 동일 질환 중복 등록 방지

### 5. 엔티티 관계도
```
FoodAllergy (1) ────────── (N) UserAllergy
     │                         │
     └── id로 연결              └── userId로 User Service 연동

Disease (1) ────────── (N) UserDisease  
     │                         │
     └── id로 연결              └── userId로 User Service 연동
```

---

## 🔄 비즈니스 로직 상세

### 토글 방식 동작 원리

**알레르기 토글 로직**
```java
@Transactional
public UserAllergyToggleResponse toggleAllergy(Integer userId, Long allergyId) {
    // 1. 기존 등록 여부 확인
    UserAllergy existing = userAllergyRepository
        .findByUserIdAndAllergyId(userId, allergyId).orElse(null);
    
    if (existing != null) {
        // 2-A. 이미 등록된 경우 → 삭제 (해제)
        userAllergyRepository.delete(existing);
        return UserAllergyToggleResponse.toDto(existing, false);
    }
    
    // 2-B. 등록되지 않은 경우 → 생성 (등록)
    FoodAllergy allergy = foodAllergyRepository.findById(allergyId)
        .orElseThrow(() -> new NotFoundException("해당 알러지를 찾을 수 없습니다."));
    
    UserAllergy newAllergy = userAllergyRepository.save(
        UserAllergy.builder()
            .userId(userId)
            .allergy(allergy)
            .build());
    
    return UserAllergyToggleResponse.toDto(newAllergy, true);
}
```

**질환 토글 로직**
```java
@Transactional
public UserDiseaseToggleResponse toggleDisease(Integer userId, Long diseaseId) {
    // 알레르기 토글과 동일한 패턴
    // 1. 기존 등록 확인 → 2-A. 삭제 또는 2-B. 생성
}
```

### 데이터 초기화 로직

**ApplicationReadyEvent 활용**
```java
@EventListener(ApplicationReadyEvent.class)
@Transactional
public void loadFoodAllergies() {
    if (isAlreadyLoaded()) return;  // 중복 로딩 방지
    
    // 미리 정의된 26개 알레르기 데이터 로딩
    save("우유");
    save("계란");
    // ... 나머지 24개
}
```

---

## ⚡ 성능 최적화 고려사항

### 데이터베이스 최적화
- **복합 유니크 인덱스**: (user_id, allergy_id), (user_id, disease_id) 조합 인덱스 자동 생성
- **지연 로딩**: @ManyToOne(fetch = FetchType.LAZY) 적용으로 필요시에만 조인
- **페이징 미적용**: 알레르기/질환 목록은 고정된 소량 데이터로 페이징 불필요

### 트랜잭션 최적화
- **읽기 전용**: @Transactional(readOnly = true) 적용으로 조회 성능 향상
- **토글 원자성**: 등록/해제를 하나의 트랜잭션으로 처리하여 데이터 일관성 보장

### 응답 데이터 최적화
- **DTO 변환**: 엔티티 직접 노출 방지, 필요한 필드만 응답
- **JsonProperty**: isRegistered 필드의 boolean getter 메서드 명시적 처리

---

## 🛡️ 보안 및 검증

### 인증/인가
- **Gateway 인증**: JWT 토큰 검증 후 X-USER-ID 헤더 전달
- **사용자 격리**: userId 기반으로 본인 데이터만 접근 가능
- **공개 API**: 전체 알레르기/질환 목록은 인증 불필요

### 데이터 무결성
- **유니크 제약**: 동일 사용자의 중복 등록 방지
- **외래키 제약**: 존재하지 않는 알레르기/질환 ID 참조 방지
- **NOT NULL 제약**: 필수 필드 누락 방지

### 예외 처리
```java
// 존재하지 않는 알레르기 ID 접근
throw new NotFoundException("해당 알러지를 찾을 수 없습니다.");

// 존재하지 않는 질환 ID 접근  
throw new NotFoundException("해당 질환을 찾을 수 없습니다.");
```

---

## 🧪 테스트 가이드

### 알레르기 관리 테스트
```bash
# 1. 전체 알레르기 목록 조회
curl -X GET http://localhost:8080/api/bodies/allergies

# 2. 내 알레르기 목록 조회 (인증 필요)
curl -X GET http://localhost:8080/api/bodies/allergies/my \
  -H "Authorization: Bearer {valid-jwt-token}"

# 3. 우유 알레르기 등록
curl -X POST http://localhost:8080/api/bodies/allergies/1/toggle \
  -H "Authorization: Bearer {valid-jwt-token}"

# 4. 우유 알레르기 해제 (동일 API 재호출)
curl -X POST http://localhost:8080/api/bodies/allergies/1/toggle \
  -H "Authorization: Bearer {valid-jwt-token}"
```

### 질환 관리 테스트
```bash
# 1. 전체 질환 목록 조회
curl -X GET http://localhost:8080/api/bodies/diseases

# 2. 내 질환 목록 조회 (인증 필요)
curl -X GET http://localhost:8080/api/bodies/diseases/my \
  -H "Authorization: Bearer {valid-jwt-token}"

# 3. 고혈압 질환 등록
curl -X POST http://localhost:8080/api/bodies/diseases/1/toggle \
  -H "Authorization: Bearer {valid-jwt-token}"

# 4. 고혈압 질환 해제 (동일 API 재호출)
curl -X POST http://localhost:8080/api/bodies/diseases/1/toggle \
  -H "Authorization: Bearer {valid-jwt-token}"
```

### 토글 동작 검증
```bash
# 1단계: 알레르기 등록 확인
curl -X POST http://localhost:8080/api/bodies/allergies/1/toggle \
  -H "Authorization: Bearer {token}"
# 예상 응답: { "isRegistered": true, "message": "알러지 등록 완료" }

# 2단계: 동일 API 재호출로 해제 확인  
curl -X POST http://localhost:8080/api/bodies/allergies/1/toggle \
  -H "Authorization: Bearer {token}"
# 예상 응답: { "isRegistered": false, "message": "알러지 해제 완료" }

# 3단계: 내 목록에서 변경 확인
curl -X GET http://localhost:8080/api/bodies/allergies/my \
  -H "Authorization: Bearer {token}"
# 예상 응답: 해제된 알레르기는 목록에서 제외됨
```

---

## 📝 주요 특징 및 설계 철학

### 1. 토글 방식 채택 이유
**기존 방식 (등록 + 해제 분리)**
```
POST /api/bodies/allergies/{id}/register    # 등록
DELETE /api/bodies/allergies/{id}/unregister # 해제
```

**현재 방식 (토글 통합)**
```
POST /api/bodies/allergies/{id}/toggle      # 등록/해제 통합
```

**토글 방식의 장점:**
- **UI 편의성**: 사용자가 체크박스 클릭 한 번으로 상태 변경
- **API 단순화**: 하나의 엔드포인트로 두 가지 동작 처리
- **상태 동기화**: 클라이언트와 서버 간 상태 불일치 방지
- **멱등성**: 동일한 요청을 여러 번 호출해도 결과 동일

### 2. 응답 메시지 설계
```json
{
  "allergyId": 1,
  "isRegistered": true,           // 현재 상태 명확히 표시
  "message": "알러지 등록 완료"    // 수행된 동작 명시
}
```

**설계 의도:**
- **명확한 상태**: isRegistered로 현재 등록 상태 명시
- **동작 피드백**: message로 어떤 동작이 수행되었는지 사용자에게 안내
- **일관된 응답**: 알레르기와 질환 API의 동일한 응답 구조

### 3. 데이터 초기화 전략
```java
@EventListener(ApplicationReadyEvent.class)
@Transactional
public void loadFoodAllergies() {
    if (isAlreadyLoaded()) return;  // 중복 방지
    // 미리 정의된 데이터 로딩
}
```

**설계 의도:**
- **애플리케이션 시작 시 자동 로딩**: 별도의 스크립트 실행 불필요
- **중복 방지**: 재시작 시에도 데이터 중복 생성 방지
- **표준 데이터**: 의료진 검토를 거친 표준 알레르기/질환 목록

---

## 🔗 다른 서비스와의 연동

### Nutrition Service 연동
**사용 예시:**
```java
// Nutrition Service에서 식단 추천 시
List<UserAllergyResponse> allergies = healthService.getUserAllergies(userId);
List<UserDiseaseResponse> diseases = healthService.getUserDiseases(userId);

// 알레르기 고려한 식재료 필터링
if (allergies.contains("우유")) {
    excludeIngredients.add("유제품");
}

// 질환 고려한 영양 가이드 적용
if (diseases.contains("당뇨병")) {
    recommendLowSugar = true;
}
if (diseases.contains("고혈압")) {
    recommendLowSodium = true;
}
```

### Exercise Service 연동
**사용 예시:**
```java
// Exercise Service에서 운동 추천 시
List<UserDiseaseResponse> diseases = healthService.getUserDiseases(userId);

// 질환 고려한 운동 제한/권장
if (diseases.contains("심부전")) {
    recommendLightCardio = true;
    avoidHighIntensity = true;
}
if (diseases.contains("골다공증")) {
    recommendWeightBearing = true;
    avoidHighImpact = true;
}
```

---

## 📊 모니터링 및 운영

### 주요 메트릭
| 메트릭명 | 설명 | 중요도 |
|---------|------|--------|
| **알레르기 등록률** | 전체 사용자 중 알레르기 등록 비율 | 높음 |
| **질환 등록률** | 전체 사용자 중 질환 등록 비율 | 높음 |
| **토글 성공률** | 알레르기/질환 토글 API 성공률 | 높음 |
| **응답 시간** | 각 API 엔드포인트별 평균 응답 시간 | 중간 |
| **데이터 정합성** | 유니크 제약 위반 오류 발생률 | 높음 |

### 로깅 전략
```java
// 중요 비즈니스 로직 로깅
log.info("사용자 {}가 알레르기 {} 상태를 {}로 변경", userId, allergyId, 
         isRegistered ? "등록" : "해제");
log.info("사용자 {}가 질환 {} 상태를 {}로 변경", userId, diseaseId, 
         isRegistered ? "등록" : "해제");

// 오류 상황 로깅
log.error("존재하지 않는 알레르기 ID {} 접근 시도 - 사용자: {}", allergyId, userId);
log.error("존재하지 않는 질환 ID {} 접근 시도 - 사용자: {}", diseaseId, userId);
```

### 데이터 품질 관리
**정기 점검 항목:**
- 고아 레코드 확인: UserAllergy/UserDisease에서 참조하는 FoodAllergy/Disease 존재 여부
- 중복 등록 확인: 유니크 제약 위반 케이스 모니터링
- 사용 빈도 분석: 자주 등록되는 알레르기/질환 TOP 10 분석

---

## 🚀 확장 및 개선 방안

### 1. 기능 확장
**알레르기 심각도 추가**
```java
@Entity
public class UserAllergy {
    // 기존 필드들...
    
    @Enumerated(EnumType.STRING)
    private AllergySeverity severity; // MILD, MODERATE, SEVERE
    
    private String symptoms; // 증상 설명
}
```

**질환 상태 관리**
```java
@Entity  
public class UserDisease {
    // 기존 필드들...
    
    private LocalDate diagnosedDate; // 진단일
    private String medication;       // 복용 약물
    private String doctorNote;       // 의사 소견
}
```

### 2. API 개선
**배치 처리 API**
```java
// 여러 알레르기를 한 번에 등록/해제
POST /api/bodies/allergies/batch
{
  "allergyIds": [1, 4, 12],
  "action": "REGISTER" // or "UNREGISTER"
}

// 여러 질환을 한 번에 등록/해제  
POST /api/bodies/diseases/batch
{
  "diseaseIds": [1, 2],
  "action": "REGISTER"
}
```

**검색 API**
```java
// 알레르기 이름으로 검색
GET /api/bodies/allergies/search?keyword=우유

// 질환을 카테고리별로 조회
GET /api/bodies/diseases?category=심혈관계
```

### 3. 성능 최적화
**캐싱 도입**
```java
@Cacheable(value = "allergies", key = "'all'")
public List<FoodAllergyResponse> getAllAllergies() {
    // 전체 알레르기 목록은 변경이 거의 없으므로 캐싱
}

@Cacheable(value = "user-allergies", key = "#userId")  
public List<UserAllergyResponse> getUserAllergies(Integer userId) {
    // 사용자별 알레르기 목록도 자주 조회되므로 캐싱
}
```

**DB 쿼리 최적화**
```java
// N+1 문제 해결을 위한 fetch join
@Query("SELECT ua FROM UserAllergy ua JOIN FETCH ua.allergy WHERE ua.userId = :userId")
List<UserAllergy> findAllByUserIdWithAllergy(@Param("userId") Integer userId);
```

### 4. 데이터 관리 개선
**소프트 삭제 도입**
```java
@Entity
public class UserAllergy {
    // 기존 필드들...
    
    private boolean isDeleted = false;
    private LocalDateTime deletedAt;
}
```

**이력 관리**
```java
@Entity
public class UserAllergyHistory {
    private Integer userId;
    private Long allergyId;
    private String action; // REGISTER, UNREGISTER
    private LocalDateTime actionAt;
}
```

---

## 📚 참고 자료

### 의료 표준 참고
- **식품 알레르기**: 식품의약품안전처 알레르기 유발식품 표시 기준
- **질환 분류**: 한국표준질병·사인분류(KCD-8) 참고
- **카테고리 분류**: 대한의사협회 질환 분류 체계 적용

### 개발 참고 문서
- **Spring Data JPA**: 복합 유니크 제약 설정 방법
- **Jackson Annotation**: @JsonProperty를 활용한 boolean 필드 처리
- **ApplicationReadyEvent**: 애플리케이션 시작 후 데이터 초기화 패턴

---

## 💡 개발 팁 및 주의사항

### 1. Boolean 필드 처리
```java
// isRegistered 필드의 올바른 처리
private final boolean isRegistered = true;

@JsonProperty("isRegistered")
public boolean getIsRegistered() {
    return isRegistered;
}
```
**주의사항**: Jackson에서 boolean 필드명이 is로 시작하면 getter 메서드명을 명시적으로 지정해야 함

### 2. 유니크 제약 활용
```java
@Table(uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "allergy_id"})
})
```
**장점**: 애플리케이션 레벨과 DB 레벨 모두에서 중복 방지 보장

### 3. 토글 로직 구현
```java
// 토글 로직의 핵심 패턴
UserAllergy existing = repository.findByUserIdAndAllergyId(userId, allergyId).orElse(null);

if (existing != null) {
    repository.delete(existing);  // 해제
    return createResponse(existing, false);
} else {
    UserAllergy newEntity = repository.save(createEntity(userId, allergyId)); // 등록
    return createResponse(newEntity, true);
}
```

### 4. 데이터 초기화
```java
@EventListener(ApplicationReadyEvent.class)
@Transactional
public void loadData() {
    if (isAlreadyLoaded()) return;  // 중복 로딩 방지 필수
    
    // 데이터 로딩 로직
}

private boolean isAlreadyLoaded() {
    return repository.count() > 0;  // 간단하고 효율적인 체크
}
```
