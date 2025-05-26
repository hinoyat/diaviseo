# 📱 DiaViseo - 온보딩 & 회원가입 문서

디아비서(DiaViseo) 앱은 \*\*Google OAuth를 통해 인증된 사용자의 필수 정보를 수집한 후, 내부 회원가입 및 초기 설정(온보딩)\*\*을 완료하는 구조로 설계되어 있습니다. 이 문서는 온보딩 및 회원가입 흐름과 관련된 화면 구성, ViewModel, API 연동 구조를 설명합니다.

## ✅ 회원가입 흐름 개요

1. **Google 로그인 (ID Token 발급)**
2. **서버에 ID Token 전달 → 기존 회원 여부 확인**

   - 신규 회원: `isNewUser: true` → 회원가입 화면으로 이동
   - 기존 회원: 바로 MainScreen으로 이동

3. **신규 회원일 경우**

   - 이름, 생년월일, 키/몸무게, 성별, 목표 정보 입력 (온보딩 단계)
   - 마지막 단계에서 서버에 회원가입 API 호출 → 토큰 발급 완료
   - 홈으로 이동

## 🔄 API 연동 요약

### 1. Google 로그인

- **API**: `POST /auth/oauth/login`
- **ViewModel**: `AuthViewModel.loginWithGoogle()`
- **응답**:

  - 기존 회원: Access/Refresh Token 포함
  - 신규 회원: `accessToken = null`, `isNewUser = true`

### 2. 휴대폰 인증 (선택적 도입 가능)

- **API**:

  - `POST /users/verify/phone` (문자 발송)
  - `POST /users/verify/phone/confirm` (코드 확인)

- **ViewModel**: `AuthViewModel`

### 3. 회원가입

- **API**: `POST /users/signup`
- **ViewModel**: `AuthViewModel.signUpWithDia()`
- **요청 파라미터**: 이름, 생년월일, 성별, 키, 몸무게, 목표 등
- **응답**: 신규 토큰 발급 (access + refresh)

## 🧭 온보딩 단계 구성

| 화면               | 기능                                        | 주요 ViewModel                           |
| ------------------ | ------------------------------------------- | ---------------------------------------- |
| `NameInputScreen`  | 이름 입력                                   | `AuthViewModel.name`                     |
| `BmiInputScreen`   | 키/몸무게 입력                              | `AuthViewModel.height`, `weight`         |
| `GoalSelectScreen` | 목표 선택 (감량/유지/증량)                  | `AuthViewModel.goal`                     |
| `FinalGuideScreen` | 목표 기준 섭취량 안내 + Health Connect 연동 | `GoalViewModel`, `ExerciseSyncViewModel` |

## 🔐 ViewModel 활용

### `AuthViewModel`

- 회원가입 전 전체 정보를 저장하는 중앙 ViewModel
- Navigation Graph 간 공유됨 (`signupNavGraph` 내부)
- 내부 StateFlow 예:

  - `name`, `birthday`, `gender`, `height`, `weight`, `goal`

### `GoalViewModel`

- FinalGuideScreen에서 표준 체중, 권장 섭취량 등 계산 보조

## 🎨 컴포넌트 예시

- `StepProgressBar`: 온보딩 단계 표시
- `MainButton`: 하단 CTA 버튼
- `PermissionRequestButton`: FCM 권한 요청 버튼
- `BottomButtonSection`: 하단 고정 버튼 영역

## 📦 관련 클래스

- **`SignupNavGraph.kt`**: `NavHost`로 온보딩 흐름 정의
- **`GoogleLoginManager.kt`**: Google 인증 처리 전담
- **`AuthApiService.kt`**: 로그인 및 토큰 재발급 API
- **`UserApiService.kt`**: 회원가입/유저 프로필 관련 API

## ⚠️ 주의사항

- 가입 완료 시점은 `AuthViewModel.signUpWithDia()`가 성공했을 때입니다.
- 이 시점에 서버는 토큰을 발급하며 이후 정상 유저로 취급합니다.
- 토큰 발급이 되지 않은 상태에서는 다른 API 접근이 제한됩니다.

## 🗂️ 기타 정보

- `ProfileViewModel`은 가입 이후 프로필 정보 조회 및 수정에 사용됩니다.
- `DataStore`를 통해 토큰을 저장 및 자동 로그인 처리합니다.

## 📌 요약

| 단계                | 내용                                       |
| ------------------- | ------------------------------------------ |
| Google 로그인       | ID Token → 서버 전달                       |
| 기존 회원 여부 확인 | `isNewUser`로 판단                         |
| 신규 온보딩 진행    | 이름, 생년월일, 키/몸무게, 성별, 목표      |
| 회원가입 완료       | 서버에 POST /users/signup 호출 + 토큰 발급 |
| 홈 이동             | MainScreen 진입                            |
