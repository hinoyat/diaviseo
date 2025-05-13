package com.s206.user.user.dto.request;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class BodyCompositionRequest {

	private Integer userId;
	private BigDecimal height;

	private BigDecimal weight;
}
