package com.sayan.auth.myauthappbeckend.controller;

import com.sayan.auth.myauthappbeckend.dtos.LoginRequest;
import com.sayan.auth.myauthappbeckend.dtos.TokenResponse;
import com.sayan.auth.myauthappbeckend.dtos.UserDTO;
import com.sayan.auth.myauthappbeckend.entities.User;
import com.sayan.auth.myauthappbeckend.repositories.UserRepository;
import com.sayan.auth.myauthappbeckend.security.service.JwtService;
import com.sayan.auth.myauthappbeckend.security.service.SecurityService;
import com.sayan.auth.myauthappbeckend.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;
    private final PasswordEncoder passwordEncoder;
    private final SecurityService securityService;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final ModelMapper modelMapper;

    @PostMapping("/register")
    public ResponseEntity<UserDTO> registerUser (@RequestBody UserDTO userDTO){

        userDTO.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        UserDTO user = authService.registerUser(userDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);

    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> loginUser (@RequestBody LoginRequest loginRequest){
        // authenticate user
       Authentication authentication =  securityService.authenticate(loginRequest);
        User user = userRepository.findByEmail(loginRequest.getEmail()).orElseThrow(()->new BadCredentialsException("User not found!"));
        if(!user.isEnabled()){
            throw  new DisabledException("User is disabled ");
        }

        // Jwt Token
        String token = jwtService.generateAccessToken(user);
       TokenResponse tokenResponse = TokenResponse.of(token,"",jwtService.getAccessTtlSeconds(),modelMapper.map(user, UserDTO.class));
       return ResponseEntity.status(HttpStatus.ACCEPTED).body(tokenResponse);
    }
}
