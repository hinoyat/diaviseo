# 챗봇 서비스 개요

**헬스 데이터 기반 GPT 챗봇 서비스**로, 사용자의 체성분·운동·식단 정보를 바탕으로 GPT API를 통해 분석과 피드백을 제공합니다.

---

## 📌 서비스 개요

### 역할과 책임

* 사용자 입력을 가공해 GPT로 전달
* LangChain 구성 – memory, tool, chain, agent 관리
* **T5 모델 및 코사인 유사도** 사용, 전체 유저 검색에 포함
* FastAPI REST API 구성
* RAG 기반 문서 검색, 체중 예측 ML 호출 지원

### MSA 구조에서의 위치

```
Android App → Gateway → FastAPI → LangChain Chain Dispatcher
                                  ↓
                       ConversationalRetrievalChain
                                  ↓
                               GPT API 응답

```

챗봇 서비스는 사용자의 모든 입력을 GPT + ML + 데이터 기반 피드백으로 변환합니다.

---

## ⚙️ 기술 스택 & 의존성

| 구분            | 기술             | 버전     | 용도             |
| ------------- | -------------- | ------ | -------------- |
| **Backend**   | FastAPI        | 0.110+ | API 서버         |
| **AI & LLM**  | LangChain      | 최신     | Chain/Agent 관리 |
|               | OpenAI GPT API | gpt-4  | LLM 응답         |
| **ML 모델**     | T5Wrapper      | -      | 템플릿 생성 / 질문 요약 |
|               | 체중 예측 모델       | -      | 지도 학습 예측       |
| **Vector DB** | FAISS          | -      | 문서 검색(RAG)     |
| **DB**        | MySQL, MongoDB | -      | 유저·대화·기록 저장    |
| **Infra**     | Docker         | -      | 컨테이너 배포        |

### 주요 의존성

* `app/core/rag` : LangChain용 문서 **인덱스**
* `app/services/chat` : 챗봇 응답 생성 서비스
* `app/ml_models` : T5, 체중 예측 등 ML 래퍼
* `app/routes` : FastAPI 라우터

---

## 🔧 핵심 기능

### 1. GPT 대화 흐름

* LangChain memory로 유저별 대화 컨텍스트 유지
* ConversationalRetrievalChain + tool 사용
* system prompt

### 2. 체중 예측 및 피드백

* 유저의 체성분 정보를 기반으로 체중 변화 예측
* 예측값을 토대로 GPT가 맞춤 피드백 생성

### 3. 문서 기반 질문 응답(RAG)

* FAISS 기반 RAG 구조로 문서 검색 후 **보강(augmentation)**
* LangChain Retriever 구성 사용

### 4. 운동/영양 분석 기능

* 유저 입력 기반 칼로리·근육 증가 등 분석 수행
* 분석 결과에 따라 LLM이 피드백 생성

### 5. 예약 기반 피드백 전송

* 매일/매주 정해진 시점에 자동 피드백 전송
* `app/scheduler` 내 cron 기반 구성

---

## 🏠 시스템 아키텍처

```
사용자
 ↓
[ FastAPI Router (/api/chat) ]
 ↓
[ LangChain Chain Dispatcher ]
 ↓
[ Chain 종류 ]
 ├─ ConversationalRetrievalChain (Memory + RAG)
 ├─ ToolAgent (체성분 예측, 영양 계산 등 도구 포함)
 └─ T5Wrapper (질문 요약 또는 템플릿 생성 등)

 ↓
[ GPT API (gpt-4) 호출 ]
 ↓
[ 응답 후 유저 전달 ]
```

보조 구성 요소

* 🧠 Memory 저장 : LangChain + Mongo 연동
* 🧪 체중 예측 : 내부 ML 모델 호출(`app/ml_models`)
* 🗞 문서 검색 : FAISS 벡터 DB 사용(`core/rag`)
* 🔁 스케줄링 : 운동/피드백 예약 전송(`scheduler`)

---

## 📝 환경설정

### `.env` 예시(민감 정보 마스킹)

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
