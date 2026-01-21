package com.sayan.auth.myauthappbeckend.dtos;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public record ApiError(
        int status,
        String error,
        String msg,
        String path,
        OffsetDateTime timestamp
) {
    public static ApiError of(int status, String error, String msg , String path) {
        return new ApiError(status,error,msg,path,OffsetDateTime.now(ZoneOffset.UTC));
    }
}
