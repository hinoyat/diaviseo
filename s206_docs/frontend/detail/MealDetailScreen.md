# 🍱 DiaViseo - 식단 상세페이지 (MealDetailScreen) 문서

본 문서는 디아비서 앱의 **하루 식단 기록 화면인 MealDetailScreen**의 구조와 데이터 흐름을 설명합니다.
사용자는 날짜별로 아침/점심/저녁/간식 기록을 확인, 등록, 수정할 수 있으며, **일일 섭취 영양 정보 시각화**, **도넛 차트** 등의 기능을 제공합니다.

---

## 📌 화면 개요

- **위치**: 홈 > 오늘 섭취 칼로리 or 평가 > 식단 상세 보러가기
- **주요 기능**:

  - 선택 날짜의 일일 식단 기록 확인
  - 끼니별 음식 리스트 및 영양 성분 보기
  - 섭취 비율 도넛 차트 시각화
  - 식단 기록 추가/수정/스킵 처리

---

## 🧱 주요 컴포넌트 구성

| 컴포넌트/클래스        | 역할                                         |
| ---------------------- | -------------------------------------------- |
| `MealDetailScreen`     | 전체 식단 상세 화면 및 상태 처리             |
| `DonutChartWithLegend` | 일일 섭취량 vs 권장량 시각화                 |
| `MealCard`             | 식단이 존재할 경우 음식/영양 정보 카드       |
| `MealSkippedCard`      | 사용자가 끼니를 '건너뛰었다'고 명시한 경우   |
| `MealEmptyCard`        | 아무 기록도 없는 경우 식단 추가 or 스킵 유도 |
| `DiaDatePickerDialog`  | 날짜 선택 모달 다이얼로그                    |

---

## 📂 상태 및 ViewModel 연동

### `GoalViewModel`

| 상태             | 타입                   | 설명                     |
| ---------------- | ---------------------- | ------------------------ |
| `selectedDate`   | `StateFlow<LocalDate>` | 선택된 날짜              |
| `showDatePicker` | `StateFlow<Boolean>`   | 날짜 선택 모달 표시 여부 |
| `isLoading`      | `StateFlow<Boolean>`   | 공통 로딩 표시 여부      |

### `MealViewModel`

| 상태                       | 타입                                   | 설명                     |
| -------------------------- | -------------------------------------- | ------------------------ |
| `mealDaily`                | `StateFlow<MealDailyResponse?>`        | 끼니별 음식 및 영양 정보 |
| `dailyNutrition`           | `StateFlow<DailyNutritionResponse?>`   | 하루 섭취한 영양소 합산  |
| `nowPhysicalInfo`          | `StateFlow<UserPhysicalInfoResponse?>` | 사용자 권장 섭취량       |
| `carbRatio`/`fatRatio`/... | `StateFlow<Double>`                    | 비율 계산용 상태값       |
| `isLoading`                | `StateFlow<Boolean>`                   | API 호출 로딩 여부       |

### `DietSearchViewModel`

| 상태            | 타입                     | 설명                          |
| --------------- | ------------------------ | ----------------------------- |
| `selectedItems` | `List<FoodWithQuantity>` | 등록/수정 시 선택된 음식 목록 |
| `selectedDate`  | `LocalDate`              | 식단 등록 시 사용 날짜        |
| `selectedMeal`  | `StateFlow<String>`      | 선택된 끼니 (아침, 점심 등)   |
| `selectedTime`  | `LocalTime?`             | 사용자가 먹은 시간            |

---

## 🔁 전체 흐름 요약

### ✅ 날짜 진입 시

1. 화면 진입 → `LaunchedEffect(selectedDate)` 실행
2. `MealViewModel`에서 다음 3개 호출:

   - `fetchPhysicalInfo(date)`
   - `fetchMealDaily(date)`
   - `fetchDailyNutrition(date)`

3. StateFlow로 각 View에 바인딩 → DonutChart + 끼니별 카드 렌더링

### ✅ 끼니별 식단 UI 분기

| 조건                          | 노출 컴포넌트     |
| ----------------------------- | ----------------- |
| 음식 데이터 존재 + 음식 ≥ 1개 | `MealCard`        |
| 음식 데이터 존재 + 음식 = 0   | `MealSkippedCard` |
| 음식 데이터 없음 (`null`)     | `MealEmptyCard`   |

---

## 🍽️ 식단 등록 / 수정 흐름

### ⬅️ 수정 버튼 클릭 시

1. `dietViewModel`에 날짜, 끼니, 시간, 음식 리스트 바인딩
2. `navController.navigate("diet_register")` 이동

### ➕ 빈 상태 추가 클릭 시

- `MealEmptyCard`에서 `onWriteClick` → `diet_register` 이동
- `onSkippedClick` → 식단 스킵 API 호출 (`submitDiet`)

  - 등록 성공 후 `mealViewModel.fetchMealDaily()` 재호출

---

## 🔄 API 연동 구조

| 기능                | API Endpoint                          | ViewModel 함수              |
| ------------------- | ------------------------------------- | --------------------------- |
| 하루 식단 전체 조회 | `GET /meals/date/{date}`              | `fetchMealDaily(date)`      |
| 일일 영양소 요약    | `GET /meals/daily-nutrition?date=`    | `fetchDailyNutrition(date)` |
| 권장 섭취량 조회    | `GET /users/physical-info?date=`      | `fetchPhysicalInfo(date)`   |
| 식단 등록/수정      | `POST /meals` (`multipart/form-data`) | `submitDiet()`              |
| 음식 검색           | `GET /foods/search/name?name=`        | `searchFoods()`             |
| 즐겨찾기 음식 목록  | `GET /foods/favorites`                | `fetchFavoriteFoods()`      |
| 최근 먹은 음식 목록 | `GET /foods/recent-foods`             | `fetchRecentFoods()`        |

---

## 📊 도넛 차트 구성

- `DonutChartWithLegend`에 표시되는 요소:

  - 실제 섭취 칼로리 (`dailyNutrition.totalCalorie`)
  - 권장 섭취 칼로리 (`nowPhysicalInfo.recommendedIntake`)
  - 각 영양소별 비율 (탄/단/지/당)

비율 계산 공식은 다음과 같음:

```kotlin
fun calculateRatio(nutrient: Double?, calorie: Int?, scale: Int = 2, n: Int): Double
```

---

## 🧪 테스트 포인트

- 날짜 변경 → 도넛 차트 및 카드 UI 자동 갱신되는지
- 끼니별로 `MealCard`, `MealSkippedCard`, `MealEmptyCard` 정상 분기 여부
- 식단 스킵 → API 등록 후 반영되는지
- `수정하기` 클릭 시 기존 음식 수량까지 전달되는지

---

## 📌 요약

| 항목             | 내용                                              |
| ---------------- | ------------------------------------------------- |
| 날짜 변경        | 3개 API 호출 → 식단/영양/권장량 모두 갱신         |
| 끼니 상태 UI     | 존재/스킵/없음 3가지 분기 처리                    |
| 음식 등록/수정   | `dietViewModel`에 상태 저장 후 화면 이동          |
| 시각화 차트      | 탄/단/지/당 + 권장/실제 섭취량 시각 비교          |
| 데이터 저장 방식 | 모든 식단 요청은 Multipart (`images`, `mealData`) |
