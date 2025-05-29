## 📱 DiaViseo - 홈 화면 (HomeScreen) 문서

### 개요

`HomeScreen`은 앱 실행 직후 사용자가 처음 마주하게 되는 홈 대시보드 화면으로, **오늘의 건강 상태 요약**, **칼로리 상태**, **AI 챗봇 바로가기**, **걸음 수 변화** 등을 시각적으로 보여줍니다.

---

### 🔄 네비게이션 흐름

- `MainScreen`의 `NavHost(startDestination = "home")` 내에 위치함
- `HomeScreen`은 하단바 탭 `"home"`에 해당하며, 다른 주요 탭들과 애니메이션 기반으로 자연스럽게 이동
- 상세화면 이동:

  - 운동 요약 클릭 → `"exercise_detail"`
  - 식단 요약 클릭 → `"meal_detail"`

---

### 🧠 연동 ViewModel

| ViewModel          | 역할                                                                                          |
| ------------------ | --------------------------------------------------------------------------------------------- |
| `ProfileViewModel` | 사용자 프로필 및 권장 섭취/소비량, TDEE 정보 불러오기                                         |
| `HomeViewModel`    | 오늘의 총 섭취 칼로리, 운동 칼로리 데이터 요청 (`/meals/daily-nutrition`, `/exercises/today`) |
| `StepViewModel`    | 걸음 수 센서 실시간 수집, 자정 기준 보정, 데이터스토어 연동                                   |

---

### 📦 데이터 흐름 요약

1. **화면 진입 시 프로필 정보 요청**

   ```kotlin
   LaunchedEffect(Unit) { viewModel.fetchMyProfile() }
   ```

2. **현재 라우트가 "home"일 때만 일일 칼로리/운동 요청**

   ```kotlin
   LaunchedEffect(currentRoute) {
       if (currentRoute == "home") {
           homeViewModel.fetchDailyNutrition(today.toString())
           homeViewModel.fetchDailyExercise(today.toString())
       }
   }
   ```

3. **걸음 수는 `StepViewModel`이 앱 실행과 동시에 시작됨**

   - `Sensor.TYPE_STEP_COUNTER`를 실시간 수신하여 오늘 누적값 계산
   - 자정 기준값 `baseSteps`는 DataStore에서 관리되어 `todaySteps`를 보정 계산

---

### 🧱 주요 UI 구성 요소

| Composable                | 설명                                             |
| ------------------------- | ------------------------------------------------ |
| `MainHeader`              | 사용자 인사말 및 오늘 날짜                       |
| `WeightPredictionSection` | 예상 체중 변화(섭취-소비 기반 계산값) 표시       |
| `CaloriesGaugeSection`    | 오늘 섭취, 권장 섭취, 운동, 잉여 칼로리를 시각화 |
| `SummaryCardSection`      | 요약 카드 2개 (오늘 섭취 칼로리 / 활동 칼로리)   |
| `AiAssistantCard`         | AI 챗봇으로 이동하는 카드                        |
| `StepCountCard`           | 실시간 걸음 수 + 어제 대비 변화 표시 (센서 연동) |

---

### 📈 칼로리 계산 방식

```kotlin
val remainingCalorie = recommendedEat - totalCalorie
val extraBurned = recommendedFit - totalExerciseCalorie
val calorieDifference = totalCalorie - tdee - totalExerciseCalorie
```

- `WeightPredictionSection`에서는 `calorieDifference`를 기반으로 체중 증감 예측
- `CaloriesGaugeSection`에서는 위 3개 변수를 원형 게이지로 시각화

---

### 🦶 StepViewModel 작동 방식

- `TYPE_STEP_COUNTER` 센서를 기반으로 센서 누적값 수신
- `DataStore`에서 자정 기준 `baseSteps`와 어제 걸음 수 관리
- 자정 이후 다시 앱 실행하면 자동 보정
- `refreshStepCount()` 호출 시 센서 리스너 재등록 없이 센서 값만 재조회

---

### ⛑ 개발자 팁

- `MainScreen`의 `isFabMenuOpen`이 true인 경우, `FabOverlayMenu`가 `Scaffold` 외부에 표시됨 (조건부 UI 주의)
- `currentBackStackEntryAsState()`를 통해 현재 화면 route를 추적하며 ViewModel 업데이트 조건으로 활용
- Sensor 리스너 등록 해제는 `onCleared()`에서 필수

---

### 💬 To-Do / 고려사항

- `StepViewModel`에서 정확한 센서 기반 걸음 수 초기화 (`baseSteps`가 0일 경우 예외 처리 강화)
- `HomeScreen`에 수면 데이터, 혈당 데이터 등 확장 고려
- 요약 카드 클릭 시 애니메이션 이동 연계 가능성 탐색
