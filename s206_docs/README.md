# 디아비서 (DiaViseo) 개발자 문서

**📌 개인 건강 데이터를 통합 관리하고 AI 기반 분석 기능을 제공하는 건강관리 플랫폼**

```
📦 diaviseo-docs/
├── README.md                # 문서 메인 안내 (기업 제출용 요약 + 목차)
│
├── common/                  # 공통 기술/모듈 정리
│   ├── overview.md          # 공통 처리 구조, 예외 처리, ResponseDto 등
│   ├── error-handling.md
│   └── utils.md
│
├── backend/
│   ├── auth-service/
│   │   ├── overview.md      # 역할, 흐름도, 기술요소 설명
│   │   ├── api-spec.md      # 주요 API 명세
│   │   ├── troubleshooting.md      # 트러블 or 오류 or 개선필요 사항 정리
│   │   └── images/
│   ├── user-service/
│   ├── health-service/
│   └── gateway-service/
│
├── ai/
│   ├── overview.md          # OCR, 챗봇 등 AI 기능 개요
│   ├── api-spec.md
│   ├── deployment.md        # on-device 이식 관련 정보
│   ├── model.md             # 모델 정보 등 (필요할 경우)
│   └── images/
│
├── frontend/
│   ├── overview.md          # 화면 흐름 / 구조 설명
│   ├── api-usage.md         # 어떤 API를 쓰는지 / props 등
│   └── images/
│
├── deployment/
│   ├── ci-cd.md             # GitHub Actions, Docker, 배포 흐름
│   ├── env-config.md        # 환경 변수 정리
│   └── run-local.md         # 로컬 실행 및 테스트 방법
│
└── images/                  # 공통 이미지 (ex. architecture.png)
```


* **프로젝트 기간:** 2025.03 \~ 2025.05
* **팀 구성:** 백엔드 3명, 프론트엔드 2명, AI 1명
* **기술 스택:** Spring Boot, MySQL, Redis, Elasticsearch, Vue.js, Kotlin Compose, Docker, Kubernetes, GitHub Actions 등

---

## 📌 프로젝트 개요 요약

\*\*디아비서(DiaViseo)\*\*는 개인 건강관리 도우미 플랫폼입니다.
사용자의 건강 데이터를 통합 관리하며, 식단 인식, 운동 기록, 목표 달성 등을 **AI와 데이터 시각화**로 지원합니다.

* 소셜 로그인 기반 회원 인증 및 관리
* OCR 기반 Inbody 데이터 인식
* 건강 목표 설정 및 일간/주간/월간 통계 시각화
* 체성분 / 식단 / 운동 사용자 개인 데이터 기반 AI 피드백
* Redis 기반 토큰 인증 및 세션 유지
* Redis & CoolSMS 휴대폰 인증
* Elasticsearch를 활용한 식단 검색
* Minio를 활용한 이미지 관리

---

## 🧩 전체 시스템 구조

![architecture-diagram](./docs/images/architecture.png)

> MSA 구조로 서비스를 분리하여 유연성과 유지보수성을 강화했습니다.
> Gateway → Auth/User/Health/AI로 요청을 라우팅하며, Redis, Elasticsearch, RabbitMQ를 통해 확장성과 반응성을 확보했습니다.

---

## 🗂️ 문서 목차

| 문서                                   | 설명                                          |
| ------------------------------------ | ------------------------------------------- |
| [📌 시스템 아키텍처](docs/architecture.md)  | 전체 기술 구조 및 서비스 간 연동 설명                      |
| [🛠️ 서비스별 문서](docs/services/)        | Auth, User, Diary, Gateway 기능별 구현 방식과 흐름 정리 |
| [🧠 AI 기능 문서](docs/ai/)              | OCR, AI 챗봇 기능 설명 및 API 구조 정리                |
| [🚀 배포 문서](docs/deployment/)         | GitHub Actions 기반 CI/CD, 환경변수 설정, 로컬 실행 안내  |
| [📦 공통 모듈 설명](docs/common-module.md) | ResponseDto, 예외 처리, 공통 유틸 구성 정리             |

---

## ⚙️ 실행 및 테스트 방법

```bash
# 1. 프로젝트 clone
git clone https://github.com/your-org/diaviseo.git

# 2. 각 서비스별 env 설정
cd auth-service
cp .env.example .env

# 3. Docker로 전체 실행
docker-compose up --build
```

> 📍 상세한 실행 및 테스트 방법은 [ci-cd.md](docs/deployment/ci-cd.md) 문서 참조

---

## 🙋‍♀️ 담당자 정보

| 파트  | 이름  | 주요 담당                  | 이메일                                                     |
| --- | --- | ---------------------- | ------------------------------------------------------- |
| 백엔드 | 강현호 | 담당 업무 | [safy.kim@example.com](mailto:safy.kim@example.com)     |
| 백엔드 | 김성현 | 담당 업무 | [safy.park@example.com](mailto:safy.park@example.com)   | 
| 백엔드 | 김홍범 | 담당 업무 | [safy.park@example.com](mailto:safy.park@example.com)   | 
| 프론트 | 이다은 | 담당 업무 | [safy.lee@example.com](mailto:safy.lee@example.com)     |
| 프론트 | 임채현 | 담당 업무 | [safy.lee@example.com](mailto:safy.lee@example.com)     |
|  AI  | 김서린린 | 담당 업무 | [safy.jeong@example.com](mailto:safy.jeong@example.com) |

---

## 📎 참고 사항

* 이 문서는 SSAFY 기업연계 프로젝트 산출물로, 기술 검토 및 유지보수를 위한 개발자 문서입니다.
* 각 문서 링크를 따라가면 세부적인 구현 설명과 흐름도를 확인할 수 있습니다.
* 문의 사항은 담당자에게 이메일로 연락 부탁드립니다.
