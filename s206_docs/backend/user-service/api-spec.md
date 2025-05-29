# User Service API 명세

**User Service는 회원 관리, SMS 인증, 신체정보 관리를 담당하는 사용자 서비스입니다.**

---

## 📌 API 개요

### Base URL
```
http://user-service/api/users
```

### User Service의 역할
- **회원 관리**: 가입, 프로필 조회/수정, 탈퇴 처리
- **SMS 인증**: CoolSMS 기반 휴대폰 번호 인증
- **신체정보 관리**: 키, 몸무게, 목표 등 건강 데이터 기록
- **Auth Service 연동**: 소셜 로그인 시 사용자 존재 여부 확인
- **FCM 토큰 관리**: 푸시 알림을 위한 Firebase 토큰 관리

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
| **201** | 생성 성공 | `"message": "회원가입 성공"` |
| **400** | 잘못된 요청 | `"message": "필수 파라미터가 누락되었습니다."` |
| **401** | 인증 실패 | `"message": "유효하지 않은 토큰입니다."` |
| **404** | 데이터 없음 | `"message": "해당 유저를 찾을 수 없습니다."` |
| **409** | 중복 데이터 | `"message": "이미 가입된 이메일입니다."` |
| **500** | 서버 오류 | `"message": "SMS 인증번호 전송에 실패했습니다."` |

---

## 👤 회원 관리 API

### 1. 회원가입

소셜 로그인 후 상세 정보를 입력하여 회원가입을 완료합니다.

#### `POST /api/users/signup`

**Request Headers**
```http
Content-Type: application/json
```

**Request Body**
```json
{
  "name": "홍길동",
  "nickname": "길동이",
  "gender": "M",
  "goal": "WEIGHT_LOSS",
  "height": 175.5,
  "weight": 70.0,
  "birthday": "1990-01-01",
  "phone": "01012345678",
  "email": "test@example.com",
  "provider": "google",
  "consentPersonal": true,
  "locationPersonal": true
}
```

**Request Body Fields**
| 필드 | 타입 | 필수 | 설명 |
|-----|------|------|------|
| `name` | String | ✅ | 실명 |
| `nickname` | String | ✅ | 별명 |
| `gender` | Enum | ✅ | 성별 ("M": 남성, "F": 여성) |
| `goal` | Enum | ✅ | 목표 ("WEIGHT_LOSS", "WEIGHT_GAIN", "WEIGHT_MAINTENANCE") |
| `height` | BigDecimal | ✅ | 키 (cm) |
| `weight` | BigDecimal | ✅ | 몸무게 (kg) |
| `birthday` | LocalDate | ✅ | 생년월일 (YYYY-MM-DD) |
| `phone` | String | ✅ | 전화번호 (SMS 인증 완료된 번호) |
| `email` | String | ✅ | 이메일 (소셜 로그인 이메일) |
| `provider` | String | ✅ | 소셜 로그인 제공자 ("google") |
| `consentPersonal` | Boolean | ✅ | 개인정보 처리 동의 |
| `locationPersonal` | Boolean | ✅ | 위치정보 처리 동의 |

**처리 과정**
1. **중복 검증**: 이메일, 전화번호 중복 체크
2. **User 엔티티 생성**: 입력 정보로 사용자 생성
3. **신체정보 저장**: UserPhysicalInfo 엔티티에 초기 신체정보 기록
4. **응답 반환**: 생성된 사용자 상세 정보 반환

**Response (성공)**
```json
{
  "timestamp": "2025-05-26T10:30:00",
  "status": "CREATED",
  "message": "회원가입 성공",
  "data": {
    "name": "홍길동",
    "nickname": "길동이",
    "gender": "M",
    "height": 175.5,
    "weight": 70.0,
    "goal": "WEIGHT_LOSS",
    "birthday": "1990-01-01",
    "phone": "01012345678",
    "email": "test@example.com",
    "consentPersonal": true,
    "locationPersonal": true,
    "createdAt": "2025-05-26T10:30:00",
    "updatedAt": "2025-05-26T10:30:00",
    "deletedAt": null,
    "isDeleted": false,
    "notificationEnabled": true
  }
}
```

**에러 응답**
| 상태코드 | 에러 케이스 | 응답 메시지 |
|---------|-----------|-----------|
| **409** | 이메일 중복 | `"이미 가입된 이메일입니다."` |
| **409** | 전화번호 중복 | `"이미 등록된 전화번호입니다."` |

---

### 2. 내 정보 조회

JWT 토큰으로 인증된 사용자의 상세 정보를 조회합니다.

#### `GET /api/users/me`

**Request Headers**
```http
Authorization: Bearer {accessToken}
X-USER-ID: {userId}  # Gateway에서 자동 추가
```

**처리 과정**
1. **사용자 검증**: X-USER-ID 헤더로 사용자 조회
2. **삭제 여부 확인**: isDeleted = false 체크
3. **상세 정보 반환**: 사용자 모든 정보 반환

**Response (성공)**
```json
{
  "timestamp": "2025-05-26T10:30:00",
  "status": "OK",
  "message": "내 정보 조회 성공",
  "data": {
    "name": "홍길동",
    "nickname": "길동이",
    "gender": "M",
    "height": 175.5,
    "weight": 69.5,
    "goal": "WEIGHT_LOSS",
    "birthday": "1990-01-01",
    "phone": "01012345678",
    "email": "test@example.com",
    "consentPersonal": true,
    "locationPersonal": true,
    "createdAt": "2025-05-26T10:30:00",
    "updatedAt": "2025-05-26T11:00:00",
    "deletedAt": null,
    "isDeleted": false,
    "notificationEnabled": true
  }
}
```

**에러 응답**
| 상태코드 | 에러 케이스 | 응답 메시지 |
|---------|-----------|-----------|
| **404** | 사용자 없음 | `"해당 유저를 찾을 수 없습니다."` |
| **400** | 탈퇴한 회원 | `"이미 탈퇴한 회원입니다."` |

---

### 3. 회원정보 수정

사용자의 프로필 정보를 부분적으로 수정합니다.

#### `PUT /api/users`

**Request Headers**
```http
Authorization: Bearer {accessToken}
X-USER-ID: {userId}  # Gateway에서 자동 추가
Content-Type: application/json
```

**Request Body**
```json
{
  "nickname": "새별명",
  "phone": "01087654321",
  "height": 176.0,
  "weight": 69.5,
  "birthday": "1990-06-15",
  "goal": "WEIGHT_MAINTENANCE",
  "notificationEnabled": false
}
```

**Request Body Fields (모두 선택)**
| 필드 | 타입 | 필수 | 설명 |
|-----|------|------|------|
| `nickname` | String | ❌ | 새 별명 (공백 불가) |
| `phone` | String | ❌ | 새 전화번호 (공백 불가) |
| `height` | BigDecimal | ❌ | 새 키 (0보다 커야 함) |
| `weight` | BigDecimal | ❌ | 새 몸무게 (0보다 커야 함) |
| `birthday` | LocalDate | ❌ | 새 생년월일 (미래일 불가) |
| `goal` | Enum | ❌ | 새 목표 |
| `notificationEnabled` | Boolean | ❌ | 알림 허용 여부 |

**처리 과정**
1. **사용자 검증**: 존재하고 삭제되지 않은 사용자 확인
2. **입력값 검증**: 각 필드별 유효성 검사
3. **신체정보 업데이트**: height, weight, birthday, goal 변경 시 UserPhysicalInfo 새 기록 생성
4. **엔티티 업데이트**: 변경된 필드만 업데이트
5. **응답 반환**: 수정된 사용자 정보 반환

**Response (성공)**
```json
{
  "timestamp": "2025-05-26T11:00:00",
  "status": "OK",
  "message": "회원정보 수정 성공",
  "data": {
    "name": "홍길동",
    "nickname": "새별명",
    "gender": "M",
    "height": 176.0,
    "weight": 69.5,
    "goal": "WEIGHT_MAINTENANCE",
    "birthday": "1990-06-15",
    "phone": "01087654321",
    "email": "test@example.com",
    "consentPersonal": true,
    "locationPersonal": true,
    "createdAt": "2025-05-26T10:30:00",
    "updatedAt": "2025-05-26T11:00:00",
    "deletedAt": null,
    "isDeleted": false,
    "notificationEnabled": false
  }
}
```

**에러 응답**
| 상태코드 | 에러 케이스 | 응답 메시지 |
|---------|-----------|-----------|
| **400** | 닉네임 공백 | `"닉네임은 비어있을 수 없습니다."` |
| **400** | 키 0 이하 | `"키는 0보다 커야 합니다."` |
| **400** | 몸무게 0 이하 | `"몸무게는 0보다 커야 합니다."` |
| **400** | 미래 생일 | `"생일은 미래일 수 없습니다."` |

---

### 4. 회원 탈퇴

사용자 계정을 논리적으로 삭제 처리합니다.

#### `DELETE /api/users`

**Request Headers**
```http
Authorization: Bearer {accessToken}
X-USER-ID: {userId}  # Gateway에서 자동 추가
```

**처리 과정**
1. **사용자 검증**: 존재하고 삭제되지 않은 사용자 확인
2. **Soft Delete**: isDeleted = true, deletedAt = 현재시간 설정
3. **데이터 보존**: 실제 데이터는 삭제하지 않고 플래그만 변경

**Response (성공)**
```json
{
  "timestamp": "2025-05-26T12:00:00",
  "status": "OK",
  "message": "회원 탈퇴 처리 완료",
  "data": null
}
```

---

## 📱 SMS 인증 API

### 5. SMS 인증번호 발송

회원가입 시 휴대폰 번호 인증을 위해 6자리 인증번호를 발송합니다.

#### `POST /api/users/verify/phone`

**Request Headers**
```http
Content-Type: application/json
```

**Request Body**
```json
{
  "phone": "01012345678"
}
```

**처리 과정**
1. **중복 체크**: 이미 가입된 전화번호인지 확인
2. **진행 중 요청 체크**: Redis에 기존 인증번호가 있는지 확인
3. **인증번호 생성**: 100,000~999,999 범위 6자리 랜덤 생성
4. **Redis 저장**: `phone:{전화번호}` 키로 3분 TTL 설정
5. **CoolSMS 발송**: 실제 SMS 발송 (현재 활성화)

**Response (성공)**
```json
{
  "timestamp": "2025-05-26T10:25:00",
  "status": "OK",
  "message": "인증번호가 발송되었습니다.",
  "data": null
}
```

**실제 SMS 내용**
```
[디아비서] 인증번호는 [123456] 입니다.
```

**에러 응답**
| 상태코드 | 에러 케이스 | 응답 메시지 |
|---------|-----------|-----------|
| **409** | 이미 가입된 번호 | `"이미 가입된 전화번호입니다."` |
| **400** | 진행 중인 인증 | `"이미 인증 요청이 진행 중입니다. 잠시 후 다시 시도해주세요."` |
| **500** | SMS 발송 실패 | `"SMS 인증번호 전송에 실패했습니다."` |

---

### 6. SMS 인증번호 확인

발송된 인증번호를 검증합니다.

#### `POST /api/users/verify/phone/confirm`

**Request Headers**
```http
Content-Type: application/json
```

**Request Body**
```json
{
  "phone": "01012345678",
  "code": "123456"
}
```

**처리 과정**
1. **실패 횟수 체크**: `phone:fail:{전화번호}` 키로 5회 초과 확인
2. **인증번호 조회**: Redis에서 저장된 코드 조회
3. **코드 비교**: 입력 코드와 저장된 코드 일치 확인
4. **성공 처리**: 인증번호 및 실패 기록 Redis에서 삭제
5. **실패 처리**: 실패 횟수 증가, 5회 초과 시 3분 차단

**Response (성공)**
```json
{
  "timestamp": "2025-05-26T10:28:00",
  "status": "OK",
  "message": "인증 성공",
  "data": null
}
```

**에러 응답**
| 상태코드 | 에러 케이스 | 응답 메시지 |
|---------|-----------|-----------|
| **400** | 시도 횟수 초과 | `"인증 시도 횟수가 초과되었습니다. 잠시 후 다시 시도해주세요."` |
| **400** | 인증번호 만료 | `"인증번호가 만료되었거나 존재하지 않습니다."` |
| **400** | 인증번호 불일치 | `"인증번호가 일치하지 않습니다."` |

**Redis 보안 메커니즘**
```
# 인증번호 저장
Key: phone:01012345678
Value: "123456"
TTL: 180초 (3분)

# 실패 횟수 저장
Key: phone:fail:01012345678
Value: "3"  (실패 횟수)
TTL: 180초 (3분)
```

---

## 🏃‍♂️ 신체정보 관리 API

### 7. 신체정보 저장/수정

특정 날짜의 신체정보를 저장하거나 수정합니다.

#### `POST /api/users/physical-info`

**Request Headers**
```http
Authorization: Bearer {accessToken}
X-USER-ID: {userId}  # Gateway에서 자동 추가
Content-Type: application/json
```

**Request Body**
```json
{
  "height": 176.0,
  "weight": 69.5,
  "birthday": "1990-06-15",
  "goal": "WEIGHT_MAINTENANCE",
  "date": "2025-05-26"
}
```

**Request Body Fields**
| 필드 | 타입 | 필수 | 설명 |
|-----|------|------|------|
| `height` | BigDecimal | ✅ | 키 (cm) |
| `weight` | BigDecimal | ✅ | 몸무게 (kg) |
| `birthday` | LocalDate | ✅ | 생년월일 |
| `goal` | Enum | ✅ | 목표 |
| `date` | LocalDate | ✅ | 기록 날짜 |

**처리 과정**
1. **사용자 검증**: 유효한 사용자인지 확인
2. **기존 기록 확인**: 해당 날짜에 기록이 있는지 체크
3. **저장/수정**: 기존 기록 있으면 수정, 없으면 새로 생성
4. **유니크 제약**: (user_id, date) 조합으로 중복 방지

**Response (성공)**
```json
{
  "timestamp": "2025-05-26T14:00:00",
  "status": "OK",
  "message": "신체 정보 저장 성공",
  "data": null
}
```

---

### 8. 신체정보 조회

특정 날짜 이전의 최신 신체정보를 조회하고 BMR/TDEE를 계산합니다.

#### `GET /api/users/physical-info`

**Request Headers**
```http
Authorization: Bearer {accessToken}
X-USER-ID: {userId}  # Gateway에서 자동 추가
```

**Query Parameters**
| 파라미터 | 타입 | 필수 | 기본값 | 설명 |
|---------|------|------|-------|------|
| `date` | LocalDate | ❌ | 오늘 | 조회 기준 날짜 (YYYY-MM-DD) |

**예시 요청**
```http
GET /api/users/physical-info?date=2025-05-26
```

**처리 과정**
1. **기준 날짜 설정**: date 파라미터가 없으면 오늘 날짜 사용
2. **최신 기록 조회**: 기준 날짜 이전 중 가장 최근 기록
3. **BMR 계산**: Mifflin-St Jeor 방정식 사용
4. **TDEE 계산**: BMR × 1.375 (가벼운 활동량)
5. **권장값 계산**: 목표에 따른 권장 칼로리/운동량

**Response (성공)**
```json
{
  "timestamp": "2025-05-26T15:00:00",
  "status": "OK",
  "message": "신체 정보 조회 성공",
  "data": {
    "height": 176.0,
    "weight": 69.5,
    "birthday": "1990-06-15",
    "goal": "WEIGHT_MAINTENANCE",
    "date": "2025-05-26",
    "age": 34,
    "bmr": 1687,
    "tdee": 2320,
    "recommendedIntake": 2320,
    "recommendedExercise": 348
  }
}
```

**계산 공식**
```
# BMR (기초대사율) - Mifflin-St Jeor 방정식
남성: BMR = 10 × 체중(kg) + 6.25 × 키(cm) - 5 × 나이 + 5
여성: BMR = 10 × 체중(kg) + 6.25 × 키(cm) - 5 × 나이 - 161

# TDEE (총 에너지 소비량)
TDEE = BMR × 1.375 (가벼운 활동량)

# 목표별 권장값
체중 감량: 권장 칼로리 = TDEE - 400, 권장 운동 = TDEE × 0.15 + 150
체중 증량: 권장 칼로리 = TDEE + 250, 권장 운동 = TDEE × 0.15 - 70
체중 유지: 권장 칼로리 = TDEE, 권장 운동 = TDEE × 0.15
```

**에러 응답**
| 상태코드 | 에러 케이스 | 응답 메시지 |
|---------|-----------|-----------|
| **404** | 기록 없음 | `"해당 날짜 이전에 등록된 신체 정보가 없습니다."` |

---

### 9. 신체정보 전체 조회

사용자의 모든 신체정보 기록을 최신순으로 조회합니다.

#### `GET /api/users/physical-info/all`

**Request Headers**
```http
Authorization: Bearer {accessToken}
X-USER-ID: {userId}  # Gateway에서 자동 추가
```

**Response (성공)**
```json
{
  "timestamp": "2025-05-26T15:30:00",
  "status": "OK",
  "message": "신체 정보 전체 조회 성공",
  "data": [
    {
      "height": 176.0,
      "weight": 69.5,
      "birthday": "1990-06-15",
      "goal": "WEIGHT_MAINTENANCE",
      "date": "2025-05-26",
      "age": 34,
      "bmr": 1687,
      "tdee": 2320,
      "recommendedIntake": 2320,
      "recommendedExercise": 348
    },
    {
      "height": 175.5,
      "weight": 70.0,
      "birthday": "1990-06-15",
      "goal": "WEIGHT_LOSS",
      "date": "2025-05-25",
      "age": 34,
      "bmr": 1695,
      "tdee": 2330,
      "recommendedIntake": 1930,
      "recommendedExercise": 500
    }
  ]
}
```

---

## 🔗 서비스 연동 API

### 10. Auth Service 연동 - 사용자 존재 확인

Auth Service에서 소셜 로그인 시 기존 회원 여부를 확인합니다.

#### `GET /api/users/exist`

**Query Parameters**
| 파라미터 | 타입 | 필수 | 설명 |
|---------|------|------|------|
| `email` | String | ✅ | 소셜 로그인 이메일 |
| `provider` | String | ✅ | 소셜 로그인 제공자 ("google") |

**예시 요청**
```http
GET /api/users/exist?email=test@example.com&provider=google
```

**처리 과정**
1. **이메일 + 제공자 조합**: 동일한 이메일이라도 제공자별로 구분
2. **사용자 조회**: findByEmailAndProvider로 조회
3. **존재 여부 반환**: 존재하면 사용자 정보, 없으면 exists=false

**Response (기존 회원)**
```json
{
  "timestamp": "2025-05-26T09:00:00",
  "status": "OK",
  "message": "회원 존재 조회 성공",
  "data": {
    "userId": 123,
    "name": "홍길동",
    "exists": true
  }
}
```

**Response (신규 회원)**
```json
{
  "timestamp": "2025-05-26T09:00:00",
  "status": "OK",
  "message": "회원 존재 조회 성공",
  "data": {
    "userId": null,
    "name": null,
    "exists": false
  }
}
```

---

## 🔔 FCM 토큰 관리 API

### 11. FCM 토큰 등록

앱에서 받은 Firebase 토큰을 등록합니다.

#### `POST /api/users/fcm-token`

**Request Headers**
```http
Authorization: Bearer {accessToken}
X-USER-ID: {userId}  # Gateway에서 자동 추가
Content-Type: application/json
```

**Request Body**
```json
{
  "token": "fGkT8vQ9R0eK3mN5pL2jH7..."
}
```

**Response (성공)**
```json
{
  "timestamp": "2025-05-26T16:00:00",
  "status": "OK",
  "message": "FCM 토큰 등록 성공",
  "data": null
}
```

---

### 12. FCM 토큰 조회

Alert Service에서 푸시 알림 발송을 위해 사용자의 FCM 토큰을 조회합니다.

#### `GET /api/users/{userId}/fcm-token`

**Path Variables**
| 변수 | 타입 | 설명 |
|-----|------|------|
| `userId` | Integer | 사용자 ID |

**Response (성공)**
```json
{
  "fcmToken": "fGkT8vQ9R0eK3mN5pL2jH7..."
}
```

**Response (토큰 없음)**
```json
{
  "fcmToken": null
}
```

---

### 13. 알림 허용 사용자 조회

Alert Service에서 알림을 허용한 사용자 목록을 조회합니다.

#### `GET /api/users/notifications-enabled`

**Response (성공)**
```json
{
  "timestamp": "2025-05-26T17:00:00",
  "status": "OK",
  "message": "알림 허용된 유저 조회 성공",
  "data": [
    {
      "userId": 123,
      "name": "홍길동",
      "nickname": "길동이",
      "updatedAt": "2025-05-26T11:00:00"
    },
    {
      "userId": 124,
      "name": "김영희",
      "nickname": "영희야",
      "updatedAt": "2025-05-26T12:00:00"
    }
  ]
}
```

---

## 🏋️‍♂️ Health Service 연동 API

### 14. 신체 구성 정보 업데이트

Health Service에서 체성분 변화 시 User Service에 알림합니다.

#### `POST /api/users/body-composition`

**Request Body**
```json
{
  "userId": 123,
  "height": 176.0,
  "weight": 68.5
}
```

**처리 과정**
1. **사용자 검증**: 유효한 사용자인지 확인
2. **신체정보 업데이트**: 키, 몸무게 정보 반영
3. **자동 저장**: 변경사항을 User 엔티티에 저장

**Response**
```
HTTP 200 OK (본문 없음)
```

---

## 📊 API 사용 시나리오

### 1. 신규 회원가입 플로우
```
1. Android App → Auth Service: OAuth 소셜 로그인
   POST /api/auth/oauth/login
   
2. Auth Service → User Service: 기존 회원 확인
   GET /api/users/exist?email=test@example.com&provider=google
   → Response: { "exists": false }
   
3. Auth Service → Android App: 신규 회원 응답
   → Response: { "isNewUser": true, "accessToken": null }
   
4. Android App → User Service: SMS 인증번호 요청
   POST /api/users/verify/phone
   → CoolSMS 발송: "[디아비서] 인증번호는 [123456] 입니다."
   
5. Android App → User Service: SMS 인증번호 확인
   POST /api/users/verify/phone/confirm
   → Response: "인증 성공"
   
6. Android App → User Service: 회원가입
   POST /api/users/signup
   → User 엔티티 생성 + UserPhysicalInfo 초기 기록
   
7. Android App → Auth Service: 재로그인으로 토큰 획득
   POST /api/auth/oauth/login
   → Response: { "isNewUser": false, "accessToken": "...", "refreshToken": "..." }
```

### 2. 기존 회원 로그인 플로우
```
1. Android App → Auth Service: OAuth 소셜 로그인
   POST /api/auth/oauth/login
   
2. Auth Service → User Service: 기존 회원 확인
   GET /api/users/exist?email=test@example.com&provider=google
   → Response: { "userId": 123, "name": "홍길동", "exists": true }
   
3. Auth Service → Android App: 토큰 발급
   → Response: { "isNewUser": false, "accessToken": "...", "refreshToken": "..." }
   
4. Android App → User Service: 프로필 조회
   GET /api/users/me (Authorization: Bearer token)
   → 사용자 상세 정보 반환
```

### 3. 프로필 수정 플로우
```
1. Android App → User Service: 현재 프로필 조회
   GET /api/users/me
   
2. 사용자가 정보 수정 (키, 몸무게, 목표 등)
   
3. Android App → User Service: 프로필 수정
   PUT /api/users
   → User 엔티티 업데이트 + UserPhysicalInfo 새 기록 생성
   
4. Android App → User Service: 수정된 프로필 조회
   GET /api/users/me
```

### 4. 신체정보 관리 플로우
```
1. 회원가입 또는 프로필 수정 시 자동 생성:
   UserService.saveIfRelevantInfoChanged() 호출
   → UserPhysicalInfo 엔티티에 새 기록 저장
   
2. Android App → User Service: 오늘의 신체정보 조회
   GET /api/users/physical-info
   → BMR/TDEE/권장칼로리 계산하여 반환
   
3. Android App → User Service: 신체정보 이력 조회
   GET /api/users/physical-info/all
   → 날짜별 변화 추이 확인
   
4. Android App → User Service: 특정 날짜 신체정보 저장
   POST /api/users/physical-info
   → 운동/식단 기록과 연동 가능
```

### 5. FCM 푸시 알림 플로우
```
1. Android App → User Service: FCM 토큰 등록
   POST /api/users/fcm-token
   → 앱 설치/업데이트 시 새 토큰 등록
   
2. Alert Service → User Service: 알림 허용 사용자 조회
   GET /api/users/notifications-enabled
   → 알림 설정된 사용자들만 반환
   
3. Alert Service → User Service: FCM 토큰 조회
   GET /api/users/{userId}/fcm-token
   → 특정 사용자에게 푸시 발송 시 사용
   
4. Alert Service → Firebase: 실제 푸시 발송
   → 운동 알림, 식사 알림 등
```

---

## 🧪 테스트 가이드

### 주요 기능 테스트 포인트

#### 1. 회원가입 플로우 검증
- **SMS 인증 → 회원가입 → 토큰 발급** 전체 과정 검증
- 인증번호 5회 실패 시 3분 차단 기능
- 중복 이메일/전화번호 방지 로직

#### 2. 프로필 관리 검증  
- 조회 → 수정 → 신체정보 자동 업데이트 연동
- BMR/TDEE 계산 정확성 (Mifflin-St Jeor 방정식)
- 입력값 검증 (키/몸무게 양수, 미래 생일 차단 등)

#### 3. 서비스 연동 검증
- Auth Service: 사용자 존재 확인 API
- Gateway: X-USER-ID 헤더 처리
- Alert Service: FCM 토큰 관리
- Health Service: 신체 구성 동기화

### 중요 검증 사항

#### SMS 인증 보안
- Redis TTL 3분 자동 만료
- 실패 횟수 제한 (5회 초과 시 차단)
- 중복 발송 방지 메커니즘

#### 데이터 무결성
- 신체정보 날짜별 유니크 제약 (user_id, date)
- Soft Delete 정상 동작 (isDeleted 플래그)
- 트랜잭션 롤백 (SMS 발송 실패 시 Redis 정리)

#### 성능 최적화
- 사용자 조회 응답시간 (50-100ms 목표)
- SMS 발송 응답시간 (3-5초, 비동기 처리 고려)
- 신체정보 계산 성능 (복잡한 쿼리 최적화)

---

## 💾 Redis 데이터 구조

### SMS 인증 관련 키
```redis
# 인증번호 저장
SETEX phone:01012345678 180 "123456"
# Key: phone:{전화번호}
# Value: 6자리 인증번호
# TTL: 180초 (3분)

# 실패 횟수 저장
SETEX phone:fail:01012345678 180 "3"
# Key: phone:fail:{전화번호}
# Value: 실패 횟수
# TTL: 180초 (3분)
```

### Redis 명령어로 확인
```bash
# 현재 저장된 인증번호 확인
redis-cli GET phone:01012345678

# 실패 횟수 확인
redis-cli GET phone:fail:01012345678

# TTL 확인 (남은 만료시간)
redis-cli TTL phone:01012345678

# 모든 인증 관련 키 조회
redis-cli KEYS phone:*
```

---

## 🔄 서비스 간 연동 명세

### Auth Service → User Service
| API | 목적 | 호출 시점 |
|-----|------|----------|
| `GET /api/users/exist` | 소셜 로그인 시 기존 회원 확인 | OAuth 로그인 요청 시 |

**연동 플로우:**
```
Auth Service: 
1. Google ID Token 검증 → 이메일 추출
2. User Service API 호출: GET /api/users/exist?email=xxx&provider=google
3. 응답에 따라 토큰 발급 여부 결정
```

### Gateway → User Service
| 헤더 | 값 | 목적 |
|-----|----|----- |
| `X-USER-ID` | JWT에서 추출한 사용자 ID | 사용자 식별 |

**연동 플로우:**
```
Gateway:
1. JWT Access Token 검증
2. 토큰에서 userId 추출
3. X-USER-ID 헤더로 User Service에 전달

User Service:
1. @RequestHeader("X-USER-ID") Integer userId로 수신
2. 해당 사용자 권한으로 API 처리
```

### Alert Service → User Service
| API | 목적 | 호출 시점 |
|-----|------|----------|
| `GET /api/users/notifications-enabled` | 알림 허용 사용자 목록 | 푸시 알림 배치 작업 시 |
| `GET /api/users/{userId}/fcm-token` | FCM 토큰 조회 | 개별 푸시 발송 시 |

### Health Service → User Service
| API | 목적 | 호출 시점 |
|-----|------|----------|
| `POST /api/users/body-composition` | 신체 구성 정보 동기화 | 체성분 변화 감지 시 |

---

## 📊 성능 고려사항

### 응답 시간 최적화
| API | 예상 응답시간 | 병목 구간 | 최적화 방안 |
|-----|--------------|----------|-----------|
| `POST /api/users/verify/phone` | 3-5초 | CoolSMS API 호출 | 비동기 처리 |
| `GET /api/users/me` | 50-100ms | DB 조회 | 캐싱 도입 |
| `PUT /api/users` | 100-200ms | 신체정보 계산 | 계산 로직 최적화 |
| `GET /api/users/physical-info` | 80-150ms | 복잡한 쿼리 | 인덱스 최적화 |

### DB 쿼리 최적화
```sql
-- 자주 사용되는 쿼리들
-- 1. 사용자 존재 확인 (Auth Service 연동)
SELECT user_id, name FROM user_tb 
WHERE email = ? AND provider = ?;

-- 2. 신체정보 최신 기록 조회
SELECT * FROM user_physical_info_tb 
WHERE user_id = ? AND date <= ? 
ORDER BY date DESC LIMIT 1;

-- 권장 인덱스
CREATE INDEX idx_user_email_provider ON user_tb(email, provider);
CREATE INDEX idx_physical_user_date ON user_physical_info_tb(user_id, date DESC);
```

### 메모리 사용 최적화
- **DTO 최적화**: 불필요한 필드 제거로 메모리 절약
- **지연 로딩**: @ManyToOne 관계에서 필요시에만 조회
- **페이징**: 대용량 데이터 조회 시 페이징 적용

---

## 🛡️ 보안 및 검증

### 입력값 검증
```java
// 회원가입 시 검증 사항
- 이메일 형식: 정규식 검증
- 전화번호 형식: 01X-XXXX-XXXX 패턴
- 키/몸무게: 양수 값만 허용
- 생년월일: 미래일 불가, 너무 과거일 불가
- 닉네임: 빈 문자열 불가, 길이 제한
```

### API 보안
```bash
# 인증 불필요 API (공개)
/api/users/signup
/api/users/verify/**
/api/users/exist

# 인증 필요 API (JWT 토큰)
/api/users/me
/api/users (PUT, DELETE)
/api/users/physical-info/**
/api/users/fcm-token

# 내부 서비스 전용 API
/api/users/notifications-enabled
/api/users/{userId}/fcm-token
/api/users/body-composition
```

### 개인정보 보호
- **로깅 주의**: 전화번호, 이메일 등 개인정보 로그 출력 금지
- **Soft Delete**: 탈퇴 시 데이터 완전 삭제하지 않고 플래그 처리
- **암호화 권장**: 향후 전화번호, 이메일 암호화 저장 고려

---

## 📝 개발 참고사항

### 현재 SMS 발송 상태
```java
// SmsService.java - 실제 발송 활성화
public void sendSms(String to, String code) {
    // CoolSMS API 실제 호출
    messageService.sendOne(new SingleMessageSendingRequest(message));
}

// 개발용 코드 (주석 처리됨)
// public String sendVerificationCode(String phone) {
//     return code; // 인증번호 직접 반환
// }
```

### CoolSMS 계정 정보 (하드코딩 상태)
```yaml
# 보안 개선 필요
coolsms:
  api-key: NCSJTLVYLEHGKS7D
  api-secret: NSHGJP6JGLYJCMK8GJXFKNXJNFGD6KON
  from-number: 01028992564
```

### 데이터베이스 스키마
```sql
-- 현재 사용 중인 테이블
user_tb: 사용자 기본 정보
user_physical_info_tb: 신체정보 이력

-- 제약조건
- user_tb.phone: UNIQUE
- user_physical_info_tb: UNIQUE(user_id, date)
```