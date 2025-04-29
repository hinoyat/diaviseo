package com.s206.common.security;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.crypto.encrypt.TextEncryptor;

class CryptoUtilsTest {

	private CryptoUtils cryptoUtils;
	private TextEncryptor textEncryptor;

	@BeforeEach
	void setUp() {
		textEncryptor = Mockito.mock(TextEncryptor.class);
		cryptoUtils = new CryptoUtils(textEncryptor);
	}

	@Test
	void 암호화_실패한_경우_Encryption_process_failed_메시지를_발생시킨다() {
		String plainText = "test";

		when(textEncryptor.encrypt(plainText)).thenThrow(new RuntimeException("Encryption error"));

		RuntimeException exception = assertThrows(RuntimeException.class, () -> {
			cryptoUtils.encryptData(plainText);
		});

		assertTrue(exception.getMessage().contains("Encryption process failed"));
	}

	@Test
	void 복호화_실패한경우_Decryption_process_failed_메시지를_발생시킨다() {
		String encryptedText = "encryptedTest";

		when(textEncryptor.decrypt(encryptedText)).thenThrow(
				new RuntimeException("Decryption error"));

		RuntimeException exception = assertThrows(RuntimeException.class, () -> {
			cryptoUtils.decryptData(encryptedText);
		});

		assertTrue(exception.getMessage().contains("Decryption process failed"));
	}
}