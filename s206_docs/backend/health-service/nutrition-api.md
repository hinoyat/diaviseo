# Nutrition API 명세

**Health Service 내 Nutrition 모듈은 음식 정보 관리, 식단 기록, 영양소 분석, 통계 제공을 담당하는 영양 관리 서비스입니다.**

---

## 📌 API 개요

### Base URL
```
http://health-service/api
```

### Health Service 내 Nutrition 모듈의 역할
- **음식 정보 관리**: 음식 데이터베이스 조회, 즐겨찾기 관리
- **음식 세트 관리**: 사용자 정의 음식 조합 생성/관리  
- **식단 기록**: 일별 식사 기록 및 이미지 업로드 (MinIO 연동)
- **영양소 분석**: 일일/주간 영양소 섭취량 실시간 계산
- **통계 제공**: 기간별(일/주/월) 영양 섭취 통계 및 트렌드 분석
- **표준 비교**: 연령/성별 기준 영양 섭취 권장량 및 평균 데이터 제공
- **알림 연동**: RabbitMQ 기반 식사 알림 발송

### 공통 응답 형식
모든 API는 Common Module의 `ResponseDto` 형식을 사용합니다.

```json
{
  "timestamp": "2025-05-26T10:30:00",
  "status": "OK", 
  "message": "성공 메시지",
  "data": { /* 실제 데이터 */ }
}
```

### 공통 HTTP 상태코드
| 상태코드 | 설명 | 응답 예시 |
|---------|------|----------|
| **200** | 성공 | `"message": "요청 성공"` |
| **201** | 생성 성공 | `"message": "식단 등록 성공"` |
| **400** | 잘못된 요청 | `"message": "유효하지 않은 음식 ID입니다."` |
| **401** | 인증 실패 | `"message": "유효하지 않은 토큰입니다."` |
| **404** | 데이터 없음 | `"message": "해당 음식을 찾을 수 없습니다."` |
| **500** | 서버 오류 | `"message": "이미지 업로드 중 오류가 발생했습니다."` |

## 🍎 음식 정보 관리 API

### 1. 음식 전체 목록 조회

사용자별 즐겨찾기 정보를 포함하여 전체 음식 목록을 조회합니다.

#### `GET /api/foods`

**Request Headers**
```http
X-USER-ID: {userId}  # Gateway에서 자동 추가
```

**처리 과정**
1. **전체 음식 조회**: Food 엔티티 전체 조회
2. **즐겨찾기 확인**: 사용자별 즐겨찾기 여부 매핑
3. **목록 응답 생성**: FoodListResponse로 변환

**Response (성공)**
```json
{
  "timestamp": "2025-05-26T10:30:00",
  "status": "OK",
  "message": "음식 전체 조회 성공",
  "data": [
    {
      "foodId": 1,
      "foodName": "백미밥",
      "calorie": 300,
      "baseAmount": "1공기(210g)",
      "isFavorite": true
    },
    {
      "foodId": 2,
      "foodName": "닭가슴살",
      "calorie": 165,
      "baseAmount": "100g",
      "isFavorite": false
    }
  ]
}
```

---

### 2. 음식 상세 정보 조회

특정 음식의 상세 영양 정보를 조회합니다.

#### `GET /api/foods/{foodId}`

**Path Variables**
| 변수 | 타입 | 설명 |
|-----|------|------|
| `foodId` | Integer | 음식 ID |

**Request Headers**
```http
X-USER-ID: {userId}  # Gateway에서 자동 추가
```

**처리 과정**
1. **음식 존재 확인**: foodId로 Food 엔티티 조회
2. **즐겨찾기 확인**: 해당 사용자의 즐겨찾기 여부 확인
3. **상세 정보 반환**: 모든 영양소 정보 포함 응답

**Response (성공)**
```json
{
  "timestamp": "2025-05-26T10:30:00",
  "status": "OK",
  "message": "음식 상세 조회 성공",
  "data": {
    "foodId": 1,
    "foodName": "백미밥",
    "calorie": 300,
    "carbohydrate": 65.2,
    "protein": 6.8,
    "fat": 0.6,
    "sweet": 0.1,
    "sodium": 2.0,
    "saturatedFat": 0.2,
    "transFat": 0.0,
    "cholesterol": 0.0,
    "baseAmount": "1공기(210g)",
    "isFavorite": true,
    "createdAt": "2025-05-26T10:30:00",
    "updatedAt": "2025-05-26T10:30:00"
  }
}
```

**에러 응답**
| 상태코드 | 에러 케이스 | 응답 메시지 |
|---------|-----------|-----------|
| **404** | 음식 없음 | `"해당 음식을 찾을 수 없습니다."` |




## ⭐ 즐겨찾기 관리 API

### 3. 즐겨찾기 토글

음식을 즐겨찾기에 추가하거나 해제합니다.

#### `POST /api/foods/favorites/{foodId}`

**Path Variables**
| 변수 | 타입 | 설명 |
|-----|------|------|
| `foodId` | Integer | 음식 ID |

**Request Headers**
```http
X-USER-ID: {userId}  # Gateway에서 자동 추가
```

**처리 과정**
1. **기존 즐겨찾기 확인**: userId와 foodId로 FavoriteFood 조회
2. **토글 처리**: 
   - 존재하면 삭제 (즐겨찾기 해제)
   - 없으면 생성 (즐겨찾기 등록)
3. **결과 반환**: 토글 후 상태 정보 반환

**Response (즐겨찾기 등록)**
```json
{
  "timestamp": "2025-05-26T10:30:00",
  "status": "OK",
  "message": "즐겨찾기 토글 성공",
  "data": {
    "foodId": 1,
    "isFavorite": true,
    "message": "즐겨찾기 등록됨",
    "toggledAt": "2025-05-26T10:30:00"
  }
}
```

**Response (즐겨찾기 해제)**
```json
{
  "timestamp": "2025-05-26T10:30:00",
  "status": "OK",
  "message": "즐겨찾기 토글 성공",
  "data": {
    "foodId": 1,
    "isFavorite": false,
    "message": "즐겨찾기 해제됨",
    "toggledAt": "2025-05-26T10:30:00"
  }
}
```

**에러 응답**
| 상태코드 | 에러 케이스 | 응답 메시지 |
|---------|-----------|-----------|
| **404** | 음식 없음 | `"해당 음식을 찾을 수 없습니다."` |

---

### 4. 즐겨찾기 목록 조회

사용자가 즐겨찾기로 등록한 음식 목록을 조회합니다.

#### `GET /api/foods/favorites`

**Request Headers**
```http
X-USER-ID: {userId}  # Gateway에서 자동 추가
```

**처리 과정**
1. **즐겨찾기 조회**: userId로 FavoriteFood 엔티티 조회
2. **음식 정보 매핑**: 연관된 Food 엔티티 정보 포함
3. **목록 응답 생성**: FoodDetailResponse로 변환 (isFavorite=true)

**Response (성공)**
```json
{
  "timestamp": "2025-05-26T10:30:00",
  "status": "OK",
  "message": "즐겨찾기 목록 조회 성공",
  "data": [
    {
      "foodId": 1,
      "foodName": "백미밥",
      "calorie": 300,
      "carbohydrate": 65.2,
      "protein": 6.8,
      "fat": 0.6,
      "sweet": 0.1,
      "sodium": 2.0,
      "saturatedFat": 0.2,
      "transFat": 0.0,
      "cholesterol": 0.0,
      "baseAmount": "1공기(210g)",
      "isFavorite": true,
      "createdAt": "2025-05-26T10:30:00",
      "updatedAt": "2025-05-26T10:30:00"
    },
    {
      "foodId": 5,
      "foodName": "계란",
      "calorie": 155,
      "carbohydrate": 1.1,
      "protein": 12.6,
      "fat": 10.6,
      "sweet": 1.1,
      "sodium": 124.0,
      "saturatedFat": 3.3,
      "transFat": 0.0,
      "cholesterol": 373.0,
      "baseAmount": "1개(60g)",
      "isFavorite": true,
      "createdAt": "2025-05-26T10:30:00",
      "updatedAt": "2025-05-26T10:30:00"
    }
  ]
}
```

## 📦 음식 세트 관리 API

### 5. 음식 세트 생성

여러 음식을 조합한 사용자 정의 음식 세트를 생성합니다.

#### `POST /api/food-sets`

**Request Headers**
```http
X-USER-ID: {userId}  # Gateway에서 자동 추가
Content-Type: application/json
```

**Request Body**
```json
{
  "name": "아침식사 세트",
  "foods": [
    {
      "foodId": 1,
      "quantity": 1.0
    },
    {
      "foodId": 5,
      "quantity": 0.5
    }
  ]
}
```

**처리 과정**
1. **음식 유효성 검증**: 각 foodId 존재 여부 확인
2. **FoodSet 엔티티 생성**: 사용자ID와 세트명으로 생성
3. **FoodSetFood 엔티티 생성**: 음식별 수량 정보로 매핑 테이블 생성
4. **총 칼로리 계산**: 각 음식의 칼로리 × 수량 합계
5. **상세 정보 반환**: 생성된 세트의 모든 정보 반환

**Response (성공)**
```json
{
  "timestamp": "2025-05-26T10:30:00",
  "status": "CREATED",
  "message": "음식 세트 등록 성공",
  "data": {
    "foodSetId": 1,
    "name": "아침식사 세트",
    "foods": [
      {
        "foodId": 1,
        "foodName": "백미밥",
        "quantity": 1.0,
        "carbohydrate": 65.2,
        "protein": 6.8,
        "fat": 0.6,
        "sweet": 0.1,
        "sodium": 2.0,
        "baseAmount": "1공기(210g)",
        "calorie": 300
      },
      {
        "foodId": 5,
        "foodName": "계란",
        "quantity": 0.5,
        "carbohydrate": 0.55,
        "protein": 6.3,
        "fat": 5.3,
        "sweet": 0.55,
        "sodium": 62.0,
        "baseAmount": "1개(60g)",
        "calorie": 78
      }
    ],
    "totalCalories": 378
  }
}
```

**에러 응답**
| 상태코드 | 에러 케이스 | 응답 메시지 |
|---------|-----------|-----------|
| **400** | 유효하지 않은 음식 ID | `"유효하지 않은 음식 ID입니다: {foodId}"` |

---

### 6. 음식 세트 상세 조회

특정 음식 세트의 상세 정보를 조회합니다.

#### `GET /api/food-sets/{id}`

**Path Variables**
| 변수 | 타입 | 설명 |
|-----|------|------|
| `id` | Integer | 음식 세트 ID |

**Request Headers**
```http
X-USER-ID: {userId}  # Gateway에서 자동 추가
```

**처리 과정**
1. **세트 존재 확인**: foodSetId로 FoodSet 조회
2. **소유권 확인**: 세트 소유자와 요청 사용자 일치 여부 확인
3. **상세 정보 반환**: 포함된 음식들과 총 칼로리 계산하여 반환

**Response (성공)**
```json
{
  "timestamp": "2025-05-26T10:30:00",
  "status": "OK",
  "message": "음식 세트 상세 조회 성공",
  "data": {
    "foodSetId": 1,
    "name": "아침식사 세트",
    "foods": [
      {
        "foodId": 1,
        "foodName": "백미밥",
        "quantity": 1.0,
        "carbohydrate": 65.2,
        "protein": 6.8,
        "fat": 0.6,
        "sweet": 0.1,
        "sodium": 2.0,
        "baseAmount": "1공기(210g)",
        "calorie": 300
      },
      {
        "foodId": 5,
        "foodName": "계란",
        "quantity": 0.5,
        "carbohydrate": 0.55,
        "protein": 6.3,
        "fat": 5.3,
        "sweet": 0.55,
        "sodium": 62.0,
        "baseAmount": "1개(60g)",
        "calorie": 78
      }
    ],
    "totalCalories": 378
  }
}
```

**에러 응답**
| 상태코드 | 에러 케이스 | 응답 메시지 |
|---------|-----------|-----------|
| **404** | 세트 없음 | `"음식 세트를 찾을 수 없습니다."` |
| **401** | 권한 없음 | `"세트에 접근할 권한이 없습니다."` |

---

### 7. 음식 세트 목록 조회

사용자가 생성한 모든 음식 세트 목록을 조회합니다.

#### `GET /api/food-sets`

**Request Headers**
```http
X-USER-ID: {userId}  # Gateway에서 자동 추가
```

**처리 과정**
1. **사용자 세트 조회**: userId로 FoodSet 필터링
2. **목록 정보 생성**: 각 세트별 음식 개수, 총 칼로리 계산
3. **목록 응답 반환**: FoodSetDetailResponse 목록으로 반환

**Response (성공)**
```json
{
  "timestamp": "2025-05-26T10:30:00",
  "status": "OK",
  "message": "음식 세트 목록 조회 성공",
  "data": [
    {
      "foodSetId": 1,
      "name": "아침식사 세트",
      "foods": [
        {
          "foodId": 1,
          "foodName": "백미밥",
          "quantity": 1.0,
          "carbohydrate": 65.2,
          "protein": 6.8,
          "fat": 0.6,
          "sweet": 0.1,
          "sodium": 2.0,
          "baseAmount": "1공기(210g)",
          "calorie": 300
        }
      ],
      "totalCalories": 378
    },
    {
      "foodSetId": 2,
      "name": "점심식사 세트",
      "foods": [
        {
          "foodId": 2,
          "foodName": "닭가슴살",
          "quantity": 1.0,
          "carbohydrate": 0.0,
          "protein": 31.0,
          "fat": 3.6,
          "sweet": 0.0,
          "sodium": 74.0,
          "baseAmount": "100g",
          "calorie": 165
        }
      ],
      "totalCalories": 165
    }
  ]
}
```

---

### 8. 음식 세트 수정

기존 음식 세트의 이름 및 포함 음식을 수정합니다.

#### `PUT /api/food-sets/{id}`

**Path Variables**
| 변수 | 타입 | 설명 |
|-----|------|------|
| `id` | Integer | 음식 세트 ID |

**Request Headers**
```http
X-USER-ID: {userId}  # Gateway에서 자동 추가
Content-Type: application/json
```

**Request Body**
```json
{
  "name": "수정된 아침식사 세트",
  "foods": [
    {
      "foodId": 1,
      "quantity": 1.5
    },
    {
      "foodId": 3,
      "quantity": 1.0
    }
  ]
}
```

**처리 과정**
1. **세트 존재 및 소유권 확인**: findOwnedFoodSet 메서드로 검증
2. **기존 음식 목록 초기화**: foodSetFoods.clear() 실행
3. **세트명 업데이트**: updateName 메서드 호출
4. **새 음식 목록 생성**: 요청 음식들로 FoodSetFood 엔티티 재생성
5. **저장 및 응답**: 수정된 세트 정보 반환

**Response (성공)**
```json
{
  "timestamp": "2025-05-26T10:30:00",
  "status": "OK",
  "message": "음식 세트 수정 성공",
  "data": {
    "foodSetId": 1,
    "name": "수정된 아침식사 세트",
    "foods": [
      {
        "foodId": 1,
        "foodName": "백미밥",
        "quantity": 1.5,
        "carbohydrate": 97.8,
        "protein": 10.2,
        "fat": 0.9,
        "sweet": 0.15,
        "sodium": 3.0,
        "baseAmount": "1공기(210g)",
        "calorie": 450
      },
      {
        "foodId": 3,
        "foodName": "현미밥",
        "quantity": 1.0,
        "carbohydrate": 60.0,
        "protein": 7.5,
        "fat": 2.3,
        "sweet": 0.5,
        "sodium": 1.5,
        "baseAmount": "1공기(210g)",
        "calorie": 285
      }
    ],
    "totalCalories": 735
  }
}
```

**에러 응답**
| 상태코드 | 에러 케이스 | 응답 메시지 |
|---------|-----------|-----------|
| **400** | 유효하지 않은 음식 ID | `"유효하지 않은 음식 ID입니다: {foodId}"` |
| **404** | 세트 없음 | `"음식 세트를 찾을 수 없습니다."` |
| **401** | 권한 없음 | `"세트에 접근할 권한이 없습니다."` |

---

### 9. 음식 세트 삭제

기존 음식 세트를 삭제합니다.

#### `DELETE /api/food-sets/{id}`

**Path Variables**
| 변수 | 타입 | 설명 |
|-----|------|------|
| `id` | Integer | 음식 세트 ID |

**Request Headers**
```http
X-USER-ID: {userId}  # Gateway에서 자동 추가
```

**처리 과정**
1. **세트 존재 및 소유권 확인**: findOwnedFoodSet 메서드로 검증
2. **물리적 삭제**: JPA repository.delete() 실행
3. **연관 데이터 자동 삭제**: orphanRemoval=true로 FoodSetFood 자동 삭제

**Response (성공)**
```json
{
  "timestamp": "2025-05-26T12:00:00",
  "status": "OK",
  "message": "음식 세트 삭제 성공",
  "data": null
}
```

**에러 응답**
| 상태코드 | 에러 케이스 | 응답 메시지 |
|---------|-----------|-----------|
| **404** | 세트 없음 | `"음식 세트를 찾을 수 없습니다."` |
| **401** | 권한 없음 | `"세트에 접근할 권한이 없습니다."` |

## 🍽️ 식단 기록 관리 API - Part 1&2 (기본 CRUD & 영양소 분석)

### 10. 식단 등록/수정 (이미지 포함)

날짜 기반으로 식단을 등록하거나 수정하며, 시간대별 이미지를 함께 업로드할 수 있습니다.

#### `POST /api/meals` (multipart/form-data)

**Request Headers**
```http
X-USER-ID: {userId}  # Gateway에서 자동 추가
Content-Type: multipart/form-data
```

**Request Parts**
```
mealData (JSON): 식단 정보
images (Files): 시간대별 이미지 파일들 (선택)
```

**Request Body (mealData JSON)**
```json
{
  "mealDate": "2025-05-26",
  "isMeal": true,
  "mealTimes": [
    {
      "mealType": "BREAKFAST",
      "eatingTime": "08:00:00",
      "foods": [
        {
          "foodId": 1,
          "quantity": 1.0,
          "foodImageUrl": null
        }
      ],
      "mealTimeImageUrl": null
    }
  ]
}
```

**처리 과정**
1. **이미지 업로드**: MultipartFile을 MinIO에 업로드
2. **기존 식단 확인**: mealDate로 기존 Meal 존재 여부 확인
3. **식단 생성/업데이트**: 
   - 없으면 새 Meal 생성
   - 있으면 기존 Meal 업데이트
4. **시간대별 처리**: MealTime 생성/수정 및 MealFood 매핑
5. **이미지 URL 매핑**: 업로드된 이미지를 해당 시간대에 연결
6. **응답 생성**: 완전한 URL로 변환하여 반환

**Response (성공)**
```json
{
  "timestamp": "2025-05-26T10:30:00",
  "status": "CREATED",
  "message": "식단 및 이미지 등록/수정 성공",
  "data": {
    "mealId": 1,
    "mealDate": "2025-05-26",
    "isMeal": true,
    "mealTimes": [
      {
        "mealTimeId": 1,
        "mealType": "BREAKFAST",
        "eatingTime": "08:00:00",
        "foods": [
          {
            "mealFoodId": 1,
            "foodId": 1,
            "foodName": "백미밥",
            "calorie": 300,
            "carbohydrate": 65.2,
            "protein": 6.8,
            "fat": 0.6,
            "sugar": 0.1,
            "sodium": 2.0,
            "quantity": 1.0,
            "foodImageUrl": "http://minio:9000/meal-images/meal/uuid-1.jpg",
            "totalCalorie": 300,
            "totalCarbohydrate": 65.2,
            "totalProtein": 6.8,
            "totalFat": 0.6,
            "totalSugar": 0.1,
            "totalSodium": 2.0
          }
        ],
        "mealTimeImageUrl": "http://minio:9000/meal-images/meal/uuid-2.jpg",
        "createdAt": "2025-05-26T10:30:00",
        "updatedAt": "2025-05-26T10:30:00"
      }
    ],
    "createdAt": "2025-05-26T10:30:00",
    "updatedAt": "2025-05-26T10:30:00"
  }
}
```

**에러 응답**
| 상태코드 | 에러 케이스 | 응답 메시지 |
|---------|-----------|-----------|
| **400** | 유효하지 않은 음식 ID | `"유효하지 않은 음식 ID입니다: {foodId}"` |
| **500** | 이미지 업로드 실패 | `"이미지 업로드 중 오류가 발생했습니다."` |

---

### 11. 식단 상세 조회

mealId를 통해 특정 식단의 상세 정보를 조회합니다.

#### `GET /api/meals/{mealId}`

**Path Variables**
| 변수 | 타입 | 설명 |
|-----|------|------|
| `mealId` | Integer | 식단 ID |

**Request Headers**
```http
X-USER-ID: {userId}  # Gateway에서 자동 추가
```

**처리 과정**
1. **식단 존재 및 소유권 확인**: findOwnedMeal 메서드로 검증
2. **이미지 URL 처리**: MinIO ObjectName을 전체 URL로 변환
3. **상세 정보 반환**: 시간대별 음식 및 이미지 정보 포함

**Response (성공)**
```json
{
  "timestamp": "2025-05-26T15:00:00",
  "status": "OK",  
  "message": "식단 상세 조회 성공",
  "data": {
    "mealId": 1,
    "mealDate": "2025-05-26",
    "isMeal": true,
    "mealTimes": [
      {
        "mealTimeId": 1,
        "mealType": "BREAKFAST",
        "eatingTime": "08:00:00",
        "foods": [
          {
            "mealFoodId": 1,
            "foodId": 1,
            "foodName": "백미밥",
            "calorie": 300,
            "carbohydrate": 65.2,
            "protein": 6.8,
            "fat": 0.6,
            "sugar": 0.1,
            "sodium": 2.0,
            "quantity": 1.0,
            "foodImageUrl": "http://minio:9000/meal-images/meal/uuid-1.jpg",
            "totalCalorie": 300,
            "totalCarbohydrate": 65.2,
            "totalProtein": 6.8,
            "totalFat": 0.6,
            "totalSugar": 0.1,
            "totalSodium": 2.0
          }
        ],
        "mealTimeImageUrl": "http://minio:9000/meal-images/meal/uuid-2.jpg",
        "createdAt": "2025-05-26T08:30:00",
        "updatedAt": "2025-05-26T10:30:00"
      }
    ],
    "createdAt": "2025-05-26T08:30:00",
    "updatedAt": "2025-05-26T10:30:00"
  }
}
```

**에러 응답**
| 상태코드 | 에러 케이스 | 응답 메시지 |
|---------|-----------|-----------|
| **404** | 식단 없음 | `"식단을 찾을 수 없습니다."` |
| **401** | 권한 없음 | `"이 식단에 접근할 권한이 없습니다."` |
| **400** | 삭제된 식단 | `"이미 삭제된 식단입니다."` |

---

### 12. 당일 식단 조회

오늘 날짜의 식단을 영양소 정보와 함께 조회합니다.

#### `GET /api/meals/today`

**Request Headers**
```http
X-USER-ID: {userId}  # Gateway에서 자동 추가
```

**처리 과정**
1. **오늘 날짜 설정**: LocalDate.now() 사용
2. **날짜별 식단 조회**: getMealByDate 메서드 호출
3. **영양소 계산**: 시간대별/전체 영양소 합계 계산
4. **이미지 URL 처리**: MinIO URL 변환

**Response (성공)**
```json
{
  "timestamp": "2025-05-26T15:00:00",
  "status": "OK",
  "message": "당일 식단 조회 성공",
  "data": {
    "mealId": 1,
    "mealDate": "2025-05-26",
    "isMeal": true,
    "totalNutrition": {
      "totalCalorie": 1800,
      "totalCarbohydrate": 250.5,
      "totalProtein": 85.2,
      "totalFat": 65.8,
      "totalSugar": 45.3,
      "totalSodium": 2500.0
    },
    "mealTimes": [
      {
        "mealTimeId": 1,
        "mealType": "BREAKFAST",
        "eatingTime": "08:00:00",
        "foods": [
          {
            "mealFoodId": 1,
            "foodId": 1,
            "foodName": "백미밥",
            "calorie": 300,
            "carbohydrate": 65.2,
            "protein": 6.8,
            "fat": 0.6,
            "sugar": 0.1,
            "sodium": 2.0,
            "quantity": 1.0,
            "foodImageUrl": "http://minio:9000/meal-images/meal/uuid-1.jpg",
            "totalCalorie": 300,
            "totalCarbohydrate": 65.2,
            "totalProtein": 6.8,
            "totalFat": 0.6,
            "totalSugar": 0.1,
            "totalSodium": 2.0
          }
        ],
        "nutrition": {
          "totalCalorie": 600,
          "totalCarbohydrate": 85.5,
          "totalProtein": 25.2,
          "totalFat": 15.8,
          "totalSugar": 12.3,
          "totalSodium": 800.0
        },
        "mealTimeImageUrl": "http://minio:9000/meal-images/meal/uuid-2.jpg",
        "createdAt": "2025-05-26T08:30:00",
        "updatedAt": "2025-05-26T10:30:00"
      }
    ],
    "createdAt": "2025-05-26T08:30:00",
    "updatedAt": "2025-05-26T10:30:00"
  }
}
```

---

### 16. 날짜별 식단 조회

특정 날짜의 식단을 영양소 정보와 함께 조회합니다.

#### `GET /api/meals/date/{date}`

**Path Variables**
| 변수 | 타입 | 설명 |
|-----|------|------|
| `date` | LocalDate | 조회할 날짜 (YYYY-MM-DD) |

**Request Headers**
```http
X-USER-ID: {userId}  # Gateway에서 자동 추가
```

**처리 과정**
1. **날짜별 식단 조회**: userId와 date로 Meal 엔티티 조회
2. **영양소 계산**: 전체 및 시간대별 영양소 합계 계산
3. **빈 데이터 처리**: 해당 날짜 식단이 없으면 빈 응답 반환
4. **이미지 URL 처리**: MinIO ObjectName을 전체 URL로 변환

**Response (성공)**
```json
{
  "timestamp": "2025-05-26T19:00:00",
  "status": "OK",
  "message": "날짜별 식단 조회 성공",
  "data": {
    "mealId": 1,
    "mealDate": "2025-05-26",
    "isMeal": true,
    "totalNutrition": {
      "totalCalorie": 1800,
      "totalCarbohydrate": 250.5,
      "totalProtein": 85.2,
      "totalFat": 65.8,
      "totalSugar": 45.3,
      "totalSodium": 2500.0
    },
    "mealTimes": [
      {
        "mealTimeId": 1,
        "mealType": "BREAKFAST",
        "eatingTime": "08:00:00",
        "foods": [
          {
            "mealFoodId": 1,
            "foodId": 1,
            "foodName": "백미밥",
            "calorie": 300,
            "carbohydrate": 65.2,
            "protein": 6.8,
            "fat": 0.6,
            "sugar": 0.1,
            "sodium": 2.0,
            "quantity": 1.0,
            "foodImageUrl": "http://minio:9000/meal-images/meal/uuid-1.jpg",
            "totalCalorie": 300,
            "totalCarbohydrate": 65.2,
            "totalProtein": 6.8,
            "totalFat": 0.6,
            "totalSugar": 0.1,
            "totalSodium": 2.0
          }
        ],
        "nutrition": {
          "totalCalorie": 600,
          "totalCarbohydrate": 85.5,
          "totalProtein": 25.2,
          "totalFat": 15.8,
          "totalSugar": 12.3,
          "totalSodium": 800.0
        },
        "mealTimeImageUrl": "http://minio:9000/meal-images/meal/uuid-2.jpg",
        "createdAt": "2025-05-26T08:30:00",
        "updatedAt": "2025-05-26T10:30:00"
      }
    ],
    "createdAt": "2025-05-26T08:30:00",
    "updatedAt": "2025-05-26T10:30:00"
  }
}
```

**Response (데이터 없음)**
```json
{
  "timestamp": "2025-05-26T19:00:00",
  "status": "OK",
  "message": "날짜별 식단 조회 성공",
  "data": {
    "mealDate": "2025-05-26",
    "isMeal": false,
    "totalNutrition": {
      "totalCalorie": 0,
      "totalCarbohydrate": 0.0,
      "totalProtein": 0.0,
      "totalFat": 0.0,
      "totalSugar": 0.0,
      "totalSodium": 0.0
    },
    "mealTimes": []
  }
}
```

---

### 13. 일일 영양정보 조회

특정 날짜의 총 영양소 섭취량을 조회합니다.

#### `GET /api/meals/daily-nutrition`

**Request Headers**
```http
X-USER-ID: {userId}  # Gateway에서 자동 추가
```

**Query Parameters**
| 파라미터 | 타입 | 필수 | 기본값 | 설명 |
|---------|------|------|-------|------|
| `date` | LocalDate | ❌ | 오늘 | 조회할 날짜 (YYYY-MM-DD) |

**예시 요청**
```http
GET /api/meals/daily-nutrition?date=2025-05-26
```

**처리 과정**
1. **날짜 설정**: date 파라미터가 없으면 오늘 날짜 사용
2. **영양소 계산**: Repository 쿼리로 해당 날짜 총 영양소 합계 계산
3. **안전한 타입 변환**: toBigDecimal 메서드로 안전한 데이터 변환
4. **결과 반환**: DailyNutritionResponse 객체로 반환

**Response (성공)**
```json
{
  "timestamp": "2025-05-26T16:00:00",
  "status": "OK",
  "message": "일일 영양정보 조회 성공",
  "data": {
    "date": "2025-05-26",
    "totalCalorie": 1800,
    "totalCarbohydrate": 250.5,
    "totalProtein": 85.2,
    "totalFat": 65.8,
    "totalSugar": 45.3,
    "totalSodium": 2500.0
  }
}
```

**Response (데이터 없음)**
```json
{
  "timestamp": "2025-05-26T16:00:00",
  "status": "OK",
  "message": "일일 영양정보 조회 성공",
  "data": {
    "date": "2025-05-26",
    "totalCalorie": 0,
    "totalCarbohydrate": 0.0,
    "totalProtein": 0.0,
    "totalFat": 0.0,
    "totalSugar": 0.0,
    "totalSodium": 0.0
  }
}
```

---

### 14. 주간 영양정보 조회

특정 종료일로부터 7일간의 일별 영양소 섭취량을 조회합니다.

#### `GET /api/meals/weekly-nutrition`

**Request Headers**
```http
X-USER-ID: {userId}  # Gateway에서 자동 추가
```

**Query Parameters**
| 파라미터 | 타입 | 필수 | 기본값 | 설명 |
|---------|------|------|-------|------|
| `endDate` | LocalDate | ❌ | 오늘 | 종료 날짜 (YYYY-MM-DD) |

**예시 요청**
```http
GET /api/meals/weekly-nutrition?endDate=2025-05-26
```

**처리 과정**
1. **기간 계산**: endDate로부터 6일 전까지 (총 7일)
2. **주간 데이터 조회**: Repository 쿼리로 날짜별 영양소 합계 조회
3. **빈 날짜 처리**: 데이터가 없는 날짜는 0값으로 채움
4. **날짜순 정렬**: 시작일부터 종료일까지 순서대로 정렬

**Response (성공)**
```json
{
  "timestamp": "2025-05-26T17:00:00",
  "status": "OK",
  "message": "주간 영양정보 조회 성공",
  "data": {
    "startDate": "2025-05-20",
    "endDate": "2025-05-26",
    "dailyNutritions": [
      {
        "date": "2025-05-20",
        "totalCalorie": 1650,
        "totalCarbohydrate": 220.3,
        "totalProtein": 75.8,
        "totalFat": 55.2,
        "totalSugar": 38.5,
        "totalSodium": 2200.0
      },
      {
        "date": "2025-05-21",
        "totalCalorie": 0,
        "totalCarbohydrate": 0.0,
        "totalProtein": 0.0,
        "totalFat": 0.0,
        "totalSugar": 0.0,
        "totalSodium": 0.0
      },
      {
        "date": "2025-05-22",
        "totalCalorie": 1850,
        "totalCarbohydrate": 260.0,
        "totalProtein": 90.5,
        "totalFat": 68.2,
        "totalSugar": 48.1,
        "totalSodium": 2650.0
      }
    ]
  }
}
```

## 🍽️ 식단 기록 관리 API - Part 3&4 (삭제/수정 & 이미지/기타)

### 15. 식단 삭제

mealId를 통해 특정 식단을 soft delete 처리합니다.

#### `DELETE /api/meals/{mealId}`

**Path Variables**
| 변수 | 타입 | 설명 |
|-----|------|------|
| `mealId` | Integer | 식단 ID |

**Request Headers**
```http
X-USER-ID: {userId}  # Gateway에서 자동 추가
```

**처리 과정**
1. **식단 존재 및 소유권 확인**: findOwnedMeal 메서드로 검증
2. **Soft Delete 처리**: isDeleted = true, deletedAt = 현재시간 설정
3. **저장**: 변경사항 데이터베이스 반영

**Response (성공)**
```json
{
  "timestamp": "2025-05-26T18:00:00",
  "status": "OK",
  "message": "식단 삭제 성공",
  "data": null
}
```

**에러 응답**
| 상태코드 | 에러 케이스 | 응답 메시지 |
|---------|-----------|-----------|
| **404** | 식단 없음 | `"식단을 찾을 수 없습니다."` |
| **401** | 권한 없음 | `"이 식단에 접근할 권한이 없습니다."` |
| **400** | 이미 삭제됨 | `"이미 삭제된 식단입니다."` |

---

### 17. 날짜별 식단 삭제

특정 날짜의 전체 식단을 soft delete 처리합니다.

#### `DELETE /api/meals/date/{date}`

**Path Variables**
| 변수 | 타입 | 설명 |
|-----|------|------|
| `date` | LocalDate | 삭제할 날짜 (YYYY-MM-DD) |

**Request Headers**
```http
X-USER-ID: {userId}  # Gateway에서 자동 추가
```

**처리 과정**
1. **날짜별 식단 조회**: userId와 date로 Meal 조회
2. **Soft Delete 처리**: meal.delete() 메서드 호출
3. **저장**: 변경사항 데이터베이스 반영

**Response (성공)**
```json
{
  "timestamp": "2025-05-26T20:00:00",
  "status": "OK",
  "message": "날짜별 식단 삭제 성공",
  "data": null
}
```

**에러 응답**
| 상태코드 | 에러 케이스 | 응답 메시지 |
|---------|-----------|-----------|
| **404** | 식단 없음 | `"해당 날짜의 식단을 찾을 수 없습니다."` |

---

### 18. 시간대별 식단 삭제

특정 날짜의 특정 시간대(아침/점심/저녁/간식) 식단을 삭제합니다.

#### `DELETE /api/meals/date/{date}/type/{mealType}`

**Path Variables**
| 변수 | 타입 | 설명 |
|-----|------|------|
| `date` | LocalDate | 삭제할 날짜 (YYYY-MM-DD) |
| `mealType` | MealType | 식사 시간대 (BREAKFAST/LUNCH/DINNER/SNACK) |

**Request Headers**
```http
X-USER-ID: {userId}  # Gateway에서 자동 추가
```

**처리 과정**
1. **날짜별 식단 조회**: userId와 date로 Meal 조회
2. **해당 시간대 MealTime 찾기**: mealType과 일치하는 MealTime 필터링
3. **Soft Delete 처리**: mealTime.delete() 메서드 호출
4. **저장**: 변경사항 데이터베이스 반영

**Response (성공)**
```json
{
  "timestamp": "2025-05-26T21:00:00",
  "status": "OK",
  "message": "시간대별 식단 삭제 성공",
  "data": null
}
```

**에러 응답**
| 상태코드 | 에러 케이스 | 응답 메시지 |
|---------|-----------|-----------|
| **404** | 식단 없음 | `"해당 날짜의 식단을 찾을 수 없습니다."` |

---

### 19. 개별 음식 삭제

식단에서 특정 음식 하나만 삭제합니다.

#### `DELETE /api/meals/food/{mealFoodId}`

**Path Variables**
| 변수 | 타입 | 설명 |
|-----|------|------|
| `mealFoodId` | Integer | 식단 음식 ID |

**Request Headers**
```http
X-USER-ID: {userId}  # Gateway에서 자동 추가
```

**처리 과정**
1. **MealFood 조회**: mealFoodId로 MealFood 엔티티 조회
2. **소유권 확인**: MealFood → MealTime → Meal → userId 경로로 소유권 검증
3. **이미지 삭제**: 해당 음식에 연결된 이미지가 있으면 MinIO에서 삭제
4. **MealFood 삭제**: 물리적 삭제 수행
5. **연관관계 정리**: MealTime에서 해당 MealFood 제거

**Response (성공)**
```json
{
  "timestamp": "2025-05-26T22:00:00",
  "status": "OK",
  "message": "음식 삭제 성공",
  "data": null
}
```

**에러 응답**
| 상태코드 | 에러 케이스 | 응답 메시지 |
|---------|-----------|-----------|
| **404** | 음식 없음 | `"해당 음식을 찾을 수 없습니다."` |
| **401** | 권한 없음 | `"이 음식에 접근할 권한이 없습니다."` |

---

### 20. 시간대별 식단 수정

특정 날짜의 특정 시간대 식단만 수정합니다.

#### `PUT /api/meals/date/{date}/type/{mealType}`

**Path Variables**
| 변수 | 타입 | 설명 |
|-----|------|------|
| `date` | LocalDate | 수정할 날짜 (YYYY-MM-DD) |
| `mealType` | MealType | 식사 시간대 (BREAKFAST/LUNCH/DINNER/SNACK) |

**Request Headers**
```http
X-USER-ID: {userId}  # Gateway에서 자동 추가
Content-Type: application/json
```

**Request Body**
```json
{
  "mealType": "BREAKFAST",
  "eatingTime": "08:30:00",
  "foods": [
    {
      "foodId": 1,
      "quantity": 1.5,
      "foodImageUrl": null
    }
  ],
  "mealTimeImageUrl": null
}
```

**처리 과정**
1. **기존 식단 확인**: 해당 날짜에 Meal이 없으면 새로 생성
2. **해당 시간대 MealTime 찾기**: mealType으로 기존 MealTime 조회
3. **음식 목록 초기화**: 기존 MealFood 목록 clear()
4. **새 음식 추가**: 요청의 foods로 새 MealFood 생성
5. **시간 정보 업데이트**: eatingTime 업데이트
6. **결과 반환**: MealTimeResponse로 변환하여 반환

**Response (성공)**
```json
{
  "timestamp": "2025-05-26T23:00:00",
  "status": "OK",
  "message": "시간대별 식단 수정 성공",
  "data": {
    "mealTimeId": 1,
    "mealType": "BREAKFAST",
    "eatingTime": "08:30:00",
    "foods": [
      {
        "mealFoodId": 2,
        "foodId": 1,
        "foodName": "백미밥",
        "calorie": 300,
        "carbohydrate": 65.2,
        "protein": 6.8,
        "fat": 0.6,
        "sugar": 0.1,
        "sodium": 2.0,
        "quantity": 1.5,
        "foodImageUrl": null,
        "totalCalorie": 450,
        "totalCarbohydrate": 97.8,
        "totalProtein": 10.2,
        "totalFat": 0.9,
        "totalSugar": 0.15,
        "totalSodium": 3.0
      }
    ],
    "mealTimeImageUrl": null,
    "createdAt": "2025-05-26T08:30:00",
    "updatedAt": "2025-05-26T23:00:00"
  }
}
```

**에러 응답**
| 상태코드 | 에러 케이스 | 응답 메시지 |
|---------|-----------|-----------|
| **400** | 유효하지 않은 음식 ID | `"유효하지 않은 음식 ID입니다: {foodId}"` |
| **500** | 내부 서버 오류 | `"식단 생성 후 해당 시간대를 찾을 수 없습니다."` |

---

### 21. 음식 이미지 업로드

특정 음식에 이미지를 업로드하고 연결합니다.

#### `POST /api/meals/food/{mealFoodId}/image` (multipart/form-data)

**Path Variables**
| 변수 | 타입 | 설명 |
|-----|------|------|
| `mealFoodId` | Integer | 식단 음식 ID |

**Request Headers**
```http
X-USER-ID: {userId}  # Gateway에서 자동 추가
Content-Type: multipart/form-data
```

**Request Parameters**
```
image (File): 업로드할 이미지 파일
```

**처리 과정**
1. **MealFood 조회 및 소유권 확인**: mealFoodId로 조회 후 소유권 검증
2. **기존 이미지 삭제**: 기존에 연결된 이미지가 있으면 MinIO에서 삭제
3. **새 이미지 업로드**: MultipartFile을 MinIO에 업로드
4. **이미지 URL 업데이트**: MealFood 엔티티에 ObjectName 저장
5. **접근 URL 생성**: MinIO URL 생성하여 응답

**Response (성공)**
```json
{
  "timestamp": "2025-05-26T23:30:00",
  "status": "CREATED",
  "message": "음식 이미지 업로드 성공",
  "data": {
    "objectName": "meal/uuid-12345.jpg",
    "imageUrl": "http://minio:9000/meal-images/meal/uuid-12345.jpg"
  }
}
```

**에러 응답**
| 상태코드 | 에러 케이스 | 응답 메시지 |
|---------|-----------|-----------|
| **404** | 음식 없음 | `"해당 음식을 찾을 수 없습니다."` |
| **401** | 권한 없음 | `"이 음식에 접근할 권한이 없습니다."` |
| **500** | 업로드 실패 | `"이미지 업로드 중 오류가 발생했습니다."` |

---

### 22. 최근 먹은 음식 조회

사용자가 최근에 기록한 음식들을 조회합니다.

#### `GET /api/meals/recent-foods`

**Request Headers**
```http
X-USER-ID: {userId}  # Gateway에서 자동 추가
```

**처리 과정**
1. **최근 음식 조회**: MealFood에서 최근 등록된 음식 10개 조회 (중복 제거)
2. **즐겨찾기 확인**: 각 음식별 사용자 즐겨찾기 여부 확인
3. **상세 정보 매핑**: FoodDetailResponse로 변환
4. **결과 반환**: 최근 순서대로 정렬된 음식 목록

**Response (성공)**
```json
{
  "timestamp": "2025-05-27T00:00:00",
  "status": "OK",
  "message": "최근 먹은 음식 조회 성공",
  "data": [
    {
      "foodId": 1,
      "foodName": "백미밥",
      "calorie": 300,
      "carbohydrate": 65.2,
      "protein": 6.8,
      "fat": 0.6,
      "sweet": 0.1,
      "sodium": 2.0,
      "saturatedFat": 0.2,
      "transFat": 0.0,
      "cholesterol": 0.0,
      "baseAmount": "1공기(210g)",
      "isFavorite": true,
      "createdAt": "2025-05-26T10:30:00",
      "updatedAt": "2025-05-26T10:30:00"
    },
    {
      "foodId": 5,
      "foodName": "계란",
      "calorie": 155,
      "carbohydrate": 1.1,
      "protein": 12.6,
      "fat": 10.6,
      "sweet": 1.1,
      "sodium": 124.0,
      "saturatedFat": 3.3,
      "transFat": 0.0,
      "cholesterol": 373.0,
      "baseAmount": "1개(60g)",
      "isFavorite": false,
      "createdAt": "2025-05-26T10:30:00",
      "updatedAt": "2025-05-26T10:30:00"
    }
  ]
}
```

**Response (데이터 없음)**
```json
{
  "timestamp": "2025-05-27T00:00:00",
  "status": "OK",
  "message": "최근 먹은 음식 조회 성공",
  "data": []
}
```


## 📸 이미지 관리 API

### 23. 식단 이미지 업로드

이미지 파일을 MinIO에 업로드하고 Object Name과 접근 URL을 반환합니다.

#### `POST /api/meals/images/upload` (multipart/form-data)

**Request Headers**
```http
X-USER-ID: {userId}  # Gateway에서 자동 추가
Content-Type: multipart/form-data
```

**Request Parameters**
```
image (File): 업로드할 이미지 파일
```

**처리 과정**
1. **버킷 존재 확인**: MinIO 버킷 존재 여부 확인 후 필요시 생성
2. **파일명 생성**: UUID + 원본 확장자로 유니크한 파일명 생성
3. **MIME 타입 결정**: 확장자별 적절한 Content-Type 설정
4. **MinIO 업로드**: PutObject로 파일 업로드
5. **URL 생성**: 외부 접근 가능한 URL 생성

**Response (성공)**
```json
{
  "timestamp": "2025-05-27T01:00:00",
  "status": "CREATED",
  "message": "식단 이미지 업로드 성공",
  "data": {
    "objectName": "meal/uuid-67890.jpg",
    "imageUrl": "http://minio:9000/meal-images/meal/uuid-67890.jpg"
  }
}
```

**에러 응답**
| 상태코드 | 에러 케이스 | 응답 메시지 |
|---------|-----------|-----------|
| **500** | 버킷 생성 실패 | `"버킷 생성 중 오류가 발생했습니다."` |
| **500** | 업로드 실패 | `"이미지 업로드 중 오류가 발생했습니다."` |

---

### 24. 특정 음식에 이미지 연결

이미 업로드된 이미지를 특정 음식에 연결합니다.

#### `POST /api/meals/images/meal-food/{mealFoodId}`

**Path Variables**
| 변수 | 타입 | 설명 |
|-----|------|------|
| `mealFoodId` | Integer | 식단 음식 ID |

**Request Headers**
```http
X-USER-ID: {userId}  # Gateway에서 자동 추가
Content-Type: application/x-www-form-urlencoded
```

**Request Parameters**
```
objectName: 업로드된 이미지의 Object Name
```

**처리 과정**
1. **MealFood 조회 및 소유권 확인**: mealFoodId로 조회 후 소유권 검증
2. **기존 이미지 삭제**: 기존 연결된 이미지가 있으면 MinIO에서 삭제
3. **새 이미지 연결**: objectName을 MealFood에 저장
4. **접근 URL 생성**: 완전한 접근 URL 생성하여 반환

**Response (성공)**
```json
{
  "timestamp": "2025-05-27T02:00:00",
  "status": "OK",
  "message": "이미지 연결 성공",
  "data": {
    "objectName": "meal/uuid-67890.jpg",
    "imageUrl": "http://minio:9000/meal-images/meal/uuid-67890.jpg"
  }
}
```

**에러 응답**
| 상태코드 | 에러 케이스 | 응답 메시지 |
|---------|-----------|-----------|
| **404** | 음식 없음 | `"해당 음식을 찾을 수 없습니다."` |
| **401** | 권한 없음 | `"이 음식에 접근할 권한이 없습니다."` |

---

### 25. 음식별 이미지 업로드

특정 음식에 이미지를 직접 업로드하고 연결합니다.

#### `POST /api/meals/images/meal-food/{mealFoodId}/upload` (multipart/form-data)

**Path Variables**
| 변수 | 타입 | 설명 |
|-----|------|------|
| `mealFoodId` | Integer | 식단 음식 ID |

**Request Headers**
```http
X-USER-ID: {userId}  # Gateway에서 자동 추가
Content-Type: multipart/form-data
```

**Request Parameters**
```
image (File): 업로드할 이미지 파일
```

**처리 과정**
1. **MealFood 조회 및 소유권 확인**: mealFoodId로 조회 후 소유권 검증
2. **기존 이미지 삭제**: 기존 연결된 이미지가 있으면 MinIO에서 삭제
3. **새 이미지 업로드**: MultipartFile을 MinIO에 업로드
4. **이미지 URL 업데이트**: MealFood 엔티티에 ObjectName 저장
5. **접근 URL 생성**: 완전한 접근 URL 생성하여 반환

**Response (성공)**
```json
{
  "timestamp": "2025-05-27T03:00:00",
  "status": "CREATED",
  "message": "음식 이미지 업로드 성공",
  "data": {
    "objectName": "meal/uuid-11111.jpg",
    "imageUrl": "http://minio:9000/meal-images/meal/uuid-11111.jpg"
  }
}
```

**에러 응답**
| 상태코드 | 에러 케이스 | 응답 메시지 |
|---------|-----------|-----------|
| **404** | 음식 없음 | `"해당 음식을 찾을 수 없습니다."` |
| **401** | 권한 없음 | `"이 음식에 접근할 권한이 없습니다."` |
| **500** | 업로드 실패 | `"이미지 업로드 중 오류가 발생했습니다."` |

---

### 26. 이미지 URL 조회

Object Name을 통해 이미지 접근 URL을 조회합니다.

#### `GET /api/meals/images/{objectName}`

**Path Variables**
| 변수 | 타입 | 설명 |
|-----|------|------|
| `objectName` | String | MinIO Object Name |

**Request Headers**
```http
X-USER-ID: {userId}  # Gateway에서 자동 추가
```

**처리 과정**
1. **Object Name 검증**: meal/ 접두사 자동 추가
2. **외부 엔드포인트 확인**: 외부 접근용 엔드포인트 설정 여부 확인
3. **URL 생성**: 적절한 엔드포인트로 완전한 URL 생성
4. **결과 반환**: 접근 가능한 URL 반환

**Response (성공)**
```json
{
  "timestamp": "2025-05-27T04:00:00",
  "status": "OK",
  "message": "이미지 URL 조회 성공",
  "data": {
    "imageUrl": "http://minio:9000/meal-images/meal/uuid-11111.jpg"
  }
}
```

---

### 27. 이미지 삭제

MinIO에서 이미지 파일을 삭제합니다.

#### `DELETE /api/meals/images/{objectName}`

**Path Variables**
| 변수 | 타입 | 설명 |
|-----|------|------|
| `objectName` | String | MinIO Object Name |

**Request Headers**
```http
X-USER-ID: {userId}  # Gateway에서 자동 추가
```

**처리 과정**
1. **Object Name 검증**: 빈 값이면 무시하고 종료
2. **MinIO 삭제**: RemoveObject로 파일 삭제
3. **결과 반환**: 삭제 완료 응답

**Response (성공)**
```json
{
  "timestamp": "2025-05-27T05:00:00",
  "status": "OK",
  "message": "식단 이미지 삭제 성공",
  "data": null
}
```

**에러 응답**
| 상태코드 | 에러 케이스 | 응답 메시지 |
|---------|-----------|-----------|
| **500** | 삭제 실패 | `"이미지 삭제 중 오류가 발생했습니다."` |

---

## MinIO 이미지 처리 상세

### 지원 이미지 형식
- **JPG/JPEG**: `image/jpeg`
- **PNG**: `image/png`
- **GIF**: `image/gif`
- **WebP**: `image/webp`

### 파일명 생성 규칙
```
meal/{UUID}.{확장자}
예: meal/550e8400-e29b-41d4-a716-446655440000.jpg
```

### URL 구조
```
{baseUrl}/{bucketName}/{objectName}
예: http://minio:9000/meal-images/meal/550e8400-e29b-41d4-a716-446655440000.jpg
```

### 이미지 업로드 플로우
1. **파일 검증**: 확장자 및 MIME 타입 확인
2. **유니크 파일명 생성**: UUID + 원본 확장자
3. **MinIO 업로드**: 적절한 Content-Type과 함께 업로드
4. **URL 생성**: 외부 접근 가능한 완전한 URL 생성
5. **데이터베이스 저장**: Object Name을 엔티티에 저장


## 📈 통계 분석 API

### 28. 영양 통계 조회

기간별(일/주/월) 영양소 섭취 통계를 조회합니다.

#### `GET /api/meals/statistics/nutrition`

**Request Headers**
```http
X-USER-ID: {userId}  # Gateway에서 자동 추가
```

**Query Parameters**
| 파라미터 | 타입 | 필수 | 기본값 | 설명 |
|---------|------|------|-------|------|
| `endDate` | LocalDate | ❌ | 오늘 | 종료 날짜 (YYYY-MM-DD) |
| `periodType` | PeriodType | ❌ | DAY | 기간 타입 (DAY/WEEK/MONTH) |

**예시 요청**
```http
GET /api/meals/statistics/nutrition?endDate=2025-05-26&periodType=WEEK
```

**처리 과정**
1. **기간 타입별 분기**: DAY/WEEK/MONTH에 따른 다른 처리 로직
2. **일별 통계 (DAY)**: 최근 7일간 일별 영양소 데이터
3. **주별 통계 (WEEK)**: 최근 7주간 주별 평균 영양소 데이터
4. **월별 통계 (MONTH)**: 최근 7개월간 월별 평균 영양소 데이터
5. **데이터 변환**: NutritionStatsEntry 리스트로 변환
6. **빈 데이터 처리**: 데이터가 없는 기간은 0값으로 채움

---

### 일별 통계 (DAY)

최근 7일간의 일별 영양소 섭취량을 조회합니다.

**Response (일별 통계)**
```json
{
  "timestamp": "2025-05-27T06:00:00",
  "status": "OK",
  "message": "영양 통계 조회 성공",
  "data": {
    "startDate": "2025-05-20",
    "endDate": "2025-05-26",
    "periodType": "DAY",
    "data": [
      {
        "label": "2025-05-20",
        "calorie": 1650,
        "carbs": 220,
        "protein": 76,
        "fat": 55,
        "sugar": 39
      },
      {
        "label": "2025-05-21",
        "calorie": 0,
        "carbs": 0,
        "protein": 0,
        "fat": 0,
        "sugar": 0
      },
      {
        "label": "2025-05-22",
        "calorie": 1800,
        "carbs": 250,
        "protein": 85,
        "fat": 66,
        "sugar": 45
      },
      {
        "label": "2025-05-23",
        "calorie": 1750,
        "carbs": 235,
        "protein": 80,
        "fat": 62,
        "sugar": 42
      },
      {
        "label": "2025-05-24",
        "calorie": 1900,
        "carbs": 270,
        "protein": 95,
        "fat": 70,
        "sugar": 50
      },
      {
        "label": "2025-05-25",
        "calorie": 1650,
        "carbs": 215,
        "protein": 78,
        "fat": 58,
        "sugar": 38
      },
      {
        "label": "2025-05-26",
        "calorie": 1800,
        "carbs": 245,
        "protein": 88,
        "fat": 65,
        "sugar": 46
      }
    ]
  }
}
```

---

### 주별 통계 (WEEK)

최근 7주간의 주별 평균 영양소 섭취량을 조회합니다.

**Response (주별 통계)**
```json
{
  "timestamp": "2025-05-27T06:00:00",
  "status": "OK",
  "message": "영양 통계 조회 성공",
  "data": {
    "startDate": "2025-04-07",
    "endDate": "2025-05-26",
    "periodType": "WEEK",
    "data": [
      {
        "label": "2025-04-07",
        "calorie": 1550,
        "carbs": 210,
        "protein": 70,
        "fat": 50,
        "sugar": 35
      },
      {
        "label": "2025-04-14",
        "calorie": 1620,
        "carbs": 225,
        "protein": 75,
        "fat": 52,
        "sugar": 38
      },
      {
        "label": "2025-04-21",
        "calorie": 1680,
        "carbs": 230,
        "protein": 78,
        "fat": 55,
        "sugar": 40
      },
      {
        "label": "2025-04-28",
        "calorie": 1720,
        "carbs": 240,
        "protein": 82,
        "fat": 58,
        "sugar": 42
      },
      {
        "label": "2025-05-05",
        "calorie": 1650,
        "carbs": 220,
        "protein": 76,
        "fat": 53,
        "sugar": 38
      },
      {
        "label": "2025-05-12",
        "calorie": 1750,
        "carbs": 245,
        "protein": 85,
        "fat": 60,
        "sugar": 44
      },
      {
        "label": "2025-05-19",
        "calorie": 1780,
        "carbs": 250,
        "protein": 88,
        "fat": 62,
        "sugar": 46
      }
    ]
  }
}
```

---

### 월별 통계 (MONTH)

최근 7개월간의 월별 평균 영양소 섭취량을 조회합니다.

**Response (월별 통계)**
```json
{
  "timestamp": "2025-05-27T06:00:00",
  "status": "OK",
  "message": "영양 통계 조회 성공",
  "data": {
    "startDate": "2024-11-01",
    "endDate": "2025-05-26",
    "periodType": "MONTH",
    "data": [
      {
        "label": "2024-11-01",
        "calorie": 1580,
        "carbs": 215,
        "protein": 72,
        "fat": 53,
        "sugar": 36
      },
      {
        "label": "2024-12-01",
        "calorie": 1650,
        "carbs": 230,
        "protein": 78,
        "fat": 56,
        "sugar": 40
      },
      {
        "label": "2025-01-01",
        "calorie": 1720,
        "carbs": 240,
        "protein": 82,
        "fat": 58,
        "sugar": 42
      },
      {
        "label": "2025-02-01",
        "calorie": 1680,
        "carbs": 235,
        "protein": 80,
        "fat": 57,
        "sugar": 41
      },
      {
        "label": "2025-03-01",
        "calorie": 1750,
        "carbs": 245,
        "protein": 85,
        "fat": 60,
        "sugar": 44
      },
      {
        "label": "2025-04-01",
        "calorie": 1700,
        "carbs": 238,
        "protein": 82,
        "fat": 58,
        "sugar": 42
      },
      {
        "label": "2025-05-01",
        "calorie": 1780,
        "carbs": 250,
        "protein": 88,
        "fat": 62,
        "sugar": 46
      }
    ]
  }
}
```

---

## 통계 데이터 계산 로직

### 일별 통계 (DAY)
- **기간**: 종료일 기준 최근 7일
- **데이터**: 각 날짜별 실제 섭취량
- **처리**: 데이터가 없는 날짜는 0으로 채움

### 주별 통계 (WEEK)
- **기간**: 종료일 기준 최근 7주
- **데이터**: 각 주별 일평균 섭취량
- **계산**: MySQL YEARWEEK 함수 활용
- **대체 로직**: 쿼리 실패 시 일별 데이터를 주별로 그룹화하여 평균 계산

### 월별 통계 (MONTH)
- **기간**: 종료일 기준 최근 7개월
- **데이터**: 각 월별 일평균 섭취량
- **계산**: YEAR, MONTH 함수 활용
- **대체 로직**: 쿼리 실패 시 일별 데이터를 월별로 그룹화하여 평균 계산

### 영양소 계산 공식
```sql
-- 일별 영양소 합계
SELECT 
    m.meal_date,
    SUM(f.calorie * mf.quantity) as total_calorie,
    SUM(f.carbohydrate * mf.quantity) as total_carbs,
    SUM(f.protein * mf.quantity) as total_protein,
    SUM(f.fat * mf.quantity) as total_fat,
    SUM(f.sweet * mf.quantity) as total_sugar
FROM meal_tb m
JOIN meal_time_tb mt ON m.meal_id = mt.meal_id
JOIN meal_food_tb mf ON mt.meal_time_id = mf.meal_time_id
JOIN food_information_tb f ON mf.food_id = f.food_id
WHERE m.user_id = ? AND m.meal_date BETWEEN ? AND ?
AND m.is_deleted = false AND mt.is_deleted = false
GROUP BY m.meal_date;
```

### 주별 평균 계산
```sql
-- MySQL YEARWEEK 활용
SELECT 
    YEARWEEK(m.meal_date, 1) as year_week,
    SUM(f.calorie * mf.quantity) / COUNT(DISTINCT m.meal_date) as avg_calorie,
    SUM(f.carbohydrate * mf.quantity) / COUNT(DISTINCT m.meal_date) as avg_carbs
FROM meal_tb m
JOIN meal_time_tb mt ON m.meal_id = mt.meal_id
JOIN meal_food_tb mf ON mt.meal_time_id = mf.meal_time_id
JOIN food_information_tb f ON mf.food_id = f.food_id
WHERE m.user_id = ? AND m.meal_date BETWEEN ? AND ?
GROUP BY YEARWEEK(m.meal_date, 1);
```

**에러 응답**
| 상태코드 | 에러 케이스 | 응답 메시지 |
|---------|-----------|-----------|
| **400** | 지원하지 않는 기간 타입 | `"지원하지 않는 기간 타입입니다: {periodType}"` |

---

## 통계 API 활용 사례

### 1. 일별 섭취량 모니터링
```
앱에서 최근 7일간 칼로리 섭취 그래프 표시
- X축: 날짜 (2025-05-20 ~ 2025-05-26)
- Y축: 칼로리 (0 ~ 2000)
- 목표 칼로리와 비교하여 색상 구분
```

### 2. 주간 영양 트렌드 분석
```
주별 평균 영양소 섭취 패턴 분석
- 탄수화물/단백질/지방 비율 변화
- 권장 섭취량 대비 부족/과다 영양소 식별
```

### 3. 월별 식습관 개선 추적
```
장기간 식습관 변화 모니터링
- 월별 평균 칼로리 증감 추이
- 목표 달성도 평가
- 개선 필요 영양소 식별
```


## 🗂️ 데이터베이스 설계

### 엔티티 구조

#### 1. Food 엔티티 (food_information_tb)
| 필드 | 타입 | 제약조건 | 설명 |
|-----|------|----------|------|
| **foodId** | Integer | PK, Auto Increment | 음식 고유 ID |
| **foodName** | String(50) | NOT NULL | 음식명 |
| **calorie** | Integer | NOT NULL | 칼로리 (kcal) |
| **carbohydrate** | BigDecimal(6,2) | NOT NULL | 탄수화물 (g) |
| **protein** | BigDecimal(6,2) | NOT NULL | 단백질 (g) |
| **fat** | BigDecimal(6,2) | NOT NULL | 지방 (g) |
| **sweet** | BigDecimal(6,2) | NOT NULL | 당분 (g) |
| **sodium** | BigDecimal(6,2) | NOT NULL | 나트륨 (mg) |
| **saturatedFat** | BigDecimal(6,2) | NOT NULL | 포화지방 (g) |
| **transFat** | BigDecimal(6,2) | NOT NULL | 트랜스지방 (g) |
| **cholesterol** | BigDecimal(6,2) | NOT NULL | 콜레스테롤 (mg) |
| **baseAmount** | String | NOT NULL | 기준량 (예: "1공기(210g)") |
| **createdAt** | LocalDateTime | NOT NULL | 생성일시 |
| **updatedAt** | LocalDateTime | NOT NULL | 수정일시 |
| **deletedAt** | LocalDateTime | NULL | 삭제일시 |
| **isDeleted** | Boolean | DEFAULT false | 삭제 여부 |

#### 2. FavoriteFood 엔티티 (favorite_food_tb)
| 필드 | 타입 | 제약조건 | 설명 |
|-----|------|----------|------|
| **favoriteId** | Integer | PK, Auto Increment | 즐겨찾기 ID |
| **userId** | Integer | NOT NULL | 사용자 ID |
| **food** | Food | FK, NOT NULL | 음식 참조 |
| **createdAt** | LocalDateTime | NOT NULL | 생성일시 |

**제약조건**: `UNIQUE(userId, foodId)` - 사용자별 음식당 하나의 즐겨찾기만 허용

#### 3. FoodSet 엔티티 (food_set_tb)
| 필드 | 타입 | 제약조건 | 설명 |
|-----|------|----------|------|
| **foodSetId** | Integer | PK, Auto Increment | 음식 세트 ID |
| **userId** | Integer | NOT NULL | 사용자 ID |
| **name** | String(50) | NOT NULL | 세트명 |
| **createdAt** | LocalDateTime | NOT NULL | 생성일시 |
| **updatedAt** | LocalDateTime | NOT NULL | 수정일시 |
| **deletedAt** | LocalDateTime | NULL | 삭제일시 |
| **isDeleted** | Boolean | DEFAULT false | 삭제 여부 |

#### 4. FoodSetFood 엔티티 (food_set_food_tb)
| 필드 | 타입 | 제약조건 | 설명 |
|-----|------|----------|------|
| **foodSetFoodId** | Integer | PK, Auto Increment | 세트 음식 ID |
| **foodSet** | FoodSet | FK, NOT NULL | 음식 세트 참조 |
| **food** | Food | FK, NOT NULL | 음식 참조 |
| **quantity** | Float | NOT NULL | 수량 |

#### 5. Meal 엔티티 (meal_tb)
| 필드 | 타입 | 제약조건 | 설명 |
|-----|------|----------|------|
| **mealId** | Integer | PK, Auto Increment | 식단 ID |
| **userId** | Integer | NOT NULL | 사용자 ID |
| **mealDate** | LocalDate | NOT NULL | 식사 날짜 |
| **isMeal** | Boolean | NOT NULL | 식사 여부 |
| **createdAt** | LocalDateTime | NOT NULL | 생성일시 |
| **updatedAt** | LocalDateTime | NOT NULL | 수정일시 |
| **deletedAt** | LocalDateTime | NULL | 삭제일시 |
| **isDeleted** | Boolean | DEFAULT false | 삭제 여부 |

#### 6. MealTime 엔티티 (meal_time_tb)
| 필드 | 타입 | 제약조건 | 설명 |
|-----|------|----------|------|
| **mealTimeId** | Integer | PK, Auto Increment | 식사 시간 ID |
| **meal** | Meal | FK, NOT NULL | 식단 참조 |
| **mealType** | Enum | NOT NULL | 식사 유형 (BREAKFAST/LUNCH/DINNER/SNACK) |
| **eatingTime** | LocalTime | NOT NULL | 식사 시간 |
| **mealTimeImageUrl** | String(250) | NULL | 시간대별 이미지 URL |
| **createdAt** | LocalDateTime | NOT NULL | 생성일시 |
| **updatedAt** | LocalDateTime | NOT NULL | 수정일시 |
| **deletedAt** | LocalDateTime | NULL | 삭제일시 |
| **isDeleted** | Boolean | DEFAULT false | 삭제 여부 |

#### 7. MealFood 엔티티 (meal_food_tb)
| 필드 | 타입 | 제약조건 | 설명 |
|-----|------|----------|------|
| **mealFoodId** | Integer | PK, Auto Increment | 식단 음식 ID |
| **mealTime** | MealTime | FK, NOT NULL | 식사 시간 참조 |
| **food** | Food | FK, NOT NULL | 음식 참조 |
| **quantity** | Float | NOT NULL | 수량 |
| **foodImageUrl** | String(250) | NULL | 음식별 이미지 URL |

#### 8. 영양 기준 엔티티들

**AgeGroup 열거형**
```java
public enum AgeGroup {
    ALL,        // 전체 평균
    TEENS,      // 10대
    TWENTIES,   // 20대
    THIRTIES,   // 30대
    FORTIES,    // 40대
    FIFTIES,    // 50대
    SIXTIES,    // 60대
    ELDERLY     // 65세 이상
}
```

**MaleNutrientStandard 엔티티 (male_nutrient_standard)**
| 필드 | 타입 | 제약조건 | 설명 |
|-----|------|----------|------|
| **id** | Long | PK, Auto Increment | 기준 ID |
| **ageGroup** | AgeGroup | NOT NULL | 연령대 |
| **caloriesMin** | Integer | NOT NULL | 최소 칼로리 (kcal) |
| **caloriesMax** | Integer | NULL | 최대 칼로리 (kcal) |
| **carbohydratesMin** | Integer | NOT NULL | 최소 탄수화물 (g) |
| **carbohydratesMax** | Integer | NULL | 최대 탄수화물 (g) |
| **proteinMin** | Integer | NOT NULL | 최소 단백질 (g) |
| **proteinMax** | Integer | NULL | 최대 단백질 (g) |
| **fatMin** | Double | NOT NULL | 최소 지방 (g) |
| **fatMax** | Double | NULL | 최대 지방 (g) |
| **sodium** | Integer | NOT NULL | 권장 나트륨 (mg) |

**FemaleNutrientStandard 엔티티 (female_nutrient_standard)**
- MaleNutrientStandard와 동일한 구조
- 여성 연령대별 영양소 권장섭취량 기준

**MaleNutrientIntake 엔티티 (male_nutrient_intake)**
| 필드 | 타입 | 제약조건 | 설명 |
|-----|------|----------|------|
| **id** | Long | PK, Auto Increment | 섭취량 ID |
| **ageGroup** | AgeGroup | NOT NULL | 연령대 |
| **calories** | Double | NOT NULL | 평균 칼로리 섭취량 (kcal) |
| **protein** | Double | NOT NULL | 평균 단백질 섭취량 (g) |
| **fat** | Double | NOT NULL | 평균 지방 섭취량 (g) |
| **carbohydrates** | Double | NOT NULL | 평균 탄수화물 섭취량 (g) |
| **sugar** | Double | NOT NULL | 평균 당 섭취량 (g) |
| **sodium** | Double | NOT NULL | 평균 나트륨 섭취량 (mg) |

**FemaleNutrientIntake 엔티티 (female_nutrient_intake)**
- MaleNutrientIntake와 동일한 구조
- 여성 연령대별 영양소 평균섭취량 데이터

---

### 데이터베이스 관계

```
User (외부) ─┬─→ FavoriteFood ──→ Food
             ├─→ FoodSet ──→ FoodSetFood ──→ Food
             └─→ Meal ──→ MealTime ──→ MealFood ──→ Food
                            │
                            └─→ MealType (BREAKFAST/LUNCH/DINNER/SNACK)

영양 기준 데이터:
MaleNutrientStandard ─── AgeGroup
FemaleNutrientStandard ─── AgeGroup
MaleNutrientIntake ─── AgeGroup
FemaleNutrientIntake ─── AgeGroup
```

**관계 설명**
- **User 1:N FavoriteFood**: 사용자는 여러 음식을 즐겨찾기 등록 가능
- **User 1:N FoodSet**: 사용자는 여러 음식 세트 생성 가능
- **FoodSet 1:N FoodSetFood**: 음식 세트는 여러 음식 포함 가능
- **User 1:N Meal**: 사용자는 여러 날짜의 식단 기록 가능
- **Meal 1:N MealTime**: 하루 식단은 여러 시간대 포함 가능
- **MealTime 1:N MealFood**: 각 시간대는 여러 음식 포함 가능

---

### 인덱스 전략

#### 성능 최적화를 위한 권장 인덱스

```sql
-- 즐겨찾기 조회 최적화
CREATE INDEX idx_favorite_user_food ON favorite_food_tb(user_id, food_id);

-- 식단 날짜별 조회 최적화
CREATE INDEX idx_meal_user_date ON meal_tb(user_id, meal_date);
CREATE INDEX idx_meal_user_date_deleted ON meal_tb(user_id, meal_date, is_deleted);

-- 식사 시간대별 조회 최적화
CREATE INDEX idx_mealtime_meal_type ON meal_time_tb(meal_id, meal_type);
CREATE INDEX idx_mealtime_meal_deleted ON meal_time_tb(meal_id, is_deleted);

-- 식단 음식 조회 최적화
CREATE INDEX idx_mealfood_mealtime ON meal_food_tb(meal_time_id);

-- 음식 세트 조회 최적화
CREATE INDEX idx_foodset_user ON food_set_tb(user_id);
CREATE INDEX idx_foodsetfood_set ON food_set_food_tb(food_set_id);

-- 최근 음식 조회 최적화 (복합 인덱스)
CREATE INDEX idx_meal_user_date_time ON meal_tb(user_id, meal_date DESC);
CREATE INDEX idx_mealtime_meal_eating ON meal_time_tb(meal_id, eating_time DESC);

-- 통계 조회 최적화
CREATE INDEX idx_meal_date_user ON meal_tb(meal_date, user_id, is_deleted);
```

---

### 데이터 무결성 제약조건

#### 유니크 제약조건
```sql
-- 사용자별 즐겨찾기 중복 방지
ALTER TABLE favorite_food_tb 
ADD CONSTRAINT uk_favorite_user_food UNIQUE (user_id, food_id);

-- 사용자별 날짜당 하나의 식단만 허용 (비즈니스 로직으로 처리)
-- 실제로는 여러 개 생성 가능하지만 애플리케이션에서 제어
```

#### 외래키 제약조건
```sql
-- FavoriteFood → Food
ALTER TABLE favorite_food_tb 
ADD CONSTRAINT fk_favorite_food FOREIGN KEY (food_id) REFERENCES food_information_tb(food_id);

-- FoodSetFood → FoodSet, Food
ALTER TABLE food_set_food_tb 
ADD CONSTRAINT fk_foodset_food_set FOREIGN KEY (food_set_id) REFERENCES food_set_tb(food_set_id);
ADD CONSTRAINT fk_foodset_food_food FOREIGN KEY (food_id) REFERENCES food_information_tb(food_id);

-- MealTime → Meal
ALTER TABLE meal_time_tb 
ADD CONSTRAINT fk_mealtime_meal FOREIGN KEY (meal_id) REFERENCES meal_tb(meal_id);

-- MealFood → MealTime, Food
ALTER TABLE meal_food_tb 
ADD CONSTRAINT fk_mealfood_mealtime FOREIGN KEY (meal_time_id) REFERENCES meal_time_tb(meal_time_id);
ADD CONSTRAINT fk_mealfood_food FOREIGN KEY (food_id) REFERENCES food_information_tb(food_id);
```

---

### 데이터 초기화

#### 영양 기준 데이터 자동 로딩

**NutrientStandardLoader**: 애플리케이션 시작 시 영양 권장량 기준 데이터 자동 로딩
- 남성/여성 연령대별 권장 칼로리, 탄수화물, 단백질, 지방, 나트륨
- 10대~60대, 65세 이상까지 세분화

**NutrientIntakeLoader**: 애플리케이션 시작 시 평균 섭취량 데이터 자동 로딩
- 남성/여성 연령대별 실제 평균 섭취량 통계
- 사용자 섭취량과 비교 분석에 활용

```java
@EventListener(ApplicationReadyEvent.class)
@Transactional
public void loadData() {
    if (isDataLoaded()) return;
    
    // 남성 데이터 로드
    loadMaleData();
    
    // 여성 데이터 로드  
    loadFemaleData();
}
```

---

### 스토리지 고려사항

#### 데이터 증가 예상치
- **Food 테이블**: 약 10,000건 (음식 데이터베이스)
- **Meal 테이블**: 사용자당 연간 365건
- **MealTime 테이블**: 사용자당 연간 1,460건 (하루 4끼 기준)
- **MealFood 테이블**: 사용자당 연간 14,600건 (시간대당 10개 음식 기준)

#### 파티셔닝 전략 (향후 확장 시)
```sql
-- 날짜 기반 파티셔닝 (연도별)
CREATE TABLE meal_tb (
    ...
) PARTITION BY RANGE (YEAR(meal_date)) (
    PARTITION p2024 VALUES LESS THAN (2025),
    PARTITION p2025 VALUES LESS THAN (2026),
    PARTITION p2026 VALUES LESS THAN (2027)
);
```

#### 아카이빙 전략
- **기준**: 3년 이상 된 데이터
- **대상**: meal_tb, meal_time_tb, meal_food_tb
- **방법**: 별도 아카이브 테이블로 이동 후 원본 삭제


## 🔄 주요 비즈니스 플로우

### 1. 식단 등록 플로우
```
사용자 → 식단 등록 요청 (이미지 포함)
     ↓
이미지 MinIO 업로드 → ObjectName 생성
     ↓
기존 날짜 식단 확인 → 있으면 업데이트, 없으면 생성
     ↓
MealTime/MealFood 엔티티 생성
     ↓
이미지 URL 매핑 후 응답 반환
```

### 2. 영양소 계산 플로우
```
음식 추가 시:
Food 기본 영양소 × 사용자 입력 수량 = 총 영양소

시간대별 합계:
MealTime 내 모든 MealFood 합산

일별 합계:
Meal 내 모든 MealTime 합산

통계 계산:
Repository 쿼리로 기간별 집계
```

### 3. 이미지 업로드 플로우
```
이미지 파일 업로드
     ↓
UUID + 확장자로 파일명 생성
     ↓
MinIO PutObject 실행
     ↓
ObjectName 저장 → 완전한 URL 반환
```

### 4. 즐겨찾기 토글 플로우
```
즐겨찾기 요청
     ↓
기존 즐겨찾기 확인
     ↓
있으면 삭제 / 없으면 생성
     ↓
토글 결과 반환
```

### 5. 음식 세트 관리 플로우
```
세트 생성:
FoodSet 생성 → FoodSetFood 매핑 → 총 칼로리 계산

세트 수정:
기존 FoodSetFood 삭제 → 새로운 매핑 생성

세트 삭제:
orphanRemoval=true로 연관 데이터 자동 삭제
```

### 6. 통계 데이터 생성 플로우
```
일별 통계: 최근 7일 개별 날짜 데이터
주별 통계: MySQL YEARWEEK로 7주 평균
월별 통계: YEAR/MONTH로 7개월 평균

실패 시 대체 로직:
일별 데이터를 애플리케이션에서 그룹화하여 평균 계산
```

## 🛡️ 보안 및 검증

### 입력값 검증
```java
// 음식 세트 생성 시
- 음식 ID 존재 여부 확인
- 수량 > 0 검증
- 세트명 길이 제한 (50자)

// 식단 등록 시  
- 날짜 유효성 (미래일 허용)
- 시간 형식 (HH:mm:ss)
- 음식 ID 유효성
```

### API 보안
```bash
# 모든 API 인증 필요
X-USER-ID: Gateway에서 JWT 토큰 검증 후 자동 추가

# 소유권 검증
- 음식 세트: userId 일치 확인
- 식단: Meal → userId 확인  
- 개별 음식: MealFood → MealTime → Meal → userId 경로 확인
```

### 소유권 검증 예시
```java
// 음식 세트
private FoodSet findOwnedFoodSet(Integer foodSetId, Integer userId) {
    FoodSet foodSet = foodSetRepository.findById(foodSetId)
        .orElseThrow(() -> new NotFoundException("음식 세트를 찾을 수 없습니다."));
    
    if (!foodSet.getUserId().equals(userId)) {
        throw new UnauthorizedException("세트에 접근할 권한이 없습니다.");
    }
    
    return foodSet;
}
```

### 데이터 무결성
- **유니크 제약**: (userId, foodId) 즐겨찾기 중복 방지
- **외래키 제약**: 모든 참조 관계 보장
- **Soft Delete**: 식단 데이터는 논리적 삭제만 수행

## ⚡ 성능 고려사항

### 응답 시간 최적화
| API | 예상 응답시간 | 병목 구간 | 최적화 방안 |
|-----|--------------|----------|-----------|
| `POST /api/meals` | 2-5초 | MinIO 이미지 업로드 | 비동기 처리 고려 |
| `GET /api/foods` | 100-200ms | 즐겨찾기 조회 | 배치 쿼리 최적화 |
| `GET /api/meals/statistics/nutrition` | 500-1000ms | 복잡한 집계 쿼리 | 인덱스 최적화, 캐싱 |
| `GET /api/meals/daily-nutrition` | 200-300ms | 영양소 계산 쿼리 | 쿼리 최적화 |

### 데이터베이스 최적화
```sql
-- 권장 인덱스
CREATE INDEX idx_favorite_user_food ON favorite_food_tb(user_id, food_id);
CREATE INDEX idx_meal_user_date ON meal_tb(user_id, meal_date);
CREATE INDEX idx_mealtime_meal_type ON meal_time_tb(meal_id, meal_type);
CREATE INDEX idx_mealfood_mealtime ON meal_food_tb(meal_time_id);

-- 자주 사용되는 쿼리 최적화
-- 1. 즐겨찾기 배치 조회
SELECT food_id FROM favorite_food_tb 
WHERE user_id = ? AND food_id IN (?,?,?...);

-- 2. 일일 영양소 계산
SELECT COALESCE(SUM(f.calorie * mf.quantity), 0) as total_calorie
FROM meal_tb m 
JOIN meal_time_tb mt ON m.meal_id = mt.meal_id
JOIN meal_food_tb mf ON mt.meal_time_id = mf.meal_time_id  
JOIN food_information_tb f ON mf.food_id = f.food_id
WHERE m.user_id = ? AND m.meal_date = ?;
```

### MinIO 이미지 처리 최적화
```java
// MIME 타입 최적화
String contentType = switch(extension.toLowerCase()) {
    case ".jpg", ".jpeg" -> "image/jpeg";
    case ".png" -> "image/png";
    case ".gif" -> "image/gif";
    case ".webp" -> "image/webp";
    default -> "image/jpeg";
};

// 이미지 URL 캐싱 고려 (향후)
// Redis 기반 URL 캐싱 도입 가능
```

### 메모리 사용 최적화
- **배치 처리**: 즐겨찾기 확인을 IN절로 배치 처리
- **지연 로딩**: @ManyToOne 관계에서 필요시에만 조회
- **DTO 최적화**: 불필요한 필드 제거로 메모리 절약


## 🧪 테스트 가이드

### 주요 기능 테스트 포인트

#### 1. 음식 정보 관리 검증
- **전체 목록 조회**: 즐겨찾기 상태 정확성
- **상세 조회**: 모든 영양소 정보 완전성
- **즐겨찾기 토글**: 등록/해제 상태 변경

#### 2. 음식 세트 관리 검증  
- **생성 → 조회 → 수정 → 삭제** 전체 플로우
- **총 칼로리 계산** 정확성
- **소유권 검증** (다른 사용자 접근 차단)

#### 3. 식단 기록 관리 검증
- **이미지 업로드 + 식단 등록** 연동
- **영양소 실시간 계산** 정확성 (수량 반영)
- **시간대별/날짜별 조회** 정상 동작
- **삭제 기능** (전체/시간대별/개별음식)

#### 4. 통계 분석 검증
- **일별/주별/월별** 통계 데이터 정확성
- **빈 데이터 처리** (0값 반환)
- **MySQL 쿼리 실패 시 대체 로직** 동작

### 중요 검증 사항

#### MinIO 이미지 처리
- 다양한 이미지 형식 업로드 (jpg, png, gif, webp)
- 이미지 URL 생성 및 접근 가능성
- 기존 이미지 삭제 후 새 이미지 업로드

#### 데이터 무결성
- 즐겨찾기 중복 등록 방지
- 식단 소유권 검증
- Soft delete 정상 동작

#### 성능 테스트
- 대용량 음식 목록 조회 응답시간
- 복잡한 통계 쿼리 성능
- 이미지 업로드 시간 (3-5초 이내)

---

## 📝 개발 참고사항

### MinIO 설정 상태
```yaml
# 현재 설정 (외부 엔드포인트 활용)
spring:
  minio:
    endpoint: http://minio:9000
    external-endpoint: http://external-minio:9000  # 외부 접근용
    bucket:
      name: meal-images
```

### 지원 이미지 형식
- **JPG/JPEG**: `image/jpeg`
- **PNG**: `image/png` 
- **GIF**: `image/gif`
- **WebP**: `image/webp`

### 영양소 계산 공식
```
총 영양소 = 음식 기본 영양소 × 사용자 입력 수량

시간대별 합계 = MealTime 내 모든 MealFood 합산
일별 합계 = Meal 내 모든 MealTime 합산
```

### RabbitMQ 알림 연동
```java
// 식사 알림 발송 (현재 구현됨)
@Service
public class MealNotificationPublisher {
    private static final String EXCHANGE = "alert-exchange";
    private static final String ROUTING_KEY = "alert.push.meal.requested";
    
    public void send(String message) {
        rabbitTemplate.convertAndSend(EXCHANGE, ROUTING_KEY, message);
    }
}
```

### 영양 기준 데이터
- **자동 로딩**: 애플리케이션 시작 시 자동으로 기준 데이터 로딩
- **연령대별 구분**: 10대~60대, 65세 이상
- **성별 구분**: 남성/여성 각각 권장량과 평균 섭취량 데이터