package com.sayan.auth.myauthappbeckend.exceptions;

import com.sayan.auth.myauthappbeckend.dtos.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFoundException(UserNotFoundException exception){
        ErrorResponse res = new ErrorResponse(exception.getMessage(),HttpStatus.NOT_FOUND);
        return ResponseEntity.status(404).body(res);
    }
}
