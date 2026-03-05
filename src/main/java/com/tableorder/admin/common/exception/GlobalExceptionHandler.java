package com.tableorder.admin.common.exception;

import com.tableorder.admin.common.dto.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException e) {
        ErrorCode code = e.getErrorCode();
        ErrorResponse response = ErrorResponse.builder()
                .status(code.getHttpStatus().value())
                .code(code.name())
                .message(code.getMessage())
                .timestamp(LocalDateTime.now())
                .details(e.getDetails())
                .build();
        return ResponseEntity.status(code.getHttpStatus()).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(f -> f.getField() + ": " + f.getDefaultMessage())
                .reduce((a, b) -> a + ", " + b)
                .orElse("입력값이 올바르지 않습니다");

        ErrorResponse response = ErrorResponse.builder()
                .status(400)
                .code(ErrorCode.INVALID_INPUT.name())
                .message(message)
                .timestamp(LocalDateTime.now())
                .details(Map.of())
                .build();
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        ErrorResponse response = ErrorResponse.builder()
                .status(500)
                .code(ErrorCode.INTERNAL_ERROR.name())
                .message("서버 내부 오류가 발생했습니다")
                .timestamp(LocalDateTime.now())
                .details(Map.of())
                .build();
        return ResponseEntity.internalServerError().body(response);
    }
}
