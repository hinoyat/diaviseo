package com.s206.common.exception.types;

import com.s206.common.exception.CustomException;
import org.springframework.http.HttpStatus;

public class UnsupportedMediaTypeException extends CustomException {
    public UnsupportedMediaTypeException(String message) {
        super(message, HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }
}