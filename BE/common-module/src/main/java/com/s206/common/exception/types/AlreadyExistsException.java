package com.s206.common.exception.types;

import com.s206.common.exception.CustomException;
import org.springframework.http.HttpStatus;

public class AlreadyExistsException extends CustomException {
    public AlreadyExistsException(String message) {
        super(message, HttpStatus.CONFLICT);
    }
}