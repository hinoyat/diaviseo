package com.s206.common.exception.types;

import com.s206.common.exception.CustomException;
import org.springframework.http.HttpStatus;

public class AiCallFailedException extends CustomException {
    public AiCallFailedException(String message) {
        super(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}