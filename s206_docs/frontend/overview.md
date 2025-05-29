# DiaViseo Frontend 개요

**디아비서(DiaViseo) 프로젝트의 안드로이드 프론트엔드로, 사용자 인터페이스부터 모든 클라이언트 로직까지 담당합니다.**

## 📌 서비스 개요

### 역할과 책임

- Android 플랫폼 기반 사용자 인터페이스 구현
- Google OAuth 로그인 및 토큰 연동
- 건강 데이터 등록 UI 및 기능 구현 (식단, 운동, 체성분)
- Health Connect 연동 데이터 수집 및 서버 전송
- Firebase 푸시 알림 수신 및 처리

## ⚙️ 기술 스택 & 의존성

| 구분            | 기술                     | 버전           | 용도                 |
| --------------- | ------------------------ | -------------- | -------------------- |
| **Language**    | Kotlin                   | 2.0.21         | 기본 언어            |
| **UI**          | Jetpack Compose          | 2025.03.01     | UI 프레임워크        |
| **Networking**  | Retrofit2 + OkHttp       | 2.9.0 / 4.12.0 | API 통신             |
| **로컬 저장**   | DataStore                | 1.1.0          | 토큰 저장, 설정 저장 |
| **비동기 처리** | Coroutine                | 1.8.0          | 비동기 로직          |
| **푸시 알림**   | Firebase Cloud Messaging | 33.13.0        | 푸시 수신            |
| **헬스 연동**   | Health Connect           | 1.1.0          | 건강 데이터 연동     |

## 📂 프로젝트 구조

```jsx
mobile/
├── src/
    └── main/
        ├── java/com/example/diaviseo/
        │   ├── ui/                  // Jetpack Compose 기반 UI 화면
        │   ├── viewmodel/           // ViewModel 상태 관리
        │   ├── network/             // Retrofit API 모듈
        │   ├── model/               // 내부 데이터 클래스 (DTO, 상태 모델)
        │   ├── datastore/           // DataStore 유틸 (Token, FCM 등)
        │   ├── healthconnect/       // Health Connect 관련 연동 모듈
        │   ├── mapper/              // DTO ↔ 모델 매핑 로직
        │   └── MyFirebaseMessagingService.kt 등
        ├── res/                     // 이미지, 텍스트 리소스
        └── AndroidManifest.xml
```

## 📁 주요 패키지 설명

| 디렉토리                              | 설명                                                  |
| ------------------------------------- | ----------------------------------------------------- |
| `ui/`                                 | Jetpack Compose 기반 UI 화면. 화면별 서브폴더 구성    |
| `viewmodel/`                          | 화면과 대응되는 ViewModel 클래스. 상태 관리, API 호출 |
| `network/`                            | Retrofit2 기반 API 모듈. 도메인별 분리                |
| `model/`                              | UI 상태 및 서버 응답 DTO 정의                         |
| `datastore/`                          | 로컬 저장소 (Token, FCM, 건강 연동 등)                |
| `healthconnect/`                      | Health Connect 연동 권한, 데이터 처리, 동기화 설정    |
| `mapper/`                             | DTO ↔ 내부 모델 변환 로직                             |
| `components/`                         | 공통 UI 컴포넌트 (버튼, 다이얼로그 등)                |
| `MyFirebaseMessagingService.kt`       | FCM 수신 처리 클래스                                  |
| `MainActivity.kt`, `MyApplication.kt` | 앱의 진입점 및 전역 설정                              |

## 🧰 UI 구성 (`ui/` 내부)

| 하위 폴더     | 설명                                   |
| ------------- | -------------------------------------- |
| `main/`       | 홈, 마이페이지 등 메인 탭              |
| `register/`   | 식단/운동/체성분 등록 화면             |
| `onboarding/` | 온보딩 프로세스 UI                     |
| `detail/`     | 상세 화면 (예: 식단 상세 화면)         |
| `components/` | 공통 요소 (버튼, DatePicker 등 재사용) |

## 📦 API 구성 (`network/` 내부)

도메인별 모듈 구성:

```
network/
├── auth/
├── meal/
├── exercise/
├── chatbot/
├── food/
├── foodset/
├── condition/
├── alarm/
└── common/
```

- 각 도메인 폴더에는 `XXApiService.kt`, `dto/` 클래스 포함
- 공통 응답은 `common/dto/ApiResponse.kt` 사용

## 📱 화면 구성 & 주요 흐름

| 화면 구분   | 주요 내용                                          |
| ----------- | -------------------------------------------------- |
| 온보딩      | 생년월일, 키/몸무게, 목표 등 입력                  |
| 홈          | 건강 데이터 요약, 사용자 피드백 정보 표시          |
| 식단 등록   | 음식 검색, 세트 선택, 즐겨찾기 관리 포함           |
| 운동 등록   | 최근 운동, 즐겨찾기, 수동 검색 등록 지원           |
| 체성분 등록 | 사진 등록(OCR 기반), 직접 수치 입력                |
| 마이페이지  | 프로필 정보, FCM 설정, Health Connect 연동 기능 등 |

## 🔐 인증 & 토큰 처리

- Google ID Token을 서버에 전달 → JWT(access/refresh) 발급
- `accessToken`: API 호출 시 HTTP 헤더에 포함
- `refreshToken`: DataStore에 저장, 만료 시 자동 재발급 처리
- 401 Unauthorized 발생 시 토큰 갱신 및 API 재요청 처리

## 📄 API 연동 구조

- Retrofit2 기반 구성
- 공통 Interceptor 설정 (Authorization 포함)
- 응답 구조 일괄 처리 (ApiResponse 형태)

---

## ⚖️ 상태 관리 전략

- 화면별 `ViewModel` + `StateFlow` 사용
- UI 상태는 `mutableStateOf`, `MutableStateFlow`, `remember`, `derivedStateOf`로 관리
- 상태-이벤트 분리: 사용자 입력 → ViewModel 처리 → UI 반영

---

## 🏃 Health Connect 연동

- 연동 대상: `StepsRecord`, `ExerciseSessionRecord`
- 연동 방식:

  - 수동: 마이페이지에서 연동 버튼 클릭
  - 자동: WorkManager를 통한 매일 23시 정기 동기화

- 데이터 기준: 최근 30일치 수집 후 서버 전송
- 권한 요청 및 설치 여부 확인 로직 포함
- 세부 구조는 [healthconnect.md](./healthconnect/healthconnect.md) 참고

## 🔔 FCM 푸시 알림

- 앱 첫 실행 시 Firebase 토큰 발급 및 로컬 저장
- 로그인 후 서버에 전송
- `MyFirebaseMessagingService`를 통한 수신 처리 (포그라운드/백그라운드/앱 실행 중 화면)
- 외부 서버에서 메시지 발송 가능

## 🎨 공통 UI 컴포넌트

- 공통 버튼, 탭, 입력 필드, 다이얼로그 등의 요소를 재사용 가능한 컴포넌트로 구성
- `MainButton`, `BottomButtonSection`, `PermissionRequestButton`, `StepProgressBar` 등을 통해 온보딩 및 등록 UI 흐름을 일관성 있게 유지
- `DiaViseoColors`, `TextStyle`을 통해 브랜드 일관성과 시각적 통일감 확보
- 앱 전체에 걸쳐 다양한 상태 UI (예: EmptyStateView) 및 피드백 UI를 통합적으로 제공

## 📝 요약

이 프로젝트는 다음과 같은 설계 원칙을 중심으로 구성되었습니다:

- **도메인 기반 폴더 구조**: 기능 단위로 명확히 나뉜 구조 (auth, meal, exercise 등)
- **ViewModel 중심 아키텍처**: 모든 UI 상태는 `StateFlow`와 Compose 상태로 관리
- **API/데이터 계층 분리**: Retrofit + Mapper 구조를 통해 DTO와 UI 모델 간 책임 분리
- **Health Connect, FCM 등 외부 시스템은 별도 디렉토리로 관리**
- **유지보수를 고려한 재사용성과 모듈성** 중심으로 UI 컴포넌트 구성

## 📋 참고 문서 및 외부 연동

- [📄 Health Connect 공식 문서](https://developer.android.com/guide/health-and-fitness/health-connect)
- [📄 Firebase FCM 설정 가이드](https://firebase.google.com/docs/cloud-messaging/android/client?hl=ko)
