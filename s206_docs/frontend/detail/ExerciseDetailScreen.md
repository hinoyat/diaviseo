# 💪 DiaViseo - 운동 상세페이지 구조 문서

본 문서는 디아비서 앱에서 사용자의 일일 운동 기록을 확인하고 수정/삭제/추가할 수 있는 **운동 상세페이지(ExerciseDetailScreen)**의 전체 구조와 흐름을 정리한 문서입니다.

---

## 📌 화면 개요

- **위치** : 홈 > 오늘 활동 칼로리 or 평가 > 운동 상세 보러가기
- **주요 기능**:

  - 해당 날짜의 운동 요약 제공 (총 시간, 칼로리)
  - 각 운동 기록별 상세 정보 제공
  - 운동 수정/삭제 기능
  - 운동 추가 기능 (운동 등록 페이지로 이동)
  - 하단에서 바텀시트로 운동 수정 가능

---

## 🏗️ 주요 컴포넌트 구조

| 컴포넌트/클래스               | 역할                                         |
| ----------------------------- | -------------------------------------------- |
| `ExerciseDetailScreen`        | 운동 상세 화면 UI 및 ViewModel 바인딩        |
| `ExerciseSummarySection`      | 총 운동 시간, 칼로리 UI 요약                 |
| `ExerciseRecordItem`          | 개별 운동 기록 아이템 (수정, 삭제 기능 포함) |
| `AddExerciseBox`              | '운동 더 추가하기' 버튼                      |
| `ExerciseRegisterBottomSheet` | 운동 수정 바텀시트 (기존 등록 UI 재사용)     |
| `DeleteDialog`                | 삭제 확인 다이얼로그                         |
| `DiaDatePickerDialog`         | 날짜 선택 다이얼로그                         |

---

## 🧭 화면 구성 흐름

### 1. 운동 데이터 불러오기

- 사용자가 화면에 진입하거나 날짜 변경 시 아래 순서로 데이터 조회

  ```kotlin
  LaunchedEffect(selectedDate) {
      exerciseViewModel.fetchDailyExercise(selectedDate.toString())
  }
  ```

- 총 시간, 칼로리, 운동 리스트 StateFlow를 관찰

  - `totalExerciseTime`, `totalCalories`, `exerciseList`

### 2. 운동 목록 렌더링

- 운동 리스트 렌더링: `ExerciseRecordItem.kt`

- 수정 버튼 클릭 시:

  - ViewModel에 운동 정보 세팅
  - 수정용 바텀시트(`ExerciseRegisterBottomSheet`) 표시

- 삭제 버튼 클릭 시:

  - `DeleteDialog` 표시 → 확인 시 서버로 DELETE 요청 및 local state 갱신

### 3. 운동 수정 바텀시트

- `ExerciseRegisterBottomSheet`는 재사용 컴포넌트
- 수정 모드 진입 시:

  - `ExerciseRecordViewModel`에 운동 ID, 시작 시간, 운동명 등 주입
  - 수정 성공 시 기존 운동 fetch 재호출

### 4. 운동 추가

- `AddExerciseBox` 클릭 → `exercise_register/{selectedDate}`로 이동

---

## 🔄 ViewModel 구조 요약

### `ExerciseViewModel.kt`

| 변수                | 설명                                     |
| ------------------- | ---------------------------------------- |
| `exerciseList`      | 일일 운동 기록 리스트 (`ExerciseDetail`) |
| `totalCalories`     | 총 소모 칼로리                           |
| `totalExerciseTime` | 총 운동 시간 (분)                        |
| `isLoading`         | 로딩 상태 표시                           |

| 함수                         | 설명                                    |
| ---------------------------- | --------------------------------------- |
| `fetchDailyExercise(date)`   | 해당 날짜의 운동 기록 및 요약 통계 조회 |
| `deleteExercise(id)`         | 운동 기록 삭제                          |
| `setTotalCalories(diff)`     | 총 칼로리 변경 (삭제 시 -값 적용)       |
| `setTotalExerciseTime(diff)` | 총 운동 시간 변경 (삭제 시 -값 적용)    |

---

### `ExerciseRecordViewModel.kt`

운동 수정 시 사용되는 임시 상태 저장용 뷰모델.

| 상태               | 설명                           |
| ------------------ | ------------------------------ |
| `exerciseId`       | 수정할 운동 ID                 |
| `exerciseTime`     | 수정할 운동 시간 (분)          |
| `registerDate`     | 등록 날짜 (`yyyy-MM-dd`)       |
| `startTime`        | 운동 시작 시간 (`HH:mm`)       |
| `selectedExercise` | 수정할 운동 객체 (id, 이름 등) |

---

## 🔌 API 연동 구조

| API Endpoint                     | 설명                                       |
| -------------------------------- | ------------------------------------------ |
| `GET /exercises/today?date=`     | 특정 날짜의 운동 기록 및 총 통계 조회      |
| `DELETE /exercises/{exerciseId}` | 특정 운동 기록 삭제                        |
| `PUT /exercises/{exerciseId}`    | 운동 수정 (바텀시트 내 `putExercise`) 사용 |

> ✅ 모든 API는 `ExerciseApiService.kt` 내에 정의되어 있습니다.

---

## 📦 데이터 흐름 정리

### 화면 진입 시

```text
날짜 선택 -> fetchDailyExercise() → 운동 리스트 렌더링
```

### 운동 수정 흐름

```text
수정 버튼 클릭 → ExerciseRecordViewModel에 값 주입 →
바텀시트 표시 → 수정 완료 시 PUT 요청 → fetchDailyExercise 재실행
```

### 운동 삭제 흐름

```text
삭제 버튼 클릭 → DeleteDialog 확인 →
DELETE 요청 → 로컬 리스트 및 통계값 수동 갱신
```

---

## 📅 날짜 선택

- `CommonTopBar`의 우측 캘린더 클릭 시 `DiaDatePickerDialog` 표시
- 날짜 변경 시 `goalViewModel.loadDataForDate()` 호출 → 상태 공유

---

## ⚠️ 주의 사항

- 운동 수정은 `ExerciseRecordViewModel`을 통해 이뤄집니다. 이전 `등록 UI`를 재활용합니다.
- 삭제 시 로컬에서 리스트 및 통계 상태를 직접 갱신해야 합니다 (`setTotalCalories()`, `setTotalExerciseTime()`).
- 날짜 변경 시 반드시 운동 기록 재조회 (`fetchDailyExercise(date)`).

---

## 🗂️ 관련 파일

| 파일명                           | 설명                             |
| -------------------------------- | -------------------------------- |
| `ExerciseDetailScreen.kt`        | 운동 상세 메인 화면              |
| `ExerciseViewModel.kt`           | 일일 운동 기록 및 통계 상태 관리 |
| `ExerciseRecordViewModel.kt`     | 운동 수정 시 상태 관리용         |
| `ExerciseRegisterBottomSheet.kt` | 운동 수정 UI                     |
| `ExerciseApiService.kt`          | 운동 관련 API 정의               |
