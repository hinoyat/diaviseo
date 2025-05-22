package com.s206.common.exception.types;

import com.s206.common.exception.CustomException;
import org.springframework.http.HttpStatus;

public class TooManyRequestsException extends CustomException {
    public TooManyRequestsException(String message) {
        super(message, HttpStatus.TOO_MANY_REQUESTS);
    }
}