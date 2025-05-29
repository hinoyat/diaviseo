# 🚀 MainActivity 구조 및 역할 문서

**이 문서는 디아비서(DiaViseo)의 MainActivity에 대한 구조, 권한 처리, FCM 등록, WorkManager 예약 등에 대해 설명합니다.**

## 📌 주요 역할

| 기능                                | 설명                                              |
| ----------------------------------- | ------------------------------------------------- |
| 앱 최초 진입 진입점                 | Splash → 회원가입(신규) / MainScreen(기가입) 분기 |
| 상태 바 스타일 설정                 | Accompanist SystemUiController 이용               |
| Navigation 그래프 연결              | Splash → SignupNavGraph → Main 화면               |
| FCM 토큰 발급 및 저장               | FirebaseMessaging 이용                            |
| 걸음 수 센서 권한 요청 및 센서 시작 | `ACTIVITY_RECOGNITION` 권한 확인 및 요청          |
| 자정 리셋 워커 예약                 | `WorkManager`로 매일 자정 걸음 수 초기화          |

## 🧭 Navigation 구성

```kotlin
NavHost(navController, startDestination = "splash") {
    composable("splash") { SplashScreen(navController) }
    signupNavGraph(navController)
    composable("main") { MainScreen(navController) }
}
```

> `startDestination`은 "splash"로 시작하며, 로그인 여부에 따라 다른 그래프로 분기됨

## 📱 권한 요청 (`ACTIVITY_RECOGNITION`)

- 사용자의 걸음 수를 추적하기 위해 **ACTIVITY_RECOGNITION** 권한이 필요
- 최신 `ActivityResultContracts.RequestPermission()` API로 요청 처리

```kotlin
Manifest.permission.ACTIVITY_RECOGNITION
```

- granted 시 `StepViewModel.startListening()` 호출
- denied 시 거절 로그 출력

## 🔁 WorkManager 설정 (자정 리셋)

- 매일 자정에 걸음 수를 초기화하기 위한 주기성 작업 등록

```kotlin
PeriodicWorkRequestBuilder<StepResetWorker>(
    24, TimeUnit.HOURS
)
.setInitialDelay(initialDelay, TimeUnit.MINUTES)
```

- `enqueueUniquePeriodicWork()` 사용해 중복 예약 방지
- 테스트용 `OneTimeWorkRequest` 로직은 주석 처리됨

## 🔐 FCM 토큰 발급 및 저장

- 앱 실행 시 FCM 토큰을 자동으로 Firebase에서 발급
- 발급된 토큰은 `FcmTokenManager`를 통해 `DataStore`에 저장됨

```kotlin
FirebaseMessaging.getInstance().token.addOnSuccessListener { token -> ... }
```

> 오류 발생 시 `addOnFailureListener`에서 로그 출력

## 🧠 ViewModel 연동

- `StepViewModel`: 걸음 수 센서 리스닝, 정지 기능 제어
- `TestViewModel`: 현재 저장된 AccessToken 로그로 출력 (테스트용)

## 🎨 UI 테마 및 상태 바 설정

- `DiaViseoTheme` 내에서 System UI 컨트롤러로 상태바 투명 처리
- `TransparentStatusBar(window)` 호출로 완전 투명 상태 적용

## 🗂️ 기타

- `onPause()`: 센서 리스너 정지 (`stopListening()`)
- `onResume()`: 필요 시 권한 재확인 및 리스너 재등록 가능
