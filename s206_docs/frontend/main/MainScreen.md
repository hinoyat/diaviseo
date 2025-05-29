# 🧭 MainScreen 내비게이션 구조 설명서 (Android Compose 기준)

---

## 💡 전체 구조 요약

- **NavHostController를 통한 화면 전환 관리**
- **화면 분할 역할**

  - `main` → 하단 탭 네비게이션 + 각 상세 화면
  - `signupGraph` → 회원가입 및 로그인 루트 (조건부 분기)

- **하단 바와 Floating 버튼은 조건부 노출**
- **화면 전환 애니메이션까지 명시적으로 제어**

---

## 🔁 NavController 흐름

```kotlin
val navController = rememberNavController()
```

- `NavHost`를 기반으로 각 `composable()` 경로를 선언
- `NavBackStackEntry`를 통해 현재 화면에 따라 BottomBar 노출 여부 제어

---

## 🧱 주요 화면 분류

| 범주           | 라우트 명                                       | 설명                                   |
| -------------- | ----------------------------------------------- | -------------------------------------- |
| ✅ 메인 탭     | `home`, `goal`, `chat_history`, `my`            | 하단 탭으로 이동 가능한 메인 화면      |
| ✅ 상세 페이지 | `exercise_detail`, `meal_detail`, `home_detail` | 개별 탭에서 이동하는 상세 보기 화면    |
| ✅ 등록 페이지 | `body_register`, `exercise_register/{date}` 등  | 사용자의 행동(등록/수정)과 관련된 화면 |
| ✅ 설정 / 편집 | `edit_profile`, `edit_physical_info`, `faq` 등  | 마이페이지 관련 유틸리티 화면          |

---

## 📱 BottomNavigationBar 노출 제어

```kotlin
val hideBottomBarRoutes = listOf(...)
val isBottomBarVisible = currentRoute !in hideBottomBarRoutes
```

- 상세 페이지 및 등록 화면에서는 BottomBar를 숨김
- 메인 탭 4개만 BottomBar와 함께 보이도록 설계

---

## 🚀 내비게이션 애니메이션 정의

Compose의 `AnimatedNavHost` 대신, 커스텀 `enterTransition`, `exitTransition` 등을 직접 정의하여 **탭 간 이동**과 **상세 페이지 진입**을 구분:

| 상황             | 애니메이션 종류                    |
| ---------------- | ---------------------------------- |
| 탭 간 이동       | `slideInHorizontally` + `fadeIn`   |
| 상세 페이지 이동 | `fadeIn`, `fadeOut`                |
| 탭 복귀 (pop)    | `slideOutHorizontally` + `fadeOut` |

---

## 🧭 경로 정의 예시 (composable)

```kotlin
composable("exercise_detail") {
    ExerciseDetailScreen(navController = navController, viewModel = profileViewModel)
}

composable("exercise_register/{date}") { backStackEntry ->
    val date = backStackEntry.arguments?.getString("date") ?: LocalDate.now().toString()
    ExerciseRegisterMainScreen(date = date, navController = navController)
}
```

- 특정 화면에 파라미터가 필요한 경우 `{}` 사용
- `arguments?.getString(...)` 으로 safe하게 파라미터 추출

---

## 🔐 로그인 상태 감지 & 전환

```kotlin
LaunchedEffect(isLoggedIn) {
    if (isLoggedIn == false) {
        navControll.navigate("signupGraph") {
            popUpTo("main") { inclusive = true }
        }
    }
}
```

- **SplashViewModel**이 로그인 상태를 판단
- 로그인 상태가 `false`면, `signupGraph`로 루트 내비게이션 리디렉션
- `inclusive = true` → `main` 스택을 완전히 제거

---

## 💬 FabOverlayMenu 동작

```kotlin
if (isFabMenuOpen.value) {
    FabOverlayMenu(
        onDismiss = { isFabMenuOpen.value = false },
        navController = navController
    )
}
```

- 홈 화면 또는 특정 탭에서만 열리는 Floating Action Menu
- `navController`를 사용해 새로운 등록 화면 등으로 이동 유도

---

## 🧩 Compose Navigation의 장점 활용

- `NavHost`와 `composable`을 통한 **분리된 화면 관리**
- `ViewModelStoreOwner`를 통해 **화면 간 상태 공유 (parentEntry 사용)**
- `NavBackStackEntry` 기반의 **상태 기반 UI 분기**
- `rememberNavController()` 기반으로 **Composables 안에서 네비게이션 동기화**

---

## 🧭 전체 Navigation 흐름 요약 (다이어그램)

```
MainScreen
│
├─ HomeScreen ───────┐
├─ GoalScreen        │
├─ ChatHistoryScreen │
├─ MyScreen          │
│                    ↓
│        (탭 내 상세로 이동)
├─ exercise_detail (상세)
├─ meal_detail (상세)
├─ home_detail (상세)
│
├─ exercise_register/{date}
├─ diet_register
├─ diet_ai_register
│
└─ edit_* (유저 편집 관련 화면들)
```

---

## 📍 안드로이드 Compose 개발 특징 반영 요약

| 특징                        | 설명                                                        |
| --------------------------- | ----------------------------------------------------------- |
| ✅ ViewModel scope 공유     | `viewModel(backStackEntry)`로 goal/home ViewModel 상태 공유 |
| ✅ Animated Nav transitions | 메인 탭 간 슬라이드 전환 / 상세 페이지 fade 효과            |
| ✅ Conditional UI           | 하단 바 노출 조건, FabMenu 조건 등 화면별 UI 분기           |
| ✅ Parameterized navigation | 날짜 등 인자를 가진 `composable("path/{param}")` 방식       |
| ✅ 인증 상태 기반 진입 제한 | SplashViewModel을 통해 로그인 상태 체크 후 리디렉션         |
