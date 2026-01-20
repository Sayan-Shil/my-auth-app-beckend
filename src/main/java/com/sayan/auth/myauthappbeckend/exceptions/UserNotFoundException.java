package com.sayan.auth.myauthappbeckend.exceptions;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(String reason){
        super(reason);
    }

    public UserNotFoundException(){
       super("User Not Found");
    }
}
