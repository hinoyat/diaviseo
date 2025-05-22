package com.s206.common.exception.types;

import com.s206.common.exception.CustomException;
import org.springframework.http.HttpStatus;

public class WebClientCommunicationException extends CustomException {
    public WebClientCommunicationException(String message) { super(message, HttpStatus.BAD_GATEWAY); };
}
