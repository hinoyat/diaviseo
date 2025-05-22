# 📄 CryptoUtils 공통 암호화 모듈 적용 가이드

## 1. 개요

Spring Boot 애플리케이션 내에서 민감한 정보를 안전하게 보호하기 위해  
CryptoUtils 클래스를 사용하여 **AES 기반 암호화/복호화**를 수행합니다.

---

## 2. 적용 방법

### 2.1. DB 저장 시 적용

---

### 2.2. API 요청/응답 적용

- **API 요청 수신 시** 복호화 처리

```java

@PostMapping("/submit")
public ResponseEntity<Void> submit(@RequestBody RequestDto requestDto) {
	String sensitiveData = cryptoUtils.decryptData(requestDto.getEncryptedData());
	// 비즈니스 로직 처리
	return ResponseEntity.ok().build();
}
```

- **API 응답 전송 시** 암호화 처리

```java

@GetMapping("/fetch")
public ResponseEntity<ResponseDto> fetch() {
	String sensitiveData = "some sensitive data";
	String encryptedData = cryptoUtils.encryptData(sensitiveData);
	return ResponseEntity.ok(new ResponseDto(encryptedData));
}
```

---

## 3. 주의사항

| 구분              | 내용                                                                           |
|:----------------|:-----------------------------------------------------------------------------|
| 키 초기화           | 서비스 기동 시 CryptoUtils 내부 TextEncryptor가 정상 주입되어야 합니다.                         |
| 예외 처리           | 암호화/복호화 실패 시 RuntimeException 발생, 반드시 try-catch 또는 ControllerAdvice로 핸들링하세요. |
| 로깅 금지           | 민감 데이터(평문/복호화 결과)는 절대 로그에 출력하지 않습니다.                                         |
| Padding/Mode 설정 | 현재 AES/CBC/PKCS5Padding 사용 하고 있습니다.                                          |

---