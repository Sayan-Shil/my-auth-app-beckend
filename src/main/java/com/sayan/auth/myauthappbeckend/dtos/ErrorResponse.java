package com.sayan.auth.myauthappbeckend.dtos;

import org.springframework.http.HttpStatus;

public record ErrorResponse(
        String message,
        HttpStatus status
) {

}
