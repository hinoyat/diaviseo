package com.s206.common.exception;

import com.s206.common.dto.ResponseDto;
import com.s206.common.exception.types.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // =========================================================================
    // 400 클라이언트 오류 - 잘못된 요청 관련 예외
    // =========================================================================

    // 400 Bad Request - 일반적인 잘못된 요청 처리
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ResponseDto<Void>> handleBadRequest(BadRequestException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ResponseDto.error(HttpStatus.BAD_REQUEST.value(), ex.getMessage()));
    }

    // 400 Bad Request - 유효성 검증 실패
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ResponseDto<Void>> handleValidation(ValidationException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ResponseDto.error(HttpStatus.BAD_REQUEST.value(), ex.getMessage()));
    }

    // 400 Bad Request - 요청 인자 유효성 검증 실패
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseDto<Void>> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .findFirst()
                .orElse("잘못된 요청입니다.");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ResponseDto.error(HttpStatus.BAD_REQUEST.value(), errorMessage));
    }

    // =========================================================================
    // 401, 403, 404 인증 및 권한, 리소스 관련 예외
    // =========================================================================

    // 401 Unauthorized - 인증 실패
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ResponseDto<Void>> handleUnauthorized(UnauthorizedException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ResponseDto.error(HttpStatus.UNAUTHORIZED.value(), ex.getMessage()));
    }

    // 404 Not Found - 리소스를 찾을 수 없음
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ResponseDto<Void>> handleNotFound(NotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ResponseDto.error(HttpStatus.NOT_FOUND.value(), ex.getMessage()));
    }

    // =========================================================================
    // 409, 415, 429 기타 클라이언트 오류
    // =========================================================================

    // 409 Conflict - 리소스 중복 충돌
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ResponseDto<Void>> handleConflict(ConflictException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ResponseDto.error(HttpStatus.CONFLICT.value(), ex.getMessage()));
    }

    // 409 Conflict - 이미 존재하는 리소스
    @ExceptionHandler(AlreadyExistsException.class)
    public ResponseEntity<ResponseDto<Void>> handleAlreadyExists(AlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ResponseDto.error(HttpStatus.CONFLICT.value(), ex.getMessage()));
    }

    // 415 Unsupported Media Type - 지원하지 않는 미디어 타입
    @ExceptionHandler(UnsupportedMediaTypeException.class)
    public ResponseEntity<ResponseDto<Void>> handleUnsupportedMediaType(UnsupportedMediaTypeException ex) {
        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                .body(ResponseDto.error(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value(), ex.getMessage()));
    }

    // 429 Too Many Requests - 요청 횟수 제한 초과
    @ExceptionHandler(TooManyRequestsException.class)
    public ResponseEntity<ResponseDto<Void>> handleTooManyRequests(TooManyRequestsException ex) {
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .body(ResponseDto.error(HttpStatus.TOO_MANY_REQUESTS.value(), ex.getMessage()));
    }

    // =========================================================================
    // 500, 502 서버 오류
    // =========================================================================

    // 500 Internal Server Error - AI 호출 실패
    @ExceptionHandler(AiCallFailedException.class)
    public ResponseEntity<ResponseDto<Void>> handleAiCallFailedException(AiCallFailedException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResponseDto.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage()));
    }

    // 502 Bad Gateway - WebClient 통신 오류
    @ExceptionHandler(WebClientCommunicationException.class)
    public ResponseEntity<ResponseDto<Void>> handleWebClientCommunication(WebClientCommunicationException ex) {
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body(ResponseDto.error(HttpStatus.BAD_GATEWAY.value(), ex.getMessage()));
    }

    // =========================================================================
    // 커스텀 및 기타 예외
    // =========================================================================

    // 커스텀 예외 - 정의된 상태 코드 사용
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ResponseDto<Void>> handleCustomException(CustomException ex) {
        return ResponseEntity.status(ex.getStatusCode())
                .body(ResponseDto.error(ex.getStatusCode(), ex.getMessage()));
    }


    // 500 Internal Server Error - 처리되지 않은 일반 예외 -> 가장 넓은 범위의 예외 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseDto<Void>> handleGenericException(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResponseDto.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "서버 오류가 발생했습니다."));
    }
}