package com.s206.health.persistence.converter;

import com.s206.common.security.CryptoUtils;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Converter
@Component
@RequiredArgsConstructor
public class StringEncryptConverter implements AttributeConverter<String, String> {
	private final CryptoUtils cryptoUtils;

	@Override
	public String convertToDatabaseColumn(String text) {
		if(Objects.isNull(text)){
			return null;
		}
		return cryptoUtils.encryptData(text);
	}

	@Override
	public String convertToEntityAttribute(String encrypted) {
		if(Objects.isNull(encrypted)){
			return null;
		}
		return cryptoUtils.decryptData(encrypted);
	}
}
