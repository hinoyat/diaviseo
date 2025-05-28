# DiaViseo AI System - 종합 개발자 문서


**디아비서(DiaViseo) AI 챗봇 서비스**는 사용자의 건강 데이터를 기반으로 맞춤형 피드백과 조언을 제공하는 대화형 AI 서비스입니다.  
FastAPI 기반의 독립 마이크로서비스로 구성되며, GPT API, Fine-tuned T5 모델, 체중 예측 ML 모델 등을 활용해 다양한 건강 인사이트를 생성합니다.

---

## 📋1. 서비스 개요 및 역할 정의
### 핵심 목적

- **개인 맞춤형 헬스 피드백 제공**  
  - 운동/식단/체성분 데이터를 바탕으로 LLM이 자연어 피드백 생성

- **대화형 건강 상담 시스템 구축**  
  - 세션 기반 사용자 챗 인터페이스 지원 (LangChain Memory 사용)

- **체중 변화 예측 및 조언 자동화**  
  - 체성분 기반 ML 예측 → GPT 기반 설명 메시지 자동 생성



### 🔍 주요 기능 요약

| 기능 구분        | 설명                          |
|------------------|-----------------------------|
| **GPT 챗봇**      | 운동·건강 관련 자유 질의응답            |
| **T5 식단 챗봇**   | 음식 영양정보 및 건강정보 제공           |
| **체중 예측**     | 60일 기준 체성분 기반 체중 변화 예측      |
| **세션 기반 관리** | 사용자별 챗 세션 생성/조회/종료 관리       |
| **영양 정보 분석** | 음식명 기반 영양소 분석, 칼로리/영양 조언 제공 |
| **피드백 기록**   | 생성된 피드백을 DB에 자동 저장 및 조회 가능  |



### 🧩 연동 대상 데이터

- 사용자 체성분 (키, 체중, 골격근량, 체지방률 등)
- 운동 기록 (운동 종류, 칼로리 소모 등)
- 식단 정보 (음식명, 영양소, 사용자 식단 등)
- 대화 이력 (MongoDB 기반 LangChain Memory)
- 예측 모델 결과 (체중 예측값 및 추세)


---

## 🏠 2. 시스템 아키텍처

AI 챗봇 서비스는 FastAPI 기반의 독립 서비스로, 다른 MSA 서비스들과 Eureka를 통해 통합됩니다.  
서비스는 GPT API, T5 모델, FAISS RAG, 체중 예측 모델 등 다양한 AI 요소와 연동되어 사용자 입력을 분석하고 맞춤형 피드백을 생성합니다.


### 전체 아키텍처 개요
```
사용자
↓
[ FastAPI Router (/api/chatbot/*) ]
↓
[ Business Layer (services/chat, nutrition, workout) ]
↓
├─ LangChain Chain Dispatcher
│ ├─ ConversationalRetrievalChain (Memory + RAG)
│ ├─ ToolAgent (예측/분석 도구)
│ └─ T5Wrapper (식단 피드백 템플릿 생성)
│
├─ GPT API 호출 (운동 질문 응답)
├─ T5 모델 호출 (식단 피드백 생성)
├─ 체중 예측 모델 서버 호출 (60일 예측)
└─ MongoDB / MySQL / Redis 저장
```

### 주요 컴포넌트 구성

| 컴포넌트 | 설명                                        |
|---------|-------------------------------------------|
| **FastAPI App** | `/main.py`에서 FastAPI 앱 초기화 및 라우터 등록       |
| **EurekaClient** | MSA 내 위치 인식 및 헬스체크 통합 (service discovery) |
| **Router** | `/api/chatbot` 경로 하위에 모든 기능 통합            |
| **LangChain** | RAG + 툴 + 메모리 + LLM 체인 관리                 |
| **GPT API** | 운동/식단 챗봇 질의응답 (gpt-4o 호출)                 |
| **T5 모델** | Fine-tuned 모델로 식단 피드백 생성                  |
| **ML 예측 모델** | 체성분 기반 체중 예측 (외부 서버로 POST 요청)             |
| **MongoDB** | LangChain Memory, Feedback 저장             |
| **MySQL** | 사용자 정보, 체성분, 식단, 운동 기록 저장                 |
| **FAISS + PDF 문서** | 영양학 정보 검색 (PDF 기반 벡터 인덱스 사용)              |
| **Redis (옵션)** | 응답 캐시, 토큰 관리 등 (명시적 사용 시)                 |


### 서비스 시작 시 초기화

FastAPI 앱 `lifespan`에서 다음 작업이 수행됩니다:

1. **Eureka 등록**  
   → `EurekaClient.start()`

2. **T5 모델 로드**  
   → `init_model()` → `DietModel` 로드 및 양자화 적용

3. **RAG 초기화**  
   → `init_rag()`  
   → MinIO에서 인덱스 다운로드 or fallback 재생성 → FAISS Retriever 구성

   

---

## 📊 3. 비즈니스 로직 및 핵심 모듈 설명

AI 챗봇 서비스는 사용자의 요청을 세션 정보에 따라 GPT, RAG, T5, 체중 예측 모델 등으로 라우팅합니다.  
LangChain 기반의 memory 구조를 사용하여 대화 맥락을 유지하며, MongoDB와 MySQL 기반의 데이터 조회를 통해 개인화된 응답을 생성합니다.



### Chat 세션 흐름 (`chat_service.py`)

| 모듈 | 역할 |
|------|------|
| `chat_with_session()` | 세션 ID 기반 사용자 메시지 처리 |
| `_handle_workout_chat()` | 운동 챗봇 요청 → GPT + 체성분 기반 운동 피드백 |
| `_handle_nutrition_chat()` | 식단 챗봇 요청 → T5 모델 또는 RAG 호출 |
| `_handle_general_chat()` | 일반 GPT 대화 (Memory 기반 응답) |
| `get_chat_history()` | MongoDB에서 세션별 대화 이력 조회 |

> LangChain Memory는 `ConversationBufferMemory + MongoDBChatMessageHistory` 구조로 관리됩니다.



### 운동 피드백 로직 (`feedback.py`, `workout_service.py`)

- 사용자 메시지를 받아 체성분과 목표 정보를 조회  
- GPT 프롬프트를 생성하여 `gpt-4o-mini`로 피드백 응답 생성
- 필요 시 MySQL에서 체중 추이 예측값, 남은 칼로리 정보 등을 추가 조회
- 피드백은 MongoDB에 저장



### 체중 예측 기능 (`/weight/predict`)

- 사용자 신체 정보와 운동 데이터를 모델 서버에 전송
- 60일 후 예상 체중값을 수신하고 변화량 계산
- 예측 실패 시 TDEE 기반 계산으로 fallback 처리



### 식단 피드백 흐름 (`nutrition_chat_service.py`)

| 조건 | 응답 방식 |
|------|-----------|
| `"자세히"` 키워드 포함 | RAG 기반 문서 검색 응답 |
| 영양 키워드 포함 | Fine-tuned T5 모델 응답 |
| 조건 불충족 시 | 안내 메시지 반환 ("자세히"  필요)

- RAG는 FAISS + HuggingFace 임베딩 + MinIO 인덱스 또는 로컬 fallback으로 구성
- T5는 `pko-t5-large-v2`를 양자화 후 파이프라인 래핑 (`get_t5_langchain_llm()`)



### 식단 계산 및 조언 생성 (`generate_advice_service.py`)

- 하루 섭취 칼로리, 탄단지 비율, 당류/콜레스테롤 기준치 초과 여부 분석
- 규칙 기반 피드백 템플릿 조합 (랜덤 샘플링 방식)
- 조합된 메시지를 GPT 없이 바로 응답



### 피드백 조회 및 저장

| 기능 | 모듈                             |
|------|--------------------------------|
| 피드백 저장 | `insert_feedback()` in MongoDB |
| 피드백 조회 | `get_feedback()` → feedback_tb |



### Memory 구성

- LangChain의 `ConversationBufferMemory` + `MongoDBChatMessageHistory` 조합
- 세션 ID별로 대화 이력 저장 및 추론 시 참조

---


## 🤖4. 모델 설명

AI 챗봇 시스템은 총 4종의 주요 모델을 조합하여 동작합니다.  
각 모델은 사용자의 질문 유형과 세션 타입에 따라 자동 분기되며, 필요 시 fallback 전략도 포함되어 있습니다.



### GPT API (OpenAI)

- **모델명**: `gpt-4o`, `gpt-4o-mini`
- **용도**: 
  - 운동 관련 자유 질문 응답
  - 체성분 기반 운동 피드백 생성
  - 체중 변화 트렌드 분석 결과 설명
- **호출 방식**: LangChain `ChatOpenAI` (API Key 기반)
- **초기화 위치**: 각 서비스 내 개별 인스턴스 생성
- **특징**: 사용자 체성분 데이터 기반 프롬프트 커스터마이징

---

### T5 기반 식단 피드백 생성기

- **모델명**: `Sseolily/pko-t5-large-v2` (Fine-tuned KoT5 모델)
- **용도**: 
  - 음식 영양정보 제공
  - 연령대별 필수/권장 영양정보 제공
  - 식단 조언 템플릿 생성
- **추론 방식**: `transformers.pipeline("text2text-generation")`
- **최적화**: 양자화 적용`("quantize_dynamic")`
- **초기화 위치**: `init_model()` → 전역 인스턴스 `diet_model`
- **호출 방식**: `get_t5_langchain_llm()`으로 LangChain LLM Wrapping
- **특징**: 영양 키워드 기반의 짧고 정확한 응답 제공


### RAG 문서 기반 응답 (LangChain + FAISS)

- **벡터 DB**: FAISS (`HuggingFaceEmbeddings`)
- **문서 출처**: 질병관리청 건강 정보 PDF
- **인덱스 구조**: MinIO에서 다운로드 or `build_index()`로 로컬 생성
- **조건**: `"자세히"` 키워드 포함 시만 작동
- **Chain 구성**:
  - Retriever: `db.as_retriever(k=4)`
  - LLM: `ChatOpenAI(gpt-4o)`
  - Prompt: 식단 전문가 역할 기반 프롬프트 구성


### 체중 예측 ML 모델

- **모델 호출 방식**: 외부 Flask API 서버로 `POST` 요청
- **엔드포인트**: 
  - 예측: `/predict-future-weight`
  - 학습: `/train-model/{user_id}`
- **입력값**: 키, 체중, 체지방률, 운동 칼로리 등
- **예측 실패 시 대체 로직**: TDEE 기반 fallback 계산
- **특징**: 모델 서버 주소 및 엔드포인트는 `.env`에서 설정됨


### 모델 분기 로직 요약

| 조건 | 사용 모델 |
|------|-----------|
| 챗봇 유형 = `workout` | GPT-4o (운동 피드백) |
| 챗봇 유형 = `nutrition` AND `"자세히"` 포함 | GPT-4o + RAG (문서 기반 응답) |
| 챗봇 유형 = `nutrition` AND `"자세히"` 미포함 | Fine-tuned T5 |
| 체중 예측 요청 | ML 서버 모델 (`/predict-future-weight`) |
| 예측 실패 | `predict_weight_change_from_base_date()` 함수 fallback |

---

## 🐇5. 비동기 피드백 및 예약 시스템

AI 챗봇은 사용자 요청에 따라 **실시간 피드백 생성**뿐 아니라, 특정 조건을 만족할 경우 **예약 피드백**을 비동기 방식으로 처리합니다. 이때 메시지 브로커로 **RabbitMQ**를 활용하여 백그라운드 작업을 처리하고, 추후 외부 알림 시스템으로 연동이 가능하도록 설계되어 있습니다.



### 메시지 브로커: RabbitMQ

- **라이브러리**: `aio_pika` (비동기 메시징 처리)
- **연결 정보**: `.env` 파일로부터 RabbitMQ 호스트 및 인증 정보 로딩
- **설정 항목 예시**:
  ```env
  RABBITMQ_HOST=rabbitmq
  RABBITMQ_PORT=5672
  RABBITMQ_USERNAME=admin
  RABBITMQ_PASSWORD=********
  RABBITMQ_EXCHANGE=alert_exchange
  RABBITMQ_QUEUE=notification-queue
  RABBITMQ_ROUTING_KEY=alert.push.notification


---

## 🔐6. 환경설정 및 설정파일 (.env)

서비스 운영 및 개발 환경 분리를 위해, 주요 민감 정보와 구성 요소는 `.env` 파일을 통해 관리합니다. 이 설정 파일은 시스템의 보안성과 이식성을 동시에 확보하기 위한 기본 구조입니다.


### 주요 설정 항목

| 항목 분류 | 설명 |
|-----------|------|
| DB 설정 | MySQL 기반 사용자/식단/세션 정보 저장 |
| GPT API | OpenAI GPT API 키 (비용 청구 연동) |
| MongoDB | LangChain Memory 및 대화 로그 저장용 |
| RAG 관련 | 식단 문서 PDF 및 FAISS 인덱스 위치 |
| T5 모델 | 사전 학습된 모델 로딩 및 추론 |
| 체중 예측 | 외부 모델 서버 URL 및 경로 |
| Eureka | MSA 환경에서 서비스 등록/탐색을 위한 설정 |
| RabbitMQ | 피드백 메시지 발행용 메시지 브로커 정보 |


### `.env` 파일
```env
# 데이터베이스 연결 정보(MySQL)
DB_USER=root
DB_PASSWORD=********
DB_HOST=mysql-external
DB_PORT=3306
DB_NAME=health_db

# OpenAI API Key
OPENAI_API_KEY=sk-******************************

# MongoDB 연결 정보
MONGO_URI=mongodb://mongodb:27017
MONGO_DB_NAME=chat_log
MONGO_DB_CHAT_COLLECTION_NAME=workout
MONGO_DB_CHAT_HISTORY_COLLECTION_NAME=history
MONGO_DB_FEEDBACK_COLLECTION_NAME=feedback

# Eureka 설정
EUREKA_SERVER=http://eureka-service:8761/eureka
EUREKA_APP_NAME=ai-service
EUREKA_INSTANCE_PORT=8000
EUREKA_INSTANCE_HOST=ai-service
EUREKA_HEALTH_CHECK_URL=http://ai-service:8000/health
EUREKA_RENEWAL_INTERVAL_IN_SECS=10
EUREKA_DURATION_IN_SECS=30

# RAG 파일
PDF_URL=http://13.124.189.230:31100/health-images/meal/nutrition_rag_data.pdf
INDEX_PKL=http://13.124.189.230:31100/health-images/ai/index.pkl
INDEX_FAISS=http://13.124.189.230:31100/health-images/ai/index.faiss

# 모델 서버 기본 URL
MODEL_SERVICE_URL=http://172.26.10.79:8100

# 모델 예측용 엔드포인트
PREDICT_MODEL_PATH=/predict-future-weight

# 모델 학습용 엔드포인트
TRAIN_MODEL_PATH=/train-model

# RabbitMQ 설정
RABBITMQ_HOST=rabbitmq
RABBITMQ_PORT=5672
RABBITMQ_USERNAME=admin
RABBITMQ_PASSWORD=********
RABBITMQ_EXCHANGE=alert_exchange
RABBITMQ_QUEUE=notification-queue
RABBITMQ_ROUTING_KEY=alert.push.notification
```

---

## 🔧 9. 향후 개선 방향

본 챗봇 서비스는 현재 SLM(Small Language Model) 기반의 T5 모델을 사용해 기본적인 식단 템플릿 생성 및 영양 정보 응답 기능을 제공합니다. 그러나 다음과 같은 한계가 존재하며, 향후 서비스 고도화를 위해 다음과 같은 개선 방향을 제안합니다.


### 1. 모델 용량 확장

- 현재 사용 중인 `pko-t5-large`는 경량화된 SLM으로, 응답 속도는 빠르지만 다음과 같은 제한이 있습니다:
  - 복잡한 문맥 이해의 어려움  
  - 사용자 상태를 반영한 세밀한 조언 부족  
  - 숫자 기반 피드백(예: 하루 권장 섭취량 대비 초과/부족)의 부정확성
- 이를 보완하기 위해 **중·대규모 파라미터의 LLM(Large Language Model) 도입**이 필요합니다.


### 2. 산술/추론 기능 향상

- 현 모델은 **수치 계산 또는 다단계 추론**
  (예: BMI → 칼로리 계산 → 식단 권장) 과정에서 오류 빈도가 높은 편입니다.
- 향후 개선 방향으로는 다음과 같은 기능 강화가 기대됩니다:
  - 내장된 수식 처리 기반 칼로리 계산
  - 체성분, 목표, 활동량에 따른 **맞춤 식단 자동 구성**
  - 다단 추론을 기반으로 한 **상황 대응형 조언 생성**


### 3. 고성능 모델 도입 검토

- `GPT-4o`, `Claude 4`, `LLaMA 3` 등  
  고성능 언어모델의 도입을 통해 추론 정확도 및 대응 범위를 확장할 수 있습니다.

---