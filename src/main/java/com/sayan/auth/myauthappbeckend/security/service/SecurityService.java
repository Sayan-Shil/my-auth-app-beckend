package com.sayan.auth.myauthappbeckend.security.service;

import com.sayan.auth.myauthappbeckend.dtos.LoginRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SecurityService {


    private final AuthenticationManager authenticationManager;

    public Authentication authenticate(LoginRequest loginRequest){
            return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(),loginRequest.getPassword()));
    }
}
