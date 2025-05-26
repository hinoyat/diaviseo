# 🧬 DiaViseo - 체성분 상세화면(HomeDetailScreen) 문서

본 문서는 디아비서 앱에서 **사용자의 체성분(체중, 체지방, 골격근, BMR 등)** 정보를 확인하고 수정할 수 있는 **HomeDetailScreen**의 구조, 컴포넌트 구성, API 연동 흐름을 설명합니다.

---

## 📌 화면 개요

- **위치**: 홈 > 오늘 소비/섭취 칼로리 게이지바 > 상세보기
- **주요 기능**:

  - 날짜별 체성분 조회
  - 키/체중/체지방/골격근 수정 (ModalBottomSheet)
  - AI 기반 피드백 제공 (예측 체중 변화)

---

## 🧱 주요 구성 요소

| 컴포넌트/클래스       | 역할                                             |
| --------------------- | ------------------------------------------------ |
| `HomeDetailScreen`    | 화면 전체 레이아웃 및 흐름 담당                  |
| `GoalViewModel`       | 선택 날짜, AI 피드백, 날짜 변경 처리             |
| `WeightViewModel`     | 체성분 조회/수정 API 처리 및 상태 관리           |
| `HomeViewModel`       | 당일 섭취/운동 칼로리 조회                       |
| `BodyInfoCard`        | 체지방, 골격근 UI 표시 및 수정 버튼              |
| `DetailInfoCard`      | 키, 체중, BMR 정보 표시 및 수정 버튼             |
| `GoalSummaryCard`     | 목표, 권장 섭취량, 운동량, 예측 결과 요약        |
| `AiTipBox`            | 오늘 날짜일 경우 피드백 표시                     |
| `BodyInfoEditSheet*`  | 체지방/골격근/키/체중 수정 시 사용되는 바텀 시트 |
| `DiaDatePickerDialog` | 날짜 선택 캘린더 모달                            |

---

## 🧭 전체 흐름 요약

### ✅ 날짜 변경 시

1. 날짜 선택 → `GoalViewModel.loadDataForDate(date)`
2. 아래 API 호출 병렬 수행:

   - `WeightViewModel.fetchPhysicalInfo(date)`
   - `WeightViewModel.loadLatestBodyData(date)`
   - `HomeViewModel.fetchDailyNutrition(date)`
   - `HomeViewModel.fetchDailyExercise(date)`

3. 오늘 날짜일 경우 → AI 피드백 호출 (`GoalViewModel.isThereFeedback()`)

### ✅ 체성분 수정 시

- 수정 버튼 클릭 → `ModalBottomSheet` 열림
- 값 입력 후 확인:

  - `bodyId` 존재 시: `WeightViewModel.updateBodyInfo()`
  - `bodyId` 없음: `WeightViewModel.registerBodyData()`

- 완료 후 → 위 API들 재호출하여 최신 상태 반영

---

## 🔄 API 연동 구조

| 기능                  | API Endpoint                                 | ViewModel 함수              |
| --------------------- | -------------------------------------------- | --------------------------- |
| 당일 체성분 조회      | `GET /bodies?date=`                          | `loadLatestBodyData(date)`  |
| 당일 신체 정보 조회   | `GET /users/physical-info?date=`             | `fetchPhysicalInfo(date)`   |
| 체성분 정보 수정      | `PATCH /bodies/{bodyid}`                     | `updateBodyInfo(id, req)`   |
| 체성분 첫 등록        | `POST /bodies`                               | `registerBodyData(req)`     |
| 일일 영양소 조회      | `GET /meals/daily-nutrition?date=`           | `fetchDailyNutrition(date)` |
| 일일 운동 칼로리 조회 | `GET /exercises/today`                       | `fetchDailyExercise(date)`  |
| AI 피드백 요청        | `GET /chatbot/feedback/{feedbackType}&date=` | `isThereFeedback()`         |
| AI 예측 요청          | `GET /chatbot/weight/trend?date=`            | `createHomeFeedBack()`      |

---

## 📦 상태 관리 구조

### `GoalViewModel`

| 상태              | 타입                   | 설명                  |
| ----------------- | ---------------------- | --------------------- |
| `selectedDate`    | `StateFlow<LocalDate>` | 선택된 날짜           |
| `workoutFeedback` | `StateFlow<String>`    | 운동 피드백 메시지    |
| `isWorkLoading`   | `StateFlow<Boolean>`   | 피드백 로딩 여부      |
| `showDatePicker`  | `StateFlow<Boolean>`   | 캘린더 모달 표시 여부 |

### `WeightViewModel`

| 상태             | 타입                                   | 설명                     |
| ---------------- | -------------------------------------- | ------------------------ |
| `bodyLatestInfo` | `StateFlow<BodyInfoResponse?>`         | 가장 최근 체성분 정보    |
| `physicalInfo`   | `StateFlow<UserPhysicalInfoResponse?>` | 추천 섭취/운동량, BMR 등 |
| `isLoading`      | `StateFlow<Boolean>`                   | 전체 로딩 상태           |

### `HomeViewModel`

| 상태                   | 타입             | 설명             |
| ---------------------- | ---------------- | ---------------- |
| `totalCalorie`         | `StateFlow<Int>` | 일일 섭취 칼로리 |
| `totalExerciseCalorie` | `StateFlow<Int>` | 일일 운동 칼로리 |

---

## 🎨 UI 연동 요약

| 위치               | 기능                            | 관련 상태                                      |
| ------------------ | ------------------------------- | ---------------------------------------------- |
| `BodyInfoCard`     | 체지방/골격근 표시 및 수정 버튼 | `muscleMass`, `bodyFat`                        |
| `DetailInfoCard`   | 키, 체중, BMR 표시 및 수정 버튼 | `userHeight`, `userWeight`, `bmr`              |
| `GoalSummaryCard`  | 목표 + 칼로리 합산 요약         | `recommendedIntake`, `tdee`, `predictValue` 등 |
| `AiTipBox`         | 오늘일 경우 피드백 메시지 표시  | `workoutFeedback`, `isToday`                   |
| `DatePickerDialog` | 날짜 선택 캘린더                | `showDatePicker`, `selectedDate`               |

---

## 🛠️ 예외 및 처리

- 체성분 정보가 없으면(`bodyId == null`) → POST 등록 처리
- 오늘 날짜가 아닌 경우 → 피드백 표시되지 않도록 상태 초기화(`setWorkoutFeedback()`)
- API 호출 실패 시 각 ViewModel 내부에서 로그 처리 및 fallback 적용

---

## 🧪 테스트 포인트

- 체성분이 없는 날짜에 진입 → 등록 flow 정상 동작 여부
- 오늘 날짜 → AiTipBox 피드백이 나타나는지
- 등록 후 다시 진입 시 정보가 반영되어 있는지 확인

---

## 📌 요약

| 기능                         | 설명                                                |
| ---------------------------- | --------------------------------------------------- |
| 체성분 표시 및 수정          | `BodyInfoCard`, `DetailInfoCard` + 바텀시트         |
| 날짜별 조회 및 상태 업데이트 | 날짜 선택 → 모든 데이터 갱신 (체성분, 영양소, 운동) |
| AI 기반 운동 피드백          | 오늘일 경우만 `AiTipBox` 노출                       |
| 상태 관리 전담 ViewModel     | `GoalViewModel`, `WeightViewModel`, `HomeViewModel` |
