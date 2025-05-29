# 🔐 Token Handling in DiaViseo App

이 문서는 디아비서(DiaViseo) 앱에서 **JWT 토큰 발급, 저장, 재발급, 삭제** 흐름을 설명합니다. 프론트엔드(Android, Kotlin) 관점에서의 구조, 사용 위치, 갱신 전략을 다룹니다.

## ✅ 토큰 종류

| 이름              | 설명                  | 저장 방식 | 유효기간 |
| ----------------- | --------------------- | --------- | -------- |
| **Access Token**  | API 요청 시 사용      | DataStore | 1시간    |
| **Refresh Token** | Access Token 재발급용 | DataStore | 100시간  |

## 📂 저장 위치

모든 토큰은 `TokenDataStore.kt`를 통해 DataStore에 저장 및 관리됩니다.

```kotlin
val Context.dataStore by preferencesDataStore(name = "user_prefs")
```

### 주요 함수

- `saveAccessToken(context, token)`
- `saveRefreshToken(context, token)`
- `getAccessToken(context): Flow<String?>`
- `getRefreshToken(context): Flow<String?>`
- `clearAccessToken(context)` → Access + Refresh 동시 삭제 (로그아웃 시)

## 🔐 토큰 저장 시점

### 1. Google OAuth 로그인 이후

- `AuthViewModel.loginWithGoogle()`
- 서버 응답에서 받은 Access/Refresh Token을 `TokenDataStore`에 저장

### 2. 회원가입 완료 이후

- `AuthViewModel.signUpWithDia()` → 서버로부터 토큰 수신
- 저장 위치 동일 (DataStore)

## 🔄 토큰 재발급

### 동작 조건

- API 호출 시 `401 Unauthorized` 응답이 발생했을 때

### 처리 흐름

1. `Retrofit` 공통 인터셉터에서 Access Token 자동 첨부
2. 401 감지 시 → Refresh Token으로 `POST /auth/reissue` 요청
3. 새 Access/Refresh Token 수신 → 다시 DataStore에 저장
4. 원래 요청 재시도

> 💡 이 로직은 전역 인터셉터 또는 인증 전용 API 핸들러 클래스에 의해 처리됩니다.

## 🚪 로그아웃 처리

### 수행 위치

- `MyPage` 혹은 설정 화면

### 처리 내용

- `TokenDataStore.clearAccessToken(context)` 호출 → 두 토큰 모두 삭제
- 이후 로그인 화면 또는 스플래시로 이동 유도

## 🧪 테스트

### 토큰 초기화 (개발/디버깅용)

```kotlin
CoroutineScope(Dispatchers.IO).launch {
    TokenDataStore.clearAccessToken(context)
}
```

## ⚙️ 연관 구성 요소

| 클래스                | 역할                                              |
| --------------------- | ------------------------------------------------- |
| `AuthViewModel.kt`    | 로그인 / 회원가입 후 토큰 저장 처리               |
| `TokenDataStore.kt`   | DataStore 기반 토큰 CRUD 담당                     |
| `RetrofitInstance.kt` | 공통 인터셉터를 통해 토큰 첨부 / 갱신 처리        |
| `MainActivity.kt`     | 앱 실행 시 토큰 존재 여부 확인 / 자동 로그인 가능 |

## 📌 요약

- Access/Refresh Token은 모두 DataStore에 저장됩니다.
- API 호출 시 자동 첨부 → 만료 시 자동 재발급 → 재시도 처리
- 로그아웃 시 토큰은 완전 삭제됩니다.

> 👉 토큰 저장 및 재발급은 앱 전역에서 안전하게 작동하도록 설계되어 있습니다.
