# 👤 마이페이지 기능 흐름 문서 (MyPage Feature)

디아비서(DiaViseo) 앱의 마이페이지 기능은 사용자의 개인정보, 건강정보, 앱 설정 등을 종합적으로 관리할 수 있도록 구성되어 있습니다. 사용자는 프로필 수정, 건강 데이터 입력, 헬스커넥트 연동, 알림 설정 등 다양한 개인화 기능을 이용할 수 있습니다.

---

## 📌 기능 개요

| 항목 | 설명 |
|------|------|
| 프로필 관리 | 회원정보 수정, 신체정보 입력, 닉네임/생년월일 변경 |
| 건강 데이터 | 알러지, 기저질환, 선호 운동 설정 |
| 연동 관리 | 헬스커넥트 연결/해제, 수동 동기화 |
| 설정 기능 | 알림 온/오프, FAQ, 회원탈퇴/로그아웃 |
| UI 패턴 | 바텀시트 편집, 검색 기능, 태그 선택 시스템 |

---

## 🧩 메인 화면 구조: `MyScreen`

### 섹션별 구성

| 섹션 | 컴포넌트 | 설명 |
|------|----------|------|
| **헤더** | `MyHeaderSection` | 사용자명 + 캐릭터 이미지 + 회원정보 수정 링크 |
| **프로필** | `MyPhysicalInfoCard` | 키/몸무게 표시 + 수정 버튼 |
| **건강정보** | `MyHealthDataList` | 알러지, 기저질환 수정 링크 |
| **연동** | `MySyncSection` | 헬스커넥트 연동 관리 |
| **설정** | `MyAlarmSettingCard` | 알림 온/오프 토글 |
| **고객관리** | `MyFaqCard` | FAQ 페이지 링크 |

### 데이터 로딩 및 상태 관리

```kotlin
val profileViewModel: ProfileViewModel = viewModel()
val profile by profileViewModel.myProfile.collectAsState()

LaunchedEffect(Unit) {
    profileViewModel.fetchMyProfile()
}
```

---

## 🔄 주요 편집 화면들

### `UserProfileEditScreen` (회원정보 수정)

**기능:**
- 닉네임, 생년월일 수정 (바텀시트)
- 기본정보 조회 (이름, 성별, 핸드폰)
- 회원탈퇴, 로그아웃

**바텀시트 구조:**
```kotlin
// 닉네임 수정
EditNicknameBottomSheet(
    initialNickname = profile?.nickname ?: "",
    onSave = { newName ->
        profileViewModel.updateUserProfile(
            request = UserUpdateRequest(nickname = newName)
        )
    }
)

// 생년월일 수정
EditBirthDateBottomSheet(
    initialBirthDate = profile?.birthday?.replace("-", ".") ?: "",
    onSave = { input ->
        val isoFormatted = input.replace(".", "-")
        profileViewModel.updateUserProfile(
            request = UserUpdateRequest(birthday = isoFormatted)
        )
    }
)
```

### `PhysicalInfoEditScreen` (신체정보 수정)

**입력 검증:**
- 키: 100.0~250.0cm 범위
- 몸무게: 30.0~300.0kg 범위
- 정규식: `^\\d*\\.?\\d{0,2}$` (소수점 둘째자리까지)

### `AllergyEditScreen` / `DiseaseEditScreen`

**공통 기능:**
- 검색 모드 / 일반 모드 전환
- 태그 선택 시스템 (`SelectableTag`)
- 변경사항 감지 및 저장 확인
- `FlowRow`를 통한 태그 레이아웃

**상태 관리:**
```kotlin
val allergyList = viewModel.allergyList
val userAllergySet = viewModel.userAllergySet
val initialAllergySet = viewModel.initialUserAllergySet
val hasChanges = userAllergySet != initialAllergySet
```

**검색 기능:**
```kotlin
val filteredList = if (isSearchMode) {
    allergyList.filter {
        it.allergyName.contains(searchValue.text, ignoreCase = true)
    }
} else allergyList
```

---

## 🔧 주요 컴포넌트 상세

### `HealthConnectManageScreen`

**연동 상태 관리:**
```kotlin
val isConnected by viewModel.isConnected.collectAsState()
val lastSyncedAt by viewModel.lastSyncedAt.collectAsState()
```

**동기화 애니메이션:**
```kotlin
val rotation = remember { Animatable(0f) }

LaunchedEffect(isSyncing) {
    if (isSyncing) {
        rotation.animateTo(360f, infiniteRepeatable(...))
    }
}
```

**권한 처리:**
```kotlin
val permissionLauncher = rememberLauncherForActivityResult(
    contract = PermissionController.createRequestPermissionResultContract()
) { granted ->
    HealthConnectSyncExecutor.handlePermissionResult(...)
}
```

### `ExerciseSearchBottomSheetContent`

**운동 검색 및 선택:**
- 카테고리별 필터링 (`SelectableCategory`)
- 운동명 검색
- 선택/해제 다이얼로그 확인
- 다중 선택 지원

```kotlin
val filteredExercises = allExercises
    .filter {
        (selectedCategoryId == null || it.categoryId == selectedCategoryId) &&
        it.name.contains(searchQuery, ignoreCase = true)
    }
```

### `FaqScreen`

**확장 가능한 FAQ 아이템:**
```kotlin
@Composable
fun FaqExpandableItem(faq: FaqItem) {
    var expanded by remember { mutableStateOf(false) }
    
    AnimatedVisibility(visible = expanded) {
        Text(text = faq.answer)
    }
}
```

---

## 💡 UX 특별 기능

### 변경사항 감지 시스템

**알러지/기저질환 화면:**
```kotlin
val hasChanges = userAllergySet != initialAllergySet

// 변경사항 알림 바
AnimatedVisibility(visible = hasChanges) {
    Row(modifier = Modifier.background(Color(0xFFE6F7FF))) {
        Text("변경사항이 있습니다")
        TextButton("취소") { viewModel.revertChanges() }
    }
}

// 뒤로가기 처리
BackHandler {
    if (hasChanges) {
        showConfirmDialog = true
    } else {
        navController?.popBackStack()
    }
}
```

### 검색 모드 전환

```kotlin
var isSearchMode by remember { mutableStateOf(false) }

// 검색 모드 진입
Text(
    text = "🔍 찾는 알러지가 없나요?",
    modifier = Modifier.clickable { isSearchMode = true }
)

// 검색창 표시
if (isSearchMode) {
    CommonSearchTopBar(
        placeholder = "어떤 알러지가 있으신가요?",
        keyword = searchValue.text,
        onKeywordChange = { searchValue = TextFieldValue(it) }
    )
}
```

### 바텀시트 패턴

```kotlin
@OptIn(ExperimentalMaterial3Api::class)
val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

if (showBottomSheet) {
    ModalBottomSheet(
        containerColor = Color.White,
        onDismissRequest = { showBottomSheet = false },
        sheetState = sheetState
    ) {
        // 바텀시트 내용
    }
}
```

---

## 📱 네비게이션 흐름

### 메인 마이페이지에서 분기

```
MyScreen (메인 마이페이지)
│
├─ 회원정보 수정 → UserProfileEditScreen
│   ├─ 닉네임 수정 → EditNicknameBottomSheet
│   └─ 생년월일 수정 → EditBirthDateBottomSheet
│
├─ 신체정보 수정 → PhysicalInfoEditScreen
│
├─ 알러지 수정 → AllergyEditScreen
├─ 기저질환 수정 → DiseaseEditScreen
├─ 선호운동 수정 → PreferredExerciseScreen
│
├─ 헬스커넥트 → HealthConnectManageScreen
│
└─ FAQ → FaqScreen
```

### 편집 화면에서의 저장/취소 플로우

```
편집 화면 진입
│
├─ 변경사항 있음
│   ├─ 저장 → API 호출 → 성공 시 뒤로가기
│   ├─ 취소 → 확인 다이얼로그 → 변경사항 폐기
│   └─ 뒤로가기 → 저장 확인 다이얼로그
│
└─ 변경사항 없음 → 바로 뒤로가기
```

---

## 🛠 데이터 모델 및 API 연동

### 프로필 업데이트

```kotlin
// UserUpdateRequest 구조
UserUpdateRequest(
    nickname = "새닉네임",
    birthday = "2000-01-01",
    height = 170.0,
    weight = 65.0,
    notificationEnabled = true
)

// API 호출
profileViewModel.updateUserProfile(
    request = request,
    onSuccess = { /* 성공 처리 */ },
    onError = { msg -> /* 에러 처리 */ }
)
```

### 알러지/기저질환 관리

```kotlin
// ViewModel 주요 메서드
viewModel.loadAllergyData()           // 초기 데이터 로드
viewModel.toggleAllergy(allergyId)    // 알러지 선택/해제
viewModel.commitChanges()             // 변경사항 저장
viewModel.revertChanges()             // 변경사항 취소
viewModel.isSelected(allergyId)       // 선택 상태 확인
```

### 헬스커넥트 연동

```kotlin
// 연동 상태 관리
viewModel.loadSyncInfo()              // 연동 정보 로드
viewModel.setLinked(true/false)       // 연동 상태 변경
viewModel.updateSyncTime(timestamp)   // 마지막 동기화 시간 업데이트

// 수동 동기화
HealthConnectManualSync.sync(
    context = context,
    scope = coroutineScope,
    viewModel = syncViewModel,
    onComplete = { isSyncing = false },
    updateSyncTime = { now -> viewModel.updateSyncTime(now) }
)
```

---

## 🎯 예외 처리 및 검증

### 입력값 검증

```kotlin
// 신체정보 검증
val h = height.toDoubleOrNull()
val w = weight.toDoubleOrNull()

if (h != null && w != null && h in 100.0..250.0 && w in 30.0..300.0) {
    // 저장 진행
} else {
    Toast.makeText(context, "정상적인 숫자를 입력해주세요", Toast.LENGTH_SHORT).show()
}

// 닉네임 길이 제한
OutlinedTextField(
    value = nickname,
    onValueChange = { if (it.text.length <= 8) nickname = it }
)
```

### 권한 및 연결 상태 처리

```kotlin
// 헬스커넥트 앱 설치 확인
healthConnectManager?.let {
    HealthConnectPermissionHandler.requestPermissionsIfAvailable(...)
} ?: run {
    HealthConnectPermissionHandler.redirectToPlayStore(context)
}
```
