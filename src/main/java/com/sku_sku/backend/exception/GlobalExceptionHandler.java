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

    @ExceptionHandler(EmptyLionException.class)
    public ResponseEntity<String> emptyLion(EmptyLionException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("그 lion 없");
    }

    @ExceptionHandler(InvalidLionException.class)
    public ResponseEntity<String> invalidLion(InvalidLionException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("이런 Lion 없");
    }

    // Jwt
    @ExceptionHandler(HandleJwtException.class)
    public ResponseEntity<String> handleJwt(HandleJwtException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
    }

    @ExceptionHandler(InvalidIdException.class)
    public ResponseEntity<String> invalidId(InvalidIdException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage() + "Id로 조회한 결과 없음");
    }

    // Project
    @ExceptionHandler(InvalidTitleException.class)
    public ResponseEntity<String> invalidTitle(InvalidTitleException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body("그 title 이미 있");
    }

    // LectureFile
    @ExceptionHandler(InvalidLectureFileException.class)
    public ResponseEntity<String> invalidLectureFile(InvalidLectureFileException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("그 강의자료 없");
    }

    @ExceptionHandler(EmptyLectureException.class)
    public ResponseEntity<String> emptyLecture(EmptyLectureException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("아무런 강의자료 없");
    }
}
