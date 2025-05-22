package com.s206.health.bodyinfo.entity;

import lombok.Getter;

@Getter
public enum InputType {
	OCR("OCR"),
	MANUAL("MANUAL");

	private final String value;

	InputType(String value) {
		this.value = value;
	}

}
