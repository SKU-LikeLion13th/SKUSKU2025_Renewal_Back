package com.sku_sku.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    // Lion
    @ExceptionHandler(InvalidEmailException.class)
    public ResponseEntity<String> invalidEmail(InvalidEmailException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body("그 email 이미 있");
    }

    // Jwt
    @ExceptionHandler(HandleJwtException.class)
    public ResponseEntity<String> handleJwt(HandleJwtException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
    }
}
