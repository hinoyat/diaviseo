# 🤖 챗봇 기능 흐름 문서 (ChatBot Feature)

디아비서(DiaViseo) 앱의 챗봇 기능은 사용자가 식단이(영양), 운동이(운동) 두 AI 캐릭터와 대화할 수 있도록 구성되어 있습니다. 세션 기반으로 동작하며, 사용자는 캐릭터를 선택한 후 질문하고 답변을 받을 수 있습니다. 모든 대화는 히스토리로 저장되어 언제든지 다시 확인할 수 있습니다.

---

## 📌 기능 개요

| 항목 | 설명 |
|------|------|
| 챗봇 캐릭터 | 1) 식단이 (nutrition) - 영양, 칼로리 상담 2) 운동이 (workout) - 운동 루틴, 피드백 |
| 세션 관리 | REST API 기반 세션 시작/종료, 중단된 대화 재개 지원 |
| 메시지 기능 | 일반 텍스트, 초기 질문 버튼, 자세히 모드 |
| 히스토리 | 과거 대화 조회, 토픽별 필터링, 종료된 대화 구분 |

---

## 🧩 ViewModel 구조: `ChatBotViewModel`

| 상태 변수 | 설명 |
|----------|------|
| `messages: StateFlow<List<ChatMessage>>` | 현재 세션의 메시지 리스트 |
| `isTyping: StateFlow<Boolean>` | 챗봇 응답 대기 상태 |
| `sessionId: StateFlow<String?>` | 현재 활성 세션 ID |
| `isSessionEnded: StateFlow<Boolean>` | 세션 종료 여부 |
| `histories: StateFlow<List<ChatHistory>>` | 과거 대화 기록 |

### 주요 메서드

- **세션 관리**: `startSession(type, characterImageRes)` / `endSession()`
- **메시지 처리**: `sendMessage(text)` / `loadMessages(sessionId, characterImageRes, isEnded)`
- **히스토리 관리**: `fetchHistories()`
- **UI 제어**: `removeInitialQuestionButtons()`

---

## 🔄 API 연동 흐름

### 1. 세션 시작 API

- **Method**: `POST /chat/start-session`
- **요청**: `StartSessionRequest(type)` - `"nutrition"` 또는 `"workout"`
- **응답**: `session_id`, `welcome_message` 포함
- **처리**: 환영 메시지 + 초기 질문 버튼 자동 표시

### 2. 메시지 전송 API

- **Method**: `POST /chat/{sessionId}/message`
- **요청**: `ChatRequest(text)`
- **응답**: `content`, `timestamp` 포함
- **특별 처리**: "자세히 모드"시 메시지 앞에 "자세히" 접두어 추가

### 3. 세션 종료 API

- **Method**: `POST /chat/end-session`
- **요청**: `EndSessionRequest(sessionId)`

### 4. 히스토리 조회 API

- **Method**: `GET /chat/sessions`
- **응답**: 세션 목록 (session_id, chatbot_type, started_at, ended_at)

### 5. 메시지 기록 조회 API

- **Method**: `GET /chat/{sessionId}/messages`
- **응답**: 메시지 목록 (content, role, timestamp)

---

## 🎨 주요 화면 구성

### `ChatScreen`

- **상단**: `ChatTopBar` (뒤로가기 + 종료 버튼)
- **메시지 영역**: `LazyColumn` + 자동 스크롤
- **입력 영역**: `ChatInputBar` + 자세히 모드 버튼
- **초기 화면**: 세션 없을 때 `FixedIntroScenario` (캐릭터 선택)

### `ChatHistoryScreen`

- **필터 탭**: 전체/식단이/운동이 분류
- **새 대화 버튼**: "+ 새로운 대화 시작"
- **히스토리 리스트**: `ChatHistoryCard` (종료 여부 표시)

### 주요 컴포넌트들

- `ChatMessageBubble`: 메시지 말풍선 (사용자/봇 구분, 캐릭터 이미지)
- `InitialQuestionButtons`: 토픽별 초기 질문 리스트
- `TypingIndicator`: 웨이브 애니메이션 타이핑 표시
- `ExitChatDialog`: 대화 종료 확인

---

## 💡 특별 기능

### 자세히 모드

- **활성화**: 입력창 포커스시 버튼 표시
- **동작**: 메시지 앞에 "자세히" 접두어 자동 추가
- **UI**: 툴팁으로 기능 설명 (3초 후 자동 숨김)

### 초기 질문 시스템

- **식단이**: "맘모스빵 영양성분은?", "20대 남자 평균 칼로리는?" 등
- **운동이**: "운동 루틴 추천해줘", "오늘 운동 피드백 줘" 등
- **처리**: 선택시 해당 텍스트로 메시지 자동 전송

### 메시지 표시 최적화

- **날짜 구분선**: "오늘", "어제", "yyyy.MM.dd" 형식
- **자동 스크롤**: 새 메시지시 하단으로 이동
- **"자세히" 숨김**: UI에서 접두어 제거하여 표시

---

## 🛠 예외 처리

- **네트워크 에러**: `HttpException` 파싱하여 사용자 친화적 메시지 표시
- **빈 히스토리**: 토픽별 안내 메시지 표시
- **세션 없음**: 캐릭터 선택 화면 표시
- **종료된 세션**: 입력창 비활성화, 조회만 가능

---

## 📱 화면 전환 흐름

```
ChatHistoryScreen (과거 대화 목록)
│
├─ 새로운 대화 시작 → ChatScreen (캐릭터 선택)
├─ 기존 대화 선택 → ChatScreen (메시지 로드)
│
ChatScreen (대화 화면)
│
├─ 대화 종료 → ChatHistoryScreen 복귀
└─ 뒤로가기 → 이전 화면 복귀
```
