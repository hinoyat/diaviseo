package com.s206.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CustomException extends RuntimeException {
    private final HttpStatus statusCode;

    public CustomException(String message, HttpStatus status) {
        super(message);
        this.statusCode = status;
    }


}