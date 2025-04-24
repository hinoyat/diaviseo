package com.s206.common.exception.types;

import com.s206.common.exception.CustomException;
import org.springframework.http.HttpStatus;

public class ConflictException extends CustomException {
    public ConflictException(String message) {
        super(message, HttpStatus.CONFLICT);
    }
}