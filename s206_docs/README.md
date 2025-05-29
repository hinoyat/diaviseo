# 디아비서 (DiaViseo) 개발자 문서

**📌 개인 건강 데이터를 통합 관리하고 AI 기반 분석 기능을 제공하는 건강관리 플랫폼**

```
📦 diaviseo/
├── README.md                # 프로젝트 메인 안내
│
├── backend/                 # MSA 백엔드 서비스
│   ├── auth-service/
│   ├── user-service/
│   ├── health-service/
│   ├── alert-service/
│   ├── gateway-service/
│   ├── config-service/
│   ├── eureka-service/
│   ├── common-module/
│   └── overview.md          # 백엔드 전체 아키텍처 문서
│
├── frontend/                # Android 모바일 앱
│   ├── src/
│   ├── overview.md          # 프론트엔드 구조 및 기능 설명
│   └── README.md
│
├── ai/                      # AI/ML 서비스
│   ├── api-spec.md
│   └── overview.md          # OCR, 챗봇 등 AI 기능 개요
│
└── docs/                    # 설계 및 배포 문서
    ├── architecture/
    ├── api-specs/
    └── deployment/
```

* **개발 기간:** 2025.04.11 ~ 2025.05.23
* **팀 구성:** 백엔드 3명, 안드로이드 2명, AI 1명
* **기술 스택:** Spring Boot, Kotlin, MySQL, Redis, Elasticsearch, MinIO, RabbitMQ, Docker

---

## 📌 프로젝트 개요

**디아비서(DiaViseo)**는 개인 맞춤형 건강관리 플랫폼입니다.
사용자의 체성분, 식단, 운동 데이터를 통합 관리하며, **AI 기반 개인화 분석**과 **스마트 알림**을 제공합니다.

### 🎯 핵심 기능
* **Google OAuth 소셜 로그인** 및 JWT 기반 인증
* **OCR 기반 체성분(InBody) 데이터 자동 인식**
* **Health Connect 연동** 자동 걸음수/운동 데이터 수집
* **Elasticsearch 기반 음식 검색** 및 식단 기록
* **개인화된 건강 목표 설정** 및 달성률 추적
* **AI 챗봇** 건강 상담 및 맞춤 피드백
* **FCM 푸시 알림** 기록 리마인더 및 동기부여

### 🏗️ 기술적 특징
* **마이크로서비스 아키텍처(MSA)** - 서비스별 독립 배포 및 확장
* **비동기 메시징(RabbitMQ)** - 서비스 간 느슨한 결합
* **분산 데이터 저장** - MySQL, Redis, Elasticsearch, MinIO

---

## 🧩 전체 시스템 구조
![alt text](image.png)
```
┌─────────────────────────────────────────────────────────┐
│                    DiaViseo System                     │
├─────────────────────────────────────────────────────────┤
│                                                         │
│  Android App ──────► Gateway Service (8080)            │
│                            │                           │
│  ┌─────────────────────────┼─────────────────────────┐  │
│  │     Infrastructure     │      Business Services │  │
│  │                        │                         │  │
│  │  Config Service (8888) │  ┌─── Auth Service      │  │
│  │  Eureka Service (8761) │  ├─── User Service      │  │
│  │  Gateway Service       │  ├─── Health Service    │  │
│  │                        │  └─── Alert Service    │  │
│  └────────────────────────┼─────────────────────────┘  │
│                           │                            │
│  ┌─────────────────────────┼─────────────────────────┐  │
│  │       Data Layer       │      External APIs      │  │
│  │                        │                         │  │
│  │  MySQL (사용자/건강)     │  Google OAuth API       │  │
│  │  Redis (세션/캐시)       │  CoolSMS API           │  │
│  │  Elasticsearch (검색)   │  Firebase FCM          │  │
│  │  MinIO (이미지 저장)     │  OCR API               │  │
│  │  RabbitMQ (메시징)      │                         │  │
│  └─────────────────────────┴─────────────────────────┘  │
│                                                         │
└─────────────────────────────────────────────────────────┘
```

## 🗂️ 주요 문서

| 카테고리 | 문서 | 설명 |
|---------|-----|------|
| **🏗️ 아키텍처** | [Backend Overview](backend/overview.md) | MSA 구조, 서비스 간 통신, 데이터 흐름 |
| **📱 클라이언트** | [Frontend Overview](frontend/overview.md) | Android 앱 구조, UI/UX, API 연동 |
| **🤖 AI/ML** | [AI Overview](ai/overview.md) | OCR, 챗봇, 이미지 분석 기능 |
| **📊 데이터베이스** | [User Service ERD](https://dbdiagram.io/d/s206_user_db-6837cbfac07db17e77a433fa) | 사용자 관리 서비스 스키마 |
| | [Health Service ERD](https://dbdiagram.io/d/s206_health_db-6837cc61c07db17e77a4428f) | 건강 관리 서비스 스키마 |
| | [Notification Service ERD](https://dbdiagram.io/d/s206_notification_db-6837cc04c07db17e77a434f0) | 알림 관리 서비스 스키마 |
| **🎨 디자인** | [Figma 링크](https://www.figma.com/design/CTl8jETdRPxjrMVzBbNM8D/SSAFY12_S206_%EB%94%94%EC%95%84%EB%B9%84%EC%A0%84?node-id=0-1&t=prsBdOKyFSaJ5Vzn-1) | UI/UX 디자인 시스템 |


## ⚙️ 빠른 시작

### 🔧 개발 환경 요구사항
```bash
# Backend
- Java 17+
- Spring Boot 3.4.5

# Frontend  
- Android Studio
- Kotlin
- Jetpack Compose

# External Services
- MySQL 8.0+
- Redis 7.0+
- Elasticsearch 8.0+
- MinIO
- RabbitMQ
```

### 🚀 로컬 실행
```bash

📍 상세한 환경 설정 및 실행 방법은 소스코드의 /exec 폴더의 포팅 메뉴얼 참조
```

---

## 👥 팀 구성

| 역할 | 이름 | 주요 담당 | 연락처 |
|-----|------|----------|--------|
| **Backend** | 강현호 | • Eureka Service (서비스 디스커버리)<br/>• Gateway Service (API 라우팅, 인증)<br/>• Auth Service (OAuth, JWT 토큰 관리)<br/>• User Service (회원 관리, SMS 인증)<br/>• Health Service (영양/알레르기/질환 관리)<br/>• Android 챗봇 기능, 마이페이지 개발 | hhwj2270@gmail.com |
| **Backend/AI** | 김성현 | • Health Service (체성분 관리)<br/>• 알림 서비스 설계 및 구현 (RabbitMQ + FCM 기반)<br/>• 스케줄러 기반 리마인더 알림 전송 기능 구현<br/>• 운동 피드백 챗봇 연동 및 알림 자동화 처리<br/>• FastAPI + LangChain 기반 챗봇 서버 개발<br/>• ChatGPT 기반 운동 피드백 프롬프트 작성 및 구조 설계<br/>• AES256을 활용한 암호화 공통 코드 작성 | aruesin2@gmail.com |
| **Backend/INFRA** | 김홍범 | • CI/CD 배포 자동화 파이프라인 설계<br/>• ElasticSearch 음식 검색 기능 도입<br/>• MinIO를 활용한 객체 스토리지 사용<br/>• Grafana, Prometheus를 통한 모니터링 환경 구축<br/>• SonarQube를 활용한 코드 정적 분석<br/>• Health Service (운동 기록 및 통계, OCR을 활용한 이미지 인식) | fkdldj48@naver.com |
| **Frontend** | 이다은 | • 온보딩 및 회원가입 UI 설계 및 구현<br/>• 식단 등록·검색·즐겨찾기 UI 및 기능 개발<br/>• 체성분 일반 등록 및 OCR 기반 등록 UI/기능 개발<br/>• 운동 데이터 등록 UI 및 입력 흐름 구현<br/>• Health Connect 기반 운동·걸음수 데이터 연동 및 동기화 처리<br/>• Firebase FCM 관련 프론트엔드 토큰 처리 및 알림 권한 대응 구현<br/>• 공통 UI 컴포넌트 제작 (탭바, 검색바, 버튼 등 재사용 구조 설계) | danivalen@naver.com |
| **Frontend** | 임채현 | • Android 프로젝트 구조 설정 및 환경 세팅<br/>• 디아비서 UI/UX 설계<br/>• Jetpack Compose 기반 UI 구현 (Kotlin 단일 언어 사용)<br/>• Retrofit2 + OkHttp 초기 세팅 및 문서화<br/>• ViewModel & DataStore 상태 관리 (토큰, 걸음 수, 화면에 필요한 데이터 등)<br/>• Coroutine 비동기 처리 (통계/정보 동시 호출)<br/>• 회원가입, 홈, 평가, 체성분/식단/운동 상세, 공통 달력 화면 개발 | chi_927@naver.com |
| **AI** | 김서린 | • SLM기반 식단 챗봇 시스템 설계 및 구현<br/>• KoT5 모델 Fine-tuning 및 Hugging Face 배포<br/>• GPT API를 통한 RAG 기반 응답 시스템 구축<br/>• FastAPI기반 AI응답 API 설계 및 응답 구조 정의<br/>• 사용자 데이터 기반 식단 피드백 로직 구성<br/>• Langchain 기반 SLM wrapping 및 흐름 설계 | seorin66@naver.com |


