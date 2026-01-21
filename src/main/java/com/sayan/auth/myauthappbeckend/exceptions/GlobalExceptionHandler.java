package com.sayan.auth.myauthappbeckend.exceptions;

import com.sayan.auth.myauthappbeckend.dtos.ApiError;
import com.sayan.auth.myauthappbeckend.dtos.ErrorResponse;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.security.sasl.AuthenticationException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    public Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFoundException(UserNotFoundException exception){
        ErrorResponse res = new ErrorResponse(exception.getMessage(),HttpStatus.NOT_FOUND);
        return ResponseEntity.status(404).body(res);
    }

    @ExceptionHandler(IllegalAccessException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException exception){
        ErrorResponse res = new ErrorResponse(exception.getMessage(),HttpStatus.NOT_FOUND);
        return ResponseEntity.status(404).body(res);
    }

    @ExceptionHandler({
            BadCredentialsException.class,
            CredentialsExpiredException.class,
            DisabledException.class
    })
    public ResponseEntity<ApiError> handleAuthException(Exception e , HttpServletRequest request){
        logger.info("Exception : {}",e.getClass().getName());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiError.of(400,"Bad Request",e.getMessage(),request.getRequestURI()));
    }
}
