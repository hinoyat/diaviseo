package com.s206.health.persistence.converter;

import com.s206.common.security.CryptoUtils;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.math.BigDecimal;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@Converter
@RequiredArgsConstructor
public class DecimalEncryptConverter implements AttributeConverter<BigDecimal, String> {
	private final CryptoUtils cryptoUtils;
	@Override
	public String convertToDatabaseColumn(BigDecimal bigDecimal) {
		if(Objects.isNull(bigDecimal)){
			return null;
		}
		return cryptoUtils.encryptData(bigDecimal.toPlainString());
	}

	@Override
	public BigDecimal convertToEntityAttribute(String encrypted) {
		if(Objects.isNull(encrypted)){
			return null;
		}
		return new BigDecimal(cryptoUtils.decryptData(encrypted));
	}
}
