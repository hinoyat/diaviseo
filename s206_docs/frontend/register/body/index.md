# 📄 체성분 등록 흐름 문서 (Body Data Register)

디아비서(DiaViseo) 앱의 체성분 등록 기능은 사용자의 키, 체중, 체지방량, 골격근량 등을 기록하기 위해 제공됩니다. 해당 기능은 수동 입력 또는 인바디 사진을 통한 자동 인식(OCR) 중 하나를 선택하여 데이터를 등록할 수 있도록 설계되어 있습니다.

---

## 📌 기능 개요

| 항목        | 설명                                           |
| ----------- | ---------------------------------------------- |
| 등록 방식   | 1) 인바디 사진 업로드 (OCR), 2) 수동 입력 선택 |
| 필수 입력값 | 체중, 측정일자 (년/월/일)                      |
| 부가 입력값 | 키, 체지방량, 골격근량 (자동 또는 수동)        |
| 전송 방식   | API를 통해 서버에 등록 요청                    |

---

## 🧩 ViewModel 구조: `BodyRegisterViewModel`

| 상태 변수                                    | 설명                                 |
| -------------------------------------------- | ------------------------------------ |
| `weight` / `height` / `fat` / `muscle`       | 사용자가 입력한 체성분 값            |
| `year` / `month` / `day` / `measurementDate` | 날짜 입력 상태 및 조합 값            |
| `isOcrLoading`                               | 사진 등록 시 OCR 처리 진행 여부 상태 |

### 주요 메서드

- `onXXXChange(value)` → 개별 입력값 변경 핸들러
- `fetchLatestBodyData()` → 수동입력 선택 시 최근 데이터 자동 채움
- `sendOcrRequest(part)` → 이미지 업로드 후 OCR 결과값 입력값에 반영
- `registerBodyData(onSuccess, onError)` → 최종 등록 API 호출

---

## 🔄 API 연동 흐름

- 사진 등록 시:

  1. 사용자가 이미지를 선택 → `MultipartBody.Part`로 변환
  2. `sendOcrRequest()` 호출 → OCR 결과로 ViewModel 상태값 자동 반영

- 수동 입력 시:

  1. 사용자 입력값 변경 시 상태 갱신
  2. 등록 버튼 클릭 시 `registerBodyData()` 호출 → 서버 전송

---

## 🎨 UI 흐름 요약 (`BodyDataRegisterScreen.kt`)

- `SelectableIconCard`를 통해 등록 방식 선택

  - 사진 등록: 갤러리 오픈 → OCR 요청 자동 수행
  - 직접 입력: 최신 데이터 자동 채우기

- `LabeledNumberInputField`를 통해 체성분 항목 입력

- `LabeledDateInputField`를 통해 측정일자 입력

- 등록 버튼은 `weight`, `measurementDate`가 유효할 때만 활성화됨

- 등록 성공 시 Toast 메시지 출력 + 입력값 초기화

---

## 💡 예외 처리 및 UX 고려

- OCR 진행 중 로딩 상태 표시 (`WaveTextSimple` 애니메이션)
- OCR 실패 시 수동 입력 가능
- 날짜 입력은 연/월/일 필드 분리 + placeholder 제공
- 등록 후 ViewModel 상태 초기화 (`resetInput()`)

---

## 🔗 관련 컴포넌트 정리

| 컴포넌트                  | 역할                                          |
| ------------------------- | --------------------------------------------- |
| `SelectableIconCard`      | 등록 방식 선택 카드 UI (카메라 / 연필 아이콘) |
| `LabeledNumberInputField` | 숫자 입력 필드 (체중, 근육 등) + 단위 표시    |
| `LabeledDateInputField`   | 연/월/일 입력 필드 컴포넌트                   |

---

이 문서는 체성분 등록 기능에 대한 전체 흐름과 UI-로직-API 연계 구조를 정리하며, 추후 식단/운동 등록 흐름과 동일한 패턴으로 문서화됩니다. ✅
