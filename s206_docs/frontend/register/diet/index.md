# 🥗 식단 등록 흐름 문서 (Diet Register)

디아비서(DiaViseo) 앱의 식단 등록 기능은 사용자가 섭취한 음식을 효과적으로 기록하고, AI 분석 및 칼로리 피드백을 받을 수 있도록 구성되어 있습니다. 사용자는 검색, 추천 세트, 즐겨찾기, 사진 인식 중 원하는 방식을 통해 식단을 등록할 수 있으며, 섭취 시간 및 끼니 설정도 함께 이루어집니다.

> ⚠️ 참고: 코드 상에는 사진 기반 AI 인식 기능의 구조가 포함되어 있으나, 실제 AI 모델 개발이 완료되지 않아 서비스에는 적용되지 않았습니다. 해당 기능은 향후 개발 및 연동 예정입니다.

---

## 📌 기능 개요

| 항목      | 설명                                                                            |
| --------- | ------------------------------------------------------------------------------- |
| 등록 방식 | 1) 음식 검색 2) 음식 세트 선택 3) 즐겨찾기 선택 4) 사진 기반 AI 인식 (※ 미구현) |
| 입력 항목 | 섭취 음식, 인분 수, 섭취 시간, 끼니 종류                                        |
| 전송 방식 | Multipart 형식으로 이미지 + JSON (`submitDiet`) 전송                            |

---

## 🧩 ViewModel 구조: `DietSearchViewModel`

| 상태 변수                                      | 설명                           |
| ---------------------------------------------- | ------------------------------ |
| `keyword`, `searchResults`, `isSearching`      | 음식 검색 관련 상태            |
| `selectedItems: List<FoodWithQuantity>`        | 선택한 음식 리스트 (인분 포함) |
| `selectedMeal`, `selectedTime`, `selectedDate` | 식사 시간/끼니 선택 상태       |
| `foodSets`, `favoriteFoods`, `recentFoods`     | 세트/즐겨찾기/최근 목록        |

### 주요 메서드

- 음식 검색: `searchFoods(keyword)`
- 음식 선택: `addSelectedFood(item)` / 수량 수정: `updateSelectedFoodQuantity()`
- 식단 등록: `submitDiet()`
- 음식 세트 조회 및 등록: `fetchFoodSets()` / `registerFoodSet(name)`
- 즐겨찾기 불러오기 및 토글: `fetchFavoriteFoods()`, `toggleFavorite(foodId)`
- 최근 음식 조회: `fetchRecentFoods()`
- 상태 초기화: `clearDietState()`

---

## 🔄 API 연동 흐름

### 1. 식단 등록 API

- **Method**: `POST /meals`
- **설명**: 사용자의 식사 시간, 끼니 정보, 섭취 음식 목록과 이미지(선택)를 함께 등록하는 API
- **요청 형식**: `multipart/form-data`

  - `mealData`: JSON 문자열 형식, 예:

    ```json
    {
      "foods": [
        { "foodId": 1, "quantity": 2 },
        { "foodId": 3, "quantity": 1 }
      ],
      "mealType": "BREAKFAST",
      "mealTime": "08:30",
      "mealDate": "2025-05-26"
    }
    ```

  - `images`: 선택적으로 이미지 파일 첨부 가능

### 2. 음식 검색 API

- **Method**: `GET /foods/search/name`
- **파라미터**: `name` (검색어)
- **응답**: `List<FoodItem>` 형태

### 3. 음식 상세 조회 API

- **Method**: `GET /foods/{foodId}`
- **응답**: `FoodDetailResponse` (영양소, 이름, 칼로리 등 포함)

### 4. 즐겨찾기 음식 API

- **조회**: `GET /foods/favorites`
- **토글**: `POST /foods/favorites/{foodId}` (있으면 제거, 없으면 추가)
- **응답**: 토글 후 상태 (`isFavorite`) 포함

### 5. 최근 음식 조회 API

- **Method**: `GET /meals/recent-foods`
- **응답**: `List<RecentFoodItemResponse>`

### 6. 음식 세트 API

- **조회**: `GET /food-sets`
- **등록**: `POST /food-sets`

  - 요청 형식 예시:

    ```json
    {
      "name": "나의 단백질 세트",
      "foods": [
        { "foodId": 5, "quantity": 2 },
        { "foodId": 8, "quantity": 1 }
      ]
    }
    ```

- **응답**: 등록된 세트의 정보 반환

---

## 🎨 주요 화면 구성

### `DietRegisterMainScreen`

- 상단 검색창 (`CommonSearchTopBar`)
- 탭 구성: `오늘 뭐먹지`, `추천 음식 세트`, `즐겨찾기`
- 음식 선택 시 `FoodDetailBottomSheet` 표시 → 인분 조절 가능

### `DietSuggestionScreen`

- 최근 섭취 음식 (`RecentFoodList`)
- 음식 세트 (`FoodSetList`, `FoodSetCard`)

### `FoodSetRegisterScreen`

- 음식 세트 구성 → 이름 입력 + 음식 선택 → `registerFoodSet()` 호출

### `DietConfirmScreen`

- `CameraFloatingIconButton`: 사진 업로드
- `MealSelector`, `MealTimePickerBottomSheet` → 식사 시간/타입 선택
- `SelectedFoodList` → 최종 확인 및 인분 수정
- `NutrientBar`, `NutrientInfoNotice` → 영양소 시각화
- `DietRegisterBottomBar` → 최종 등록 버튼 (`submitDiet()` 호출)

---

## 🔧 주요 컴포넌트

| 컴포넌트                                                         | 설명                                                 |
| ---------------------------------------------------------------- | ---------------------------------------------------- |
| `FoodDetailBottomSheet`                                          | 음식 상세 + 인분 조절 + 즐겨찾기 버튼                |
| `SelectedFoodList`                                               | 선택된 음식 표시 및 수정                             |
| `MealTimePickerBottomSheet`                                      | 섭취 시간 설정 (시/분 입력)                          |
| `MealBottomSheet`, `MealSelector`                                | 끼니 종류 선택 UI                                    |
| `NutrientBar`, `NutrientInfoNotice`                              | 영양 시각화 안내 UI                                  |
| `CameraFloatingIconButton`                                       | 이미지 등록을 위한 진입 버튼 (※ AI 인식 미연동 상태) |
| `FavoriteFoodsContent`, `SearchSuggestionList`, `RecentFoodList` | 탭별 음식 목록                                       |

---

## 💡 UX 및 예외 처리 설계

- `EmptyStateView`: 검색 결과 없음, 즐겨찾기 없음 등 상황에 따라 안내 메시지 표시
- 이미지 선택 시 바로 분석 수행 (※ 현재는 UI 구성만 완료된 상태, AI 분석 기능 연동 미완료)
- 입력 완료 조건 (`selectedItems.isNotEmpty()`, 시간/끼니 지정 여부) 만족 시만 등록 버튼 활성화
- `clearDietState()` 호출로 등록 후 상태 초기화

---

## 📝 정리 요약

- `DietSearchViewModel`이 전체 상태 관리 및 서버 연동 중심
- 화면은 Tab 구조로 기능을 명확히 분리 (검색, 추천, 즐겨찾기)
- 유저가 직접 입력한 정보와 자동 인식(이미지) 데이터를 조합하여 서버 전송
- 데이터 클래스 `FoodWithQuantity`, `FoodSet` 등을 통해 유연한 조합 지원
- AI 기반 음식 인식 기능은 추후 개발 예정이며, 현재 앱에는 비활성 상태입니다

> 이 흐름은 체성분/운동 등록과 동일한 아키텍처를 따릅니다. ✅
