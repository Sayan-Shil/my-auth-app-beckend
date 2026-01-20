package com.sayan.auth.myauthappbeckend.utils;

import java.util.UUID;

public class UserHelper {
    public static UUID parseUUID(String userId){
        return UUID.fromString(userId);
    }
}
