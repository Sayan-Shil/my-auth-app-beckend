package com.sayan.auth.myauthappbeckend.services;

import com.sayan.auth.myauthappbeckend.dtos.LoginRequest;
import com.sayan.auth.myauthappbeckend.dtos.UserDTO;

public interface AuthService {
    UserDTO registerUser(UserDTO userDTO);
}
