package com.s206.common.exception.types;

import com.s206.common.exception.CustomException;
import org.springframework.http.HttpStatus;

public class InternalServerErrorException extends CustomException {
  public InternalServerErrorException(String message) {
    super(message, HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
