# Common Module 개요

**디아비서(DiaViseo) 프로젝트의 공통 모듈**로, 모든 마이크로서비스에서 공통으로 사용하는 핵심 기능들을 제공합니다.

---

## 📌 역할과 목적

- **일관된 API 응답 형식** 제공으로 프론트엔드와의 통신 표준화
- **전역 예외 처리** 시스템으로 안정적인 오류 관리
- **암호화/복호화** 기능으로 민감한 데이터 보호
- **서비스 간 통신** 설정 표준화 (내부/외부 WebClient)

---

## 🗂️ 주요 구성 요소

### 1. **응답 표준화** (`dto/`)
- **ResponseDto**: 모든 API 응답의 통일된 형식 제공
- 성공/실패 응답 구조 표준화
- 타임스탬프, 상태코드, 메시지, 데이터 포함

### 2. **예외 처리 시스템** (`exception/`)
- **GlobalExceptionHandler**: 전역 예외 처리기
- **CustomException**: 기본 커스텀 예외 클래스
- **Exception Types**: HTTP 상태코드별 예외 클래스들
  - `BadRequestException` (400)
  - `UnauthorizedException` (401) 
  - `NotFoundException` (404)
  - `ConflictException` (409)
  - `InternalServerErrorException` (500)
  - 기타 10여개 예외 타입

### 3. **보안 모듈** (`security/`)
- **CryptoUtils**: AES 기반 암호화/복호화 유틸리티
- **EncryptionConfig**: 암호화 설정 (TextEncryptor Bean)
- Spring Security Crypto 기반 구현

### 4. **통신 설정** (`webclient/`)
- **WebClientConfig**: 내부/외부 서비스 통신 설정
- LoadBalanced WebClient (내부 서비스용)
- External WebClient (외부 API용)

---

## ⚙️ 기술 스택

| 구분 | 기술 | 버전 | 용도 |
|-----|-----|-----|-----|
| **Framework** | Spring Boot | 3.4.5 | 기본 프레임워크 |
| **Cloud** | Spring Cloud | 2024.0.1 | 마이크로서비스 지원 |
| **보안** | Spring Security Crypto | - | 데이터 암호화 |
| **통신** | WebFlux | - | 비동기 HTTP 클라이언트 |
| **메시징** | RabbitMQ | - | 서비스 간 메시지 큐 |
| **테스트** | JUnit 5 + Mockito | 5.10.0 | 단위 테스트 |

---

## 📦 빌드 설정

```gradle
// 실행 가능한 fat jar 비활성화 (라이브러리 모듈)
bootJar { enabled = false }

// 일반 jar 활성화 (다른 서비스에서 의존성으로 사용)
jar { enabled = true }
```

**주요 특징:**
- `java-library` 플러그인 사용으로 라이브러리 모듈로 설정
- 다른 마이크로서비스에서 의존성으로 추가하여 사용
- 공통 기능의 중복 구현 방지

---

## 🔧 사용 방법

### 1. 의존성 추가
다른 서비스의 `build.gradle`에 추가:
```gradle
dependencies {
    implementation project(':common-module')
}
```

### 2. ResponseDto 사용
```java
// 성공 응답
return ResponseEntity.ok(
    ResponseDto.success(HttpStatus.OK, "조회 성공", data)
);

// 에러 응답  
return ResponseEntity.badRequest(
    ResponseDto.error(HttpStatus.BAD_REQUEST, "잘못된 요청")
);
```

### 3. 예외 처리
```java
// 커스텀 예외 발생
throw new NotFoundException("사용자를 찾을 수 없습니다.");
throw new BadRequestException("필수 파라미터가 누락되었습니다.");
```

### 4. 암호화 사용
```java
@Autowired
private CryptoUtils cryptoUtils;

// 암호화
String encrypted = cryptoUtils.encryptData("민감한 데이터");

// 복호화  
String decrypted = cryptoUtils.decryptData(encrypted);
```

---

## 🛡️ 보안 고려사항

- **AES + CBC 방식** 암호화 적용
- **환경변수**로 암호화 키/솔트 관리
- **로깅 금지**: 민감 데이터는 절대 로그에 출력하지 않음
- **예외 처리**: 암호화/복호화 실패 시 안전한 오류 처리

---

## 📋 환경 설정

```yaml
# application.yml 예시
security:
  encryption:
    password: ${ENCRYPTION_PASSWORD}
    salt: ${ENCRYPTION_SALT}
```

**필수 환경변수:**
- `ENCRYPTION_PASSWORD`: 암호화 비밀번호
- `ENCRYPTION_SALT`: 암호화 솔트값

---

## 🔄 확장성

현재 구조로 향후 추가 가능한 기능들:
- **캐싱** 관련 공통 설정
- **로깅** 표준화 모듈  
- **메트릭 수집** 공통 기능
- **이벤트 발행/구독** 표준화