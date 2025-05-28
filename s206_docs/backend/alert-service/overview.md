# 📣 디아비서(DiaViseo) 알림 서비스

사용자에게 **운동 피드백**, **식단 알림** 등 다양한 정보를 **푸시 알림** 형태로 전달합니다.

---

## 📌 서비스 개요

### 🔧 역할과 책임

- **사용자 맞춤 알림**(운동, 식단, 체성분 변동 등) 전송
- Firebase Cloud Messaging(FCM)을 통한 디바이스 푸시
- RabbitMQ 기반 **비동기 큐 처리**로 확장성 확보
- 알림 이력 저장 및 상태(읽음/삭제) 관리

### 🧩 MSA 구조 내 위치

```
다른 서비스 → RabbitMQ (Exchange/Queue)
↓ (Listener)
Alert Service → Firebase Push
```

> Alert Service는 **RabbitMQ Listener**로 큐 메시지를 수신한 뒤  
> `FcmService`를 통해 Firebase API에 요청을 보내 사용자 디바이스로 푸시 알림을 전달합니다.

---

## ⚙️ 기술 스택 & 의존성

| 구분 | 기술 | 버전 | 용도 |
|------|------|------|------|
| **Framework** | Spring Boot | 3.x | REST API |
| **Messaging** | RabbitMQ | - | 비동기 알림 큐 |
| **Push** | Firebase Cloud Messaging | - | 모바일 푸시 |
| **Persistence** | Spring Data JPA + MySQL | - | 알림 기록 저장 |
| **Config** | Spring Cloud Config | - | 중앙 설정 관리 |
| **Service Discovery** | Eureka Client | - | 마이크로서비스 등록 |

### 🔗 주요 의존성

- **User Service**: 사용자 정보 및 FCM 토큰 조회
- **Common Module**: ResponseDto, 공통 예외처리
- **RabbitMQ**: 알림 메시지 큐 관리

---

## 🧠 핵심 기능

1. **알림 조회 및 읽음 처리**
    - 페이지네이션 기반 목록 조회
    - 단일 및 전체 알림 읽음 처리

2. **알림 삭제**
    - 단건 및 전체 알림 삭제 지원

3. **FCM 전송**
    - `FcmService` → Firebase API 호출
    - 전송 결과 Notification 엔티티에 저장

4. **비동기 메시지 소비**
    - `PushNotificationConsumer`가 MQ 메시지를 수신하고 푸시 전송 수행

---

## 🏗️ 시스템 아키텍처

```
┌─────────────────────────────────────────────┐
│                Alert Service                │
├─────────────────────────────────────────────┤
│ Controller                                  │
│ └─ NotificationController                   │
├─────────────────────────────────────────────┤
│ Service                                     │
│ ├─ NotificationService                      │
│ └─ FcmService                               │
├─────────────────────────────────────────────┤
│ Consumer                                    │
│ └─ PushNotificationConsumer                 │
├─────────────────────────────────────────────┤
│ Config                                      │
│ ├─ RabbitConfig                             │
│ └─ FirebaseConfig                           │
├─────────────────────────────────────────────┤
│ Repository / Entity / Mapper                │
│ ├─ NotificationRepository                   │
│ ├─ Notification (Entity)                    │
│ └─ NotificationMapper                       │
└─────────────────────────────────────────────┘
```

---

## 🔁 알림 전송 플로우

### 1. 비동기 발송

1. 다른 서비스 → RabbitMQ로 알림 메시지 발행
2. `PushNotificationConsumer`가 메시지 수신
3. `UserClient`로 FCM 토큰 조회
4. `FcmService` → Firebase API 호출
5. `NotificationService` → 알림 정보 DB 저장

### 2. 동기 API 처리

1. App → Alert Service REST 호출
2. Controller 처리
3. NotificationService → 비즈니스 로직 수행

---

## 🧾 주요 클래스

### 🎮 Controller Layer

| 클래스 | 책임 | 주요 메서드 |
|--------|------|-------------|
| `NotificationController` | 알림 CRUD API | `getNotifications`, `markAsRead`, `deleteNotification` |

### 🛠️ Service Layer

| 클래스 | 책임 | 주요 메서드 |
|--------|------|-------------|
| `NotificationService` | 알림 DB/상태 로직 | `getUserNotifications`, `markAllAsRead` |
| `FcmService` | FCM 전송 로직 | `sendMessageTo` |

### 📬 Consumer

| 클래스 | 책임 | 주요 메서드 |
|--------|------|-------------|
| `PushNotificationConsumer` | MQ 메시지 수신 및 푸시 전송 | `listen` |

### ⚙️ Config

| 클래스 | 책임 |
|--------|------|
| `RabbitConfig` | Exchange 및 Queue 선언 |
| `FirebaseConfig` | FirebaseApp 초기화 설정 |

---

## 🌐 REST API 명세

| Method | Path | 설명 |
|--------|------|------|
| GET | `/api/notifications` | 로그인 사용자의 알림 목록 조회 (`page`, `size` 지원) |
| PATCH | `/api/notifications/{id}/read` | 특정 알림 읽음 처리 |
| PATCH | `/api/notifications/read-all` | 전체 알림 읽음 처리 |
| DELETE | `/api/notifications/{id}` | 특정 알림 삭제 |
| DELETE | `/api/notifications` | 사용자 알림 전체 삭제 |

---

## 📤 RabbitMQ 메시지 포맷

알림 이벤트는 **`notification.exchange`** 로 발행되며  
**`notification.queue`** 로 라우팅됩니다.

```json
{
  "notificationId": 1001,
  "userId": 42,
  "message": "오늘 식단을 아직 기록하지 않으셨어요!",
  "notificationType": "PUSH",
  "isRead": false,
  "sentAt": "2025-05-28T13:00:00+09:00"
}
```

→ PushNotificationConsumer → FCM 토큰 조회 → FcmService 푸시 전송 →  
→ NotificationService DB 영속화

---

## 🗄️ Notification 엔티티 스키마

┌──────────────────────┬────────────────────────────┬────────────────────────────────────────────┐
│ 필드                 │ 타입                       │ 설명                                       │
├──────────────────────┼────────────────────────────┼────────────────────────────────────────────┤
│ notificationId       │ Long                       │ PK, auto-increment                         │
│ userId               │ Integer                    │ 수신자 ID                                  │
│ notificationType     │ Enum (NotificationType)    │ 알림 유형 (DIET, WORKOUT 등)              │
│ title                │ String                     │ 알림 제목 (선택적)                         │
│ message              │ String                     │ 알림 내용                                  │
│ sentAt               │ LocalDateTime              │ 전송 시각 (자동 생성)                      │
│ isRead               │ Boolean                    │ 읽음 여부 (기본값: false)                  │
│ isDeleted            │ Boolean                    │ 삭제 여부 (기본값: false)                  │
└──────────────────────┴────────────────────────────┴────────────────────────────────────────────┘
---

## 📦 주요 DTO

| 패키지 | 클래스 | 설명 |
|--------|--------|------|
| `client.dto.response` | `FcmTokenResponse` | UserService로부터 받은 FCM 토큰 |
| `data.request` | `NotificationCreateMessage` | MQ 발행용 알림 DTO |
| `data.response` | `NotificationResponse` | REST API 응답용 알림 DTO |

---

## 🔐 보안 및 안정성

- **재시도 로직**: FCM 실패 시 지수 백오프 적용 예정
- **장애 격리**: MQ 버퍼링으로 푸시 실패 시 서비스 영향 최소화
- **인증 처리**: Gateway에서 JWT 인증 후 `X-USER-ID` 헤더 전달

---

## ⚙️ 환경 변수

| 변수명 | 설명 | 예시 |
|--------|------|------|
| `spring.rabbitmq.host` | MQ 호스트 | `rabbitmq` |
| `spring.rabbitmq.port` | MQ 포트 | `5672` |
| `firebase.credentials.path` | Firebase 인증 JSON 경로 | `/app/firebase.json` |
| `eureka.client.service-url.defaultZone` | Eureka 주소 | `http://eureka:8761/eureka` |

---

## 🚀 확장 가능성

- 알림 우선순위·채널별 설정 기능
- Slack, Email 등 추가 채널 통합
- Redis 캐싱을 통한 조회 성능 최적화
