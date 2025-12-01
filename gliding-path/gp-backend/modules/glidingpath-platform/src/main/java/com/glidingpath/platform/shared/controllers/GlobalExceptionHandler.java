package com.glidingpath.platform.shared.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import com.glidingpath.common.util.AppException;
import com.glidingpath.common.util.ErrorCode;
import com.glidingpath.common.util.ErrorResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.time.LocalDateTime;
import com.tryfinch.api.errors.FinchException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AppException.class)
    public ResponseEntity<ErrorResponse> handleAppException(AppException ex, HttpServletRequest request) {
        org.slf4j.LoggerFactory.getLogger(GlobalExceptionHandler.class).error("AppException caught: {}", ex.getMessage(), ex);
        ErrorCode errorCode = ex.getErrorCode();
        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now(),
                errorCode.getCode(),
                request.getRequestURI(),
                ex.getMessage()
        );
        return new ResponseEntity<>(response, mapToHttpStatus(errorCode));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllExceptions(Exception ex, HttpServletRequest request) {
        org.slf4j.LoggerFactory.getLogger(GlobalExceptionHandler.class).error("Exception caught: {}", ex.getMessage(), ex);
        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now(),
                ErrorCode.INTERNAL_ERROR.getCode(),
                request.getRequestURI(),
                ex.getMessage()
        );
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(FinchException.class)
    public ResponseEntity<ErrorResponse> handleFinchException(FinchException ex, HttpServletRequest request) {
        org.slf4j.LoggerFactory.getLogger(GlobalExceptionHandler.class).error("FinchException caught: {}", ex.getMessage(), ex);
        // FinchException is thrown for webhook signature or payload errors
        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now(),
                "FINCH_WEBHOOK_ERROR",
                request.getRequestURI(),
                ex.getMessage()
        );
        // If the error is due to signature, return 401, else 400
        if (ex.getMessage() != null && ex.getMessage().toLowerCase().contains("signature")) {
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        } else {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    private HttpStatus mapToHttpStatus(ErrorCode errorCode) {
        switch (errorCode) {
            case USER_NOT_FOUND:
                return HttpStatus.NOT_FOUND;
            case INVALID_INPUT:
                return HttpStatus.BAD_REQUEST;
            case UNAUTHORIZED:
                return HttpStatus.UNAUTHORIZED;
            case FINCH_AUTH_ERROR:
                return HttpStatus.INTERNAL_SERVER_ERROR;
            case FORBIDDEN:
                return HttpStatus.FORBIDDEN;
            default:
                return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }
}
