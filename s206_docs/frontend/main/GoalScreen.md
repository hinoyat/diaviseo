# 📘 DiaViseo - GoalScreen 개발자 문서

### 🧭 개요

`GoalScreen`은 사용자의 건강 관련 데이터를 날짜별로 조회하고, **식단 / 운동 / 체중**에 대한 통계와 AI 피드백을 확인할 수 있는 화면입니다.

이 화면은 `BottomNavigation`의 `"goal"` 탭에서 진입되며, 상단 날짜 헤더, 탭 선택 UI, 날짜 선택 모달, 평가 결과 및 통계를 보여주는 `GoalContent`로 구성됩니다.

---

### 🧱 구성 요소

#### 1. `GoalTopHeader`

- 상단 날짜 영역 + 탭 선택 컴포넌트
- 오늘 날짜와 비교해 색상을 조절하며, 요일을 함께 표시
- 사용자 선택에 따라 `selectedTab` 상태를 업데이트
- 캘린더 아이콘 클릭 시 날짜 선택 모달 (`DiaDatePickerDialog`) 활성화

#### 2. `GoalContent`

- 실질적인 콘텐츠 영역
- 선택된 탭(`식단`, `운동`, `체중`)에 따라 각기 다른 섹션을 렌더링

#### 3. `DiaDatePickerDialog`

- 날짜를 선택할 수 있는 모달 다이얼로그
- 날짜를 선택하면 `GoalViewModel.loadDataForDate(date)`를 호출하여 데이터 갱신

---

### 🧠 사용 ViewModel

#### ✅ GoalViewModel

- 날짜 상태 관리: `selectedDate`, `showDatePicker`
- 피드백 로딩: `isLoading`, `nutritionFeedback`, `weightFeedback`, ...
- 날짜 변경 시 상태 초기화 및 데이터 호출

#### ✅ MealViewModel

- 식단 관련 통계, 일일 섭취 정보 등을 `GoalContent`에서 활용

#### ✅ ExerciseViewModel / WeightViewModel

- 각각 운동 기록 및 체성분 기록을 불러오고, `GoalContent`에서 처리

---

### 📦 상태 관리 구조

| 변수             | 역할                                           |
| ---------------- | ---------------------------------------------- |
| `selectedTab`    | 현재 선택된 탭 (`식단`, `운동`, `체중`)을 기억 |
| `selectedDate`   | 선택된 날짜                                    |
| `showDatePicker` | 날짜 선택 모달 표시 여부                       |
| `isLoading`      | 전체 로딩 상태 (뷰 전체 오버레이로 처리됨)     |

---

### 🔄 동작 흐름

```
GoalScreen 진입
    └── GoalViewModel 및 MealViewModel 초기화
        ├── selectedDate: LocalDate.now()
        ├── selectedTab: 식단(기본값)
    └── GoalTopHeader 렌더링
        └── 날짜, 요일, 탭 선택 UI
    └── GoalContent 렌더링 (선택된 탭 기반)
        └── 각 ViewModel에서 필요한 데이터 fetch
    └── DiaDatePickerDialog (모달로 동작)
        └── 날짜 선택 → ViewModel에 날짜 전달 → 데이터 갱신
```

---

### 📌 주의사항

- `GoalScreen`은 **다수의 ViewModel 간 상태 동기화**가 중요합니다. 날짜(`selectedDate`) 변경에 따라 연동된 모든 데이터를 재요청해야 합니다.
- `GoalContent`의 내용은 매우 방대하므로, **구현 및 유지보수를 위해 별도 모듈화**되어 있어야 하며, 공통된 날짜 상태를 공유해야 합니다.
- 상단 UI(`GoalTopHeader`)는 사용자의 **요일 감성 디자인**을 고려해 색상 및 텍스트 스타일이 변합니다.
- `selectedTab`은 문자열로 관리되고 있으며 `"식단"`, `"운동"`, `"체중"` 외의 값은 허용되지 않아야 합니다.

---

다음은 **`GoalContent` 컴포저블**에 대한 Android 개발자 문서입니다. 이 컴포저블은 사용자에게 **식단/운동/체중**에 대한 통계 및 AI 평가 피드백을 제공하는 핵심 UI입니다.

---

### ✅ GoalContent 개요

`GoalContent`는 건강 관리 앱에서 "식단", "운동", "체중" 탭에 따라 **다양한 통계 데이터와 AI 평가 코멘트**를 보여주는 메인 뷰입니다.
탭에 따라 서로 다른 컴포넌트와 ViewModel 데이터를 활용하며, 유저가 날짜를 변경할 경우 동기적으로 API를 호출해 최신 상태로 갱신합니다.

---

### 🧱 구성 요소

| 탭 구분 | 주요 컴포넌트                                                      | 데이터 출처(ViewModel)               |
| ------- | ------------------------------------------------------------------ | ------------------------------------ |
| 식단    | DonutChartWithLegend, MealChartSection, AiTipBox                   | `MealViewModel`, `GoalViewModel`     |
| 운동    | GoalExerciseSection, LineChartSection, StepBarChart                | `ExerciseViewModel`, `GoalViewModel` |
| 체중    | WeightOverviewSection, BMRMBISection, WeightChartSection, AiTipBox | `WeightViewModel`, `GoalViewModel`   |

---

### 🔁 주요 로직 흐름

#### 1. 날짜 변경 처리

- `GoalViewModel.selectedDate`가 변경되면 관련 ViewModel들이 데이터를 다시 불러옴

```kotlin
LaunchedEffect(selectedDate) {
    weightViewModel.loadBodyData(selectedDate.toString())
    coroutineScope {
        goalViewModel.isThereFeedback("nutrition", selectedDate.toString())
        goalViewModel.isThereFeedback("weight_trend", selectedDate.toString())
    }
    // 식단/운동 데이터도 동기적으로 로딩
    mealViewModel.fetchPhysicalInfo(...)
    mealViewModel.fetchMealStatistic(...)
    exerciseViewModel.fetchAllStats(...)
}
```

#### 2. AI 피드백 생성

- 데이터가 존재하면 `AiTipBox`에 피드백 출력
- 없으면 안내 박스 노출
- 버튼 클릭 시 `GoalViewModel.create[Type]FeedBack()` 호출

#### 3. 탭 선택 처리

- `"식단"`, `"운동"`, `"체중"` 탭 상태는 `selectedTab` 문자열로 구분
- 각 탭마다 서로 다른 UI 구성

---

### 📊 주요 UI 컴포넌트 설명

#### 🍚 식단 탭

- **DonutChartWithLegend**
  : 섭취 칼로리 대비 권장 칼로리 비율 시각화
- **MealChartSection**
  : 기간별(일/주/월) 누적 섭취 통계 → `StakedBarChart`로 시각화
- **AiTipBox**
  : AI가 분석한 식습관 평가 제공

#### 💪 운동 탭

- **GoalExerciseSection**
  : 오늘 운동한 목록 및 칼로리/시간 정보
- **LineChartSection**
  : 기간별 소모 칼로리 궤적을 `LineChart`로 시각화
- **StepBarChart**
  : 최근 7일 걸음수 막대 그래프

#### ⚖️ 체중 탭

- **WeightOverviewSection**
  : 체중/근육/지방 비율을 인바디 스타일 그래프로 표시
- **BMRMBISection**
  : BMR/BMI 계산값 출력
- **WeightChartSection**
  : 기간별 체중/근육/지방 궤적을 multi-line chart로 표현
- **AiTipBox**
  : 체성분 분석 기반 AI 피드백

---

### 🔗 사용 ViewModel 요약

| ViewModel           | 역할                                                 |
| ------------------- | ---------------------------------------------------- |
| `GoalViewModel`     | 날짜 선택, AI 피드백 생성 및 상태 관리               |
| `MealViewModel`     | 식단 관련 데이터 (칼로리, 탄단지 비율, 통계 등) 관리 |
| `ExerciseViewModel` | 운동 기록, 걸음 수, 통계 처리                        |
| `WeightViewModel`   | 체성분 (몸무게/근육/지방), 통계 그래프 관리          |

---

### 🔄 API 호출 예시

| 기능                | API 명세                                          |
| ------------------- | ------------------------------------------------- |
| 일별 식단 조회      | `GET /meals/daily-nutrition?date=`                |
| AI 식단 피드백 생성 | `POST /chatbot/nutrition_feedback?feedback_date=` |
| 일별 운동 조회      | `GET /exercises/today?date=`                      |
| 체성분 조회         | `GET /bodies/date?date=`                          |

---

### 📝 개발 시 유의 사항

- `selectedDate` 기반으로 ViewModel들이 모두 자동 호출되므로 side-effect 처리에 주의할 것
- `AiTipBox`는 데이터 존재 여부에 따라 조건 분기 렌더링
- Vico 차트는 성능상 recomposition 최소화를 위해 `remember`와 `.mapIndexed` 처리 권장
- `ExerciseItemCard`, `LegendRow`, `PeriodSelector` 등은 재사용 가능성이 높음 → 별도 모듈화 권장
