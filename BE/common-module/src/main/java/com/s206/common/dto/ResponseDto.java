package com.s206.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class ResponseDto<T> {
    private final LocalDateTime timestamp;

    private final HttpStatus status;

    private final String message;

    private final T data;

    public static <T> ResponseDto<T> success(HttpStatus status, String message) {
        return ResponseDto.<T>builder()
                .timestamp(LocalDateTime.now())
                .status(status)
                .message(message)
                .data(null)
                .build();
    }
    public static <T> ResponseDto<T> success(HttpStatus status, String message, T data) {
        return ResponseDto.<T>builder()
                .timestamp(LocalDateTime.now())
                .status(status)
                .message(message)
                .data(data)
                .build();
    }
    public static <T> ResponseDto<T> error(HttpStatus status, String message) {
        return ResponseDto.<T>builder()
                .timestamp(LocalDateTime.now())
                .status(status)
                .message(message)
                .build();
    }

}
