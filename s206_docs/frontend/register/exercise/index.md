# 🏋️ 운동 등록 기능 문서 (Exercise Registration)

**이 문서는 디아비서(DiaViseo) 앱의 운동 등록 기능에 대한 전체적인 흐름, 화면 구성, API 연동 방식, ViewModel 설계 등을 설명합니다.**

> ⚠️ 참고: 현재 구글 헬스 커넥트(Health Connect) SDK에서 삼성 헬스 데이터를 직접적으로 가져오는 방식이 제한되어 있어, 디아비서는 Health Connect를 통해 연동된 운동 세션 또한 **삼성 헬스 기반 운동 ID 및 구조**를 기준으로 통일하여 관리하고 있습니다. 따라서 사용자 운동 기록은 직접 등록이든 자동 연동이든 모두 **삼성 헬스 기반 운동 리스트**(`exerciseNumber`)를 기준으로 서버에 저장됩니다.

## 📱 화면 구성 개요

운동 등록 기능은 다음과 같은 화면 및 컴포넌트로 구성됩니다:

### 주요 화면

| 화면 이름                        | 설명                                                   |
| -------------------------------- | ------------------------------------------------------ |
| `ExerciseRegisterMainScreen.kt`  | 운동 등록 메인 화면, 최근/즐겨찾기/검색 탭 제공        |
| `ExerciseRegisterBottomSheet.kt` | 운동 상세 입력 바텀시트 (시간, 시작시간, 날짜 입력 등) |

### 서브 컴포넌트

| 파일                          | 설명                              |
| ----------------------------- | --------------------------------- |
| `ExerciseListSection.kt`      | 탭별 운동 리스트 표시 섹션        |
| `ExerciseSearchResultList.kt` | 검색 결과 리스트 UI               |
| `ExerciseEmptyStateView.kt`   | 비어있는 탭일 때 노출되는 안내 UI |

## 🧠 ViewModel 구조 및 역할

### 📌 `ExerciseRecordViewModel.kt`

- 운동 등록 전체 흐름을 관리하는 핵심 ViewModel
- 선택된 운동 상태, 입력된 시간/시작시간/날짜 상태 관리
- `submitExercise()`를 통해 서버로 등록 요청 수행
- 즐겨찾기 토글 및 최근/즐겨찾기 운동 목록 조회 지원

### 📌 `ExerciseSearchViewModel.kt`

- 운동 이름 또는 카테고리 기준으로 검색 기능 제공
- 검색어 변경 시 `filteredExercises` 흐름이 자동 필터링

### 📌 `ExerciseSyncViewModel.kt`

- Health Connect를 통해 수집한 운동/걸음 데이터를 서버로 업로드하는 기능 제공
- 수동/자동 동기화 모두 이 ViewModel을 통해 이뤄짐

## 🔄 운동 등록 플로우

### 1. 운동 선택 및 상세 입력

- 사용자에게 최근/즐겨찾기/검색을 통해 운동을 선택하게 함
- 선택 시 `ExerciseRecordViewModel.setExercise()` 호출
- `ExerciseRegisterBottomSheet.kt`에서 시간, 시작 시간, 날짜 입력을 유도

### 2. 등록 요청 수행

- 입력이 완료되면 `submitExercise()` 호출
- 내부적으로 `ExerciseRecordRequest` 객체 생성 후 `registerExercise()` API 요청 수행
- 성공 시 최근 운동 목록 자동 갱신

## 📦 운동 데이터 모델 (서버 기준)

```json
{
  "exerciseNumber": 56, // 운동 타입 ID (예: 달리기)
  "exerciseDate": "2025-05-25T07:30:00", // 운동 시작 시간 (ISO 8601)
  "exerciseTime": 40 // 운동 시간 (분)
}
```

> `ExerciseRecordRequest` 객체에 해당하며, ViewModel에서 자동 생성됩니다.

## 🔐 API 연동

### ✅ 운동 등록 API

```
POST /exercises
```

- Request Body: `ExerciseRecordRequest`
- Response: `ExerciseRecordResponse`
- 호출 위치: `ExerciseRecordViewModel.submitExercise()`

### ✅ 즐겨찾기/최근 운동 목록 조회

```
GET /exercises/latest
GET /exercises/favorite
```

- 호출 위치: ViewModel 내부 자동 호출
- 즐겨찾기 추가/해제: `POST /exercises/favorite/{exerciseNumber}`

### ✅ 운동 상세 조회

```
GET /exercises/types/{exerciseNumber}
```

- 상세 정보 조회 및 `isFavorite` 상태 확인용
- `ExerciseRecordViewModel.fetchExerciseDetail()` 등에서 호출됨

## 🗃️ 기타 참고 사항

- 전체 운동 리스트(`ExerciseData.exerciseList`)는 앱에 내장된 삼성 헬스 기반 매핑 데이터를 사용함
- `exerciseIconMap`을 활용하여 운동 타입별 아이콘을 UI에 표시
- 각 운동은 `exerciseNumber`를 기준으로 백엔드와 매칭됨
- Health Connect 연동 시에도 해당 운동 ID 기준으로 기록 처리

## ⚠️ 유의사항

- 등록 화면에서 시간/시작시간/운동 타입이 모두 입력되지 않으면 버튼이 비활성화됨
- 운동 ID가 백엔드 매핑과 일치하지 않을 경우 등록이 거부되므로, 로컬 리스트의 일관성 유지 필요
- 운동 등록 성공 후 반드시 `fetchRecentExercises()` 호출로 최신 데이터 갱신할 것

## 📎 관련 파일 요약

| 파일                             | 설명                                 |
| -------------------------------- | ------------------------------------ |
| `ExerciseRecordViewModel.kt`     | 운동 등록 전체 상태 및 API 연동 처리 |
| `ExerciseSearchViewModel.kt`     | 검색어 기반 운동 필터링 기능 제공    |
| `ExerciseRegisterMainScreen.kt`  | 운동 등록 UI 시작 지점, 탭 구성      |
| `ExerciseRegisterBottomSheet.kt` | 등록용 상세 입력 창 (시간, 날짜 등)  |
| `ExerciseData.kt`                | 전체 운동 목록 및 칼로리 정보 보유   |
| `ExerciseIcons.kt`               | 운동 번호별 아이콘 매핑              |
| `ExerciseApiService.kt`          | 운동 관련 API 정의 인터페이스        |
| `ExerciseMapper.kt`              | API → 도메인 변환 매핑 처리          |
