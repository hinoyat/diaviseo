# Exercise API 명세서

**Health Service - 운동 도메인의 API 명세서입니다.**

이 문서는 운동 기록 등록, 수정, 삭제, 조회 및 운동 유형/카테고리 관리, 즐겨찾기 등록/해제, 통계 API 전반을 포함합니다.

---

(작성 시작 전 기본 구조)

## 📌 API 개요

### Base URL

```
http://health-service/api/exercises
```

### 공통 Header

| 헤더              | 필수 | 설명                       |
| --------------- | -- | ------------------------ |
| `Authorization` | ✅  | Bearer 타입 AccessToken    |
| `X-USER-ID`     | ✅  | Gateway에서 전달하는 사용자 식별 ID |

### 공통 응답 형식 (`ResponseDto<T>`) 예시

```json
{
  "timestamp": "2025-05-27T10:00:00",
  "status": "OK",
  "message": "요청 성공",
  "data": { /* 실제 응답 데이터 */ }
}
```

---

(아래로 API 상세 명세가 이어집니다)

## 🔄 운동 기록 API

### 1. 운동 기록 등록

#### `POST /api/exercises`

운동을 등록합니다. 사용자 ID는 Gateway에서 전달한 `X-USER-ID` 헤더를 통해 확인합니다. 운동 칼로리는 운동 유형과 시간 기반으로 서버에서 계산됩니다.

**Request Headers**

```
Authorization: Bearer {accessToken}
X-USER-ID: {userId}
Content-Type: application/json
```

**Request Body**

```json
{
  "exerciseNumber": 101,
  "exerciseDate": "2025-05-27T08:00:00",
  "exerciseTime": 30,
  "healthConnectUuid": "abc123"
}
```

**Field 설명**

| 필드                  | 타입            | 필수 | 설명                                  |
| ------------------- | ------------- | -- | ----------------------------------- |
| `exerciseNumber`    | Integer       | ✅  | 운동 유형 고유 번호 (exercise\_type\_tb 기준) |
| `exerciseDate`      | LocalDateTime | ✅  | 수행 일시 (예: 2025-05-27T08:00:00)      |
| `exerciseTime`      | Integer       | ✅  | 운동 시간 (단위: 분)                       |
| `healthConnectUuid` | String        | ❌  | 외부 연동 ID (예: Health Connect)        |

**Response (성공)**

```json
{
  "timestamp": "2025-05-27T10:30:00",
  "status": "OK",
  "message": "운동 기록 생성 성공",
  "data": {
    "exerciseId": 1,
    "exerciseName": "걷기",
    "exerciseTime": 30,
    "exerciseCalorie": 110,
    "exerciseDate": "2025-05-27T08:00:00"
  }
}
```

**에러 응답 예시**

| 상태코드 | 에러 케이스   | 메시지                                      |
| ---- | -------- | ---------------------------------------- |
| 400  | 필드 누락    | `"운동 시간은 0보다 커야 합니다."`                   |
| 404  | 운동 유형 없음 | `"해당 exerciseNumber에 해당하는 운동 유형이 없습니다."` |
| 500  | 서버 오류    | `"운동 기록 저장에 실패했습니다."`                    |

**처리 과정 요약**

1. 사용자 인증 및 ID 확인 (`X-USER-ID` 헤더)
2. 운동 유형 조회 (exerciseNumber 기준)
3. 운동 칼로리 = `exerciseType.caloriePerMinute × exerciseTime`
4. 운동 기록 생성 및 저장 (Exercise 엔티티)
5. 메시지 발행 (ExerciseNotificationPublisher)
6. 생성된 운동 정보 응답 반환

## 📂 운동 유형 / 카테고리 API

### 5. 전체 운동 카테고리 조회

#### `GET /api/exercises/category`

운동 유형의 상위 카테고리를 모두 조회합니다.

**Response (성공)**

```json
{
  "status": "OK",
  "message": "카테고리 전체 조회 성공",
  "data": [
    {
      "exerciseCategoryId": 1,
      "exerciseCategoryName": "유산소"
    },
    ...
  ]
}
```

---

### 6. 카테고리별 운동 유형 조회

#### `GET /api/exercises/category/{categoryId}`

특정 카테고리에 속한 운동 유형들을 조회합니다.

**Response (성공)**

```json
{
  "status": "OK",
  "message": "운동 유형 조회 성공",
  "data": [
    {
      "exerciseTypeId": 1,
      "exerciseName": "걷기",
      "exerciseEnglishName": "Walking",
      "exerciseNumber": 101,
      "exerciseCalorie": 3.6,
      "isFavorite": false
    },
    ...
  ]
}
```

---

### 7. 운동 유형 상세 조회

#### `GET /api/exercises/types/{exerciseNumber}`

운동 번호로 특정 운동 유형의 상세 정보를 조회합니다. (즐겨찾기 여부 포함)

**Response (성공)**

```json
{
  "status": "OK",
  "message": "운동 유형 상세 조회 성공",
  "data": {
    "exerciseTypeId": 1,
    "exerciseCategoryId": 1,
    "exerciseName": "걷기",
    "exerciseEnglishName": "Walking",
    "exerciseNumber": 101,
    "exerciseCalorie": 3.6,
    "exerciseCategoryName": "유산소",
    "isFavorite": true
  }
}
```

**에러 응답**

| 상태코드 | 메시지                      |
| ---- | ------------------------ |
| 404  | `"해당 운동 유형을 찾을 수 없습니다."` |

### 2. 전체 운동 기록 조회

#### `GET /api/exercises`

사용자가 기록한 모든 운동 목록을 최신순으로 조회합니다.

**Request Headers**

```
Authorization: Bearer {accessToken}
X-USER-ID: {userId}
```

**Response (성공)**

```json
{
  "timestamp": "2025-05-27T10:30:00",
  "status": "OK",
  "message": "운동 기록 전체 조회 성공",
  "data": [
    {
      "exerciseId": 1,
      "exerciseName": "걷기",
      "exerciseCategoryName": "유산소",
      "exerciseNumber": 101,
      "exerciseDate": "2025-05-27T08:00:00",
      "exerciseTime": 30,
      "exerciseCalorie": 110
    },
    { ... }
  ]
}
```

**에러 응답**

| 상태코드 | 메시지                   |
| ---- | --------------------- |
| 404  | `"운동 기록이 존재하지 않습니다."` |

---

### 3. 운동 기록 수정

#### `PUT /api/exercises/{exerciseId}`

운동 기록을 수정합니다. 칼로리는 수정된 시간 기준으로 재계산됩니다.

**Request Body**

```json
{
  "exerciseDate": "2025-05-27T08:30:00",
  "exerciseTime": 40
}
```

**Response (성공)**

```json
{
  "status": "OK",
  "message": "운동 기록 수정 성공",
  "data": null
}
```

**에러 응답**

| 상태코드 | 메시지                      |
| ---- | ------------------------ |
| 400  | `"운동 시간은 0보다 커야 합니다."`   |
| 404  | `"해당 운동 기록을 찾을 수 없습니다."` |

---

### 4. 운동 기록 삭제

#### `DELETE /api/exercises/{exerciseId}`

운동 기록을 삭제합니다.

**Response (성공)**

```json
{
  "status": "OK",
  "message": "운동 기록 삭제 성공",
  "data": null
}
```

**에러 응답**

| 상태코드 | 메시지                      |
| ---- | ------------------------ |
| 404  | `"해당 운동 기록을 찾을 수 없습니다."` |

## ⭐ 즐겨찾기 운동 API

### 8. 즐겨찾기 등록/해제 (토글)

#### `POST /api/exercises/favorites/{exerciseNumber}`

특정 운동을 즐겨찾기로 등록하거나 해제합니다.

**Response (성공)**

```json
{
  "status": "OK",
  "message": "즐겨찾기 토글 성공",
  "data": {
    "isFavorite": true
  }
}
```

**에러 응답**

| 상태코드 | 메시지                      |
| ---- | ------------------------ |
| 404  | `"해당 운동 유형을 찾을 수 없습니다."` |

---

### 9. 즐겨찾기 목록 조회

#### `GET /api/exercises/favorites`

사용자의 즐겨찾기 등록된 운동 목록을 조회합니다.

**Response (성공)**

```json
{
  "status": "OK",
  "message": "즐겨찾기 목록 조회 성공",
  "data": [
    {
      "exerciseTypeId": 1,
      "exerciseName": "걷기",
      "exerciseNumber": 101,
      "exerciseCalorie": 3.6,
      "exerciseCategoryName": "유산소"
    },
    ...
  ]
}
```

**에러 응답**

| 상태코드 | 메시지                 |
| ---- | ------------------- |
| 404  | `"즐겨찾기한 운동이 없습니다."` |

## 📈 운동 통계 API

### 10. 오늘의 운동 통계 조회

#### `GET /api/exercises/today`

오늘 또는 지정한 날짜의 운동 통계를 조회합니다.

**Query Parameters**

| 파라미터   | 필수 | 설명                                  |
| ------ | -- | ----------------------------------- |
| `date` | ❌  | 조회 기준 날짜 (yyyy-MM-dd) - 미입력 시 오늘 날짜 |

**Response (성공)**

```json
{
  "status": "OK",
  "message": "오늘 운동 통계 조회 성공",
  "data": {
    "date": "2025-05-27",
    "totalCalories": 210,
    "totalExerciseTime": 45,
    "exerciseCount": 2,
    "exercises": [
      {
        "exerciseId": 1,
        "exerciseName": "걷기",
        "categoryName": "유산소",
        "exerciseDate": "2025-05-27T08:00:00",
        "exerciseTime": 30,
        "exerciseCalorie": 110
      },
      ...
    ]
  }
}
```

---

### 11. 일별 통계 조회 (최근 7일)

#### `GET /api/exercises/daily`

**Query Parameters**

| 파라미터   | 필수 | 설명                             |
| ------ | -- | ------------------------------ |
| `date` | ❌  | 기준일 (yyyy-MM-dd) - default: 오늘 |

**Response (성공)**

```json
{
  "status": "OK",
  "message": "일별 운동 통계 조회 성공",
  "data": {
    "dailyExercises": [
      {
        "date": "2025-05-27",
        "totalCalories": 210,
        "exerciseCount": 2,
        "exercises": [ ... ]
      },
      ...
    ]
  }
}
```

---

### 12. 주별 통계 조회 (최근 7주)

#### `GET /api/exercises/weekly`

**Query Parameters**

| 파라미터   | 필수 | 설명                             |
| ------ | -- | ------------------------------ |
| `date` | ❌  | 기준일 (yyyy-MM-dd) - default: 오늘 |

**Response (성공)**

```json
{
  "status": "OK",
  "message": "주별 운동 통계 조회 성공",
  "data": {
    "weeklyExercises": [
      {
        "startDate": "2025-04-21",
        "endDate": "2025-04-27",
        "avgDailyCalories": 250.4,
        "totalExerciseCount": 10,
        "totalCalories": 1753
      },
      ...
    ]
  }
}
```

---

### 13. 월별 통계 조회 (최근 7개월)

#### `GET /api/exercises/monthly`

**Query Parameters**

| 파라미터   | 필수 | 설명                             |
| ------ | -- | ------------------------------ |
| `date` | ❌  | 기준월 (yyyy-MM-dd) - default: 오늘 |

**Response (성공)**

```json
{
  "status": "OK",
  "message": "월별 운동 통계 조회 성공",
  "data": {
    "monthlyExercises": [
      {
        "yearMonth": "2025-04",
        "avgDailyCalories": 320.0,
        "totalExerciseCount": 28,
        "totalCalories": 8960
      },
      ...
    ]
  }
}
```
