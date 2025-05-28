# AI Service API 명세

**AI Service는 운동, 식단, 체성분 관련 데이터를 기반으로 GPT 기반 챗봇 피드백, 문서 검색, 예측 결과를 제공하는 서비스입니다.**

---

## 📌 API 개요

### Base URL
```
http://ai-service/api
```

### AI Service의 역할
- GPT 기반 챗봇 피드백 제공
- 운동/식단/체성분 데이터 분석
- RAG 기반 문서 검색
- 체중 예측 등 ML 모델 호출

---

## 🤖 1. 챗봇 세션 시작

#### `POST /api/chatbot/start-session`
```http
Authorization: Bearer {accessToken}
Content-Type: application/json
```
```json
{
  "chatbot_type": "workout"
}
```

---

## 💬 2. 챗봇 메시지 전송

#### `POST /api/chatbot/chat/{sessionId}`
```http
X-USER-ID: {userId}
Content-Type: application/json
```
```json
{
  "message": "오늘 운동은 어떻게 하면 좋을까?"
}
```

---

## 🗂️ 3. 챗봇 세션 목록 조회

#### `GET /api/chatbot/chats`
```http
X-USER-ID: {userId}
```

---

## 🗨️ 4. 세션 메시지 히스토리 조회

#### `GET /api/chatbot/messages/{sessionId}`

---

## 🔚 5. 챗봇 세션 종료

#### `DELETE /api/chatbot/end-session`
```json
{
  "session_id": "session_1_workout_2025-05-16 01:57:08.049764"
}
```

---

## ⚖️ 6. 체중 트렌드 피드백 API

#### `GET /api/chatbot/weight/trend?date={YYYY-MM-DD}&days={n}`

```http
Authorization: Bearer {accessToken}
```

**Response**
```json
{
  "feedback": "1️⃣ 최근 7일간 체중이 -0.30kg 감소했으나, 근육량은 소폭 증가했습니다..."
}
```

---

## 📜 7. 과거 피드백 내역 조회

#### `GET /api/chatbot/feedback/{feedback_type}?date={YYYY-MM-DD}`

**Headers**
```http
Authorization: Bearer {accessToken}
```

**Path Parameters**

- `feedback_type`: `workout`, `weight_trend`, `nutrition` 중 하나

**Query Parameters**

- `date` (optional): YYYY-MM-DD 형식 (기본값: 오늘 날짜)

**Response**
```json
"1️⃣ 최근 7일간 체중이 -0.20kg 감소했으나, 근육량은 유지되고 있습니다..."
```

**Error**
```json
{
  "detail": "Invalid user_id or user not found"
}
```
또는
```json
{
  "detail": "Invalid feedback_type. Must be one of: workout, weight_trend, nutrition"
}
```

---
