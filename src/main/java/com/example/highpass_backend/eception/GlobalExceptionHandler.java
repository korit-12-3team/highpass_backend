package com.example.highpass_backend.eception;

import com.example.highpass_backend.dto.etc.ErrorResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException exception) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.builder()
                        .status(HttpStatus.BAD_REQUEST.value())
                        .message(exception.getMessage())
                        .build());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolationException(DataIntegrityViolationException exception) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.builder()
                        .status(HttpStatus.BAD_REQUEST.value())
                        .message(resolveDataIntegrityMessage(exception))
                        .build());
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> handleResponseStatusException(ResponseStatusException exception) {
        HttpStatus status = HttpStatus.valueOf(exception.getStatusCode().value());
        return ResponseEntity
                .status(status)
                .body(ErrorResponse.builder()
                        .status(status.value())
                        .message(exception.getReason() == null || exception.getReason().isBlank()
                                ? status.getReasonPhrase()
                                : exception.getReason())
                        .build());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception exception) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.builder()
                        .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .message("서버 오류가 발생했습니다.")
                        .build());
    }

    private String resolveDataIntegrityMessage(DataIntegrityViolationException exception) {
        Throwable rootCause = exception.getMostSpecificCause();
        String rawMessage = rootCause == null ? exception.getMessage() : rootCause.getMessage();
        if (rawMessage == null) {
            return "데이터 저장 중 제약 조건 오류가 발생했습니다.";
        }

        String normalized = rawMessage.toLowerCase();
        if (normalized.contains("nickname")) {
            return "이미 사용 중인 닉네임입니다.";
        }

        return "데이터 저장 중 제약 조건 오류가 발생했습니다.";
    }
}
