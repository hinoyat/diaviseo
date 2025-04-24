package com.s206.common.exception.types;

import com.s206.common.exception.CustomException;
import org.springframework.http.HttpStatus;

public class ValidationException extends CustomException {
    public ValidationException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}