package com.sayan.auth.myauthappbeckend.services.impl;

import com.sayan.auth.myauthappbeckend.dtos.UserDTO;
import com.sayan.auth.myauthappbeckend.services.AuthService;
import com.sayan.auth.myauthappbeckend.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserService userService;


    @Override
    public UserDTO registerUser(UserDTO userDTO) {
        // Register Verification
        UserDTO userDto = userService.createUser(userDTO);
        return userDto;
    }
}
