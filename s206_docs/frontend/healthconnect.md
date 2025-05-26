# 📄 HealthConnect 연동 구조 문서

**본 문서는 디아비서(DiaViseo) 앱의 Health Connect 연동 구조 및 동기화 로직에 대해 설명합니다.**

## 📌 개요

Health Connect는 Android의 건강 데이터 통합 플랫폼으로, 디아비서는 사용자의 걸음 수와 운동 데이터를 수집하여 서버에 동기화하는 기능을 제공합니다.

- 연동 대상: `StepsRecord`, `ExerciseSessionRecord`
- 연동 방식: 수동 동기화 + 자동 동기화 (WorkManager)
- 데이터 주기: 최근 30일
- 연동 위치: `com.example.diaviseo.healthconnect`

---

## 🏗️ 주요 컴포넌트 구조

| 클래스                           | 역할                                        |
| -------------------------------- | ------------------------------------------- |
| `HealthConnectManager`           | Health Connect Client 생성 및 데이터 조회   |
| `HealthConnectPermissionHandler` | 권한 요청 및 설치 유도 처리                 |
| `HealthConnectLogger`            | 디버깅용 로그 출력                          |
| `HealthConnectManualSync`        | 마이페이지 수동 동기화 처리                 |
| `HealthDataSyncWorker`           | WorkManager 기반 자동 동기화 처리           |
| `HealthDataSyncScheduler`        | 자동 동기화 예약 등록기                     |
| `StepDataProcessor`              | StepsRecord → API 전송용 DTO 변환           |
| `ExerciseSessionRecordProcessor` | ExerciseSessionRecord → API 전송용 DTO 변환 |

---

## 🔄 동기화 흐름

### ✅ 수동 동기화

- 위치: 마이페이지 > 동기화 버튼 클릭 시 호출
- 흐름:

  1. `HealthConnectManager.readSteps()` / `readExerciseSessions()`
  2. `StepDataProcessor.process()` / `ExerciseSessionProcessor.toRequestList()`
  3. ViewModel의 `syncStepRecords()` / `syncExerciseRecords()`로 서버 전송
  4. 성공 시 마지막 동기화 시간 저장 (`HealthConnectDataStore.setLastSyncTime()`)

### ✅ 자동 동기화 (WorkManager)

- 실행 시간: 매일 밤 11시 (23:00)
- 흐름:

  1. 앱 최초 연동 시 `scheduleDailyHealthSync()` 실행
  2. `HealthDataSyncWorker`가 실행되어 최근 동기화 시점 이후의 데이터 조회
  3. 데이터 변환 후 서버로 전송
  4. 성공 시 마지막 동기화 시간 저장
  5. 실패 시 `Result.retry()`로 재시도

---

## 🛡️ 권한 및 설치 처리

- `HealthConnectManager.isAvailable()`로 설치 여부 확인
- `HealthConnectPermissionHandler.requestPermissionsIfAvailable()`로 권한 요청 또는 Play Store 이동
- `handlePermissionResult()`로 권한 수락 여부 확인 및 후속 작업 실행

---

## 🧪 디버깅 유틸

| 함수                                           | 설명                                     |
| ---------------------------------------------- | ---------------------------------------- |
| `HealthConnectLogger.logRawSteps()`            | Steps 데이터를 Logcat으로 출력           |
| `HealthConnectLogger.logRawExerciseSessions()` | ExerciseSession 데이터를 Logcat으로 출력 |

---

## 📦 데이터 처리 로직

### `StepDataProcessor`

- `StepsRecord`를 받아 `StepRecordRequest(stepDate, stepCount)`로 변환
- 날짜는 ISO_LOCAL_DATE 포맷 (yyyy-MM-dd)

### `ExerciseSessionRecordProcessor`

- `ExerciseSessionRecord`를 받아 `HealthSyncExerciseRequest`로 변환
- duration(분), 시작 시각, 운동 ID 기반 칼로리 계산 포함
- 미매핑 운동은 제외 처리

---

## 💾 저장소 구조 (DataStore)

| 항목               | 메서드                                    |
| ------------------ | ----------------------------------------- |
| 연동 여부          | `getLinked()` / `setLinked(true)`         |
| 마지막 동기화 시간 | `getLastSyncTime()` / `setLastSyncTime()` |

---

## 🎯 UI 연동 지점 요약

| 위치                           | 설명                                                                             |
| ------------------------------ | -------------------------------------------------------------------------------- |
| `FinalGuideScreen.kt`          | 온보딩 마지막 단계에서 연동 권한 요청 및 초기 데이터 업로드 수행                 |
| `HealthConnectManageScreen.kt` | 마이페이지에서 연동 상태 확인 및 수동 동기화 가능. 연동 해제 시 상태 초기화 처리 |

---

## 📝 참고 사항

- Health Connect는 Android 13 이상에서 사용 가능하며, 설치되어 있어야 동작함
- 연동 시 사용자 동의 필요 (권한 요청)
- 서버 연동은 Retrofit 기반 업로드 방식 사용
- 향후 데이터 타입 확장 시 `HealthConnectManager`에 record 추가만으로 대응 가능
