package com.s206.common.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.stereotype.Component;

/**
 * 데이터 암호화 및 복호화를 위한 유틸리티 클래스입니다.
 * <p>
 * 이 클래스는 Spring Security의 TextEncryptor를 사용하여 AES + CBC 방식으로 민감한 데이터를 암호화하고 복호화합니다.
 * <p>
 * 사용 예시:
 * <pre>
 * {@code
 * String encryptedData = cryptoUtils.encryptData("민감한 정보");
 * String decryptedData = cryptoUtils.decryptData(encryptedData);
 * }
 * </pre>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CryptoUtils {

	// AES + CBC 방식
	private final TextEncryptor textEncryptor;

	/**
	 * 주어진 평문 데이터를 암호화합니다.
	 *
	 * @param plain 암호화할 평문 데이터
	 * @return 암호화된 데이터
	 * @throws RuntimeException 암호화 과정에서 오류가 발생한 경우
	 */
	public String encryptData(String plain) {
		try {
			return textEncryptor.encrypt(plain);
		} catch (Exception e) {
			log.error("Encryption failed: {}", e.getMessage(), e);
			throw new RuntimeException("Encryption process failed", e);
		}
	}

	/**
	 * 암호화된 데이터를 복호화합니다.
	 *
	 * @param encrypted 복호화할 암호화된 데이터
	 * @return 복호화된 평문 데이터
	 * @throws RuntimeException 복호화 과정에서 오류가 발생한 경우
	 */
	public String decryptData(String encrypted) {
		try {
			return textEncryptor.decrypt(encrypted);
		} catch (Exception e) {
			log.error("Decryption failed: {}", e.getMessage(), e);
			throw new RuntimeException("Decryption process failed", e);
		}
	}
}
