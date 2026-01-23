package com.sayan.auth.myauthappbeckend.controller;

import com.sayan.auth.myauthappbeckend.dtos.LoginRequest;
import com.sayan.auth.myauthappbeckend.dtos.RefreshTokenRequest;
import com.sayan.auth.myauthappbeckend.dtos.TokenResponse;
import com.sayan.auth.myauthappbeckend.dtos.UserDTO;
import com.sayan.auth.myauthappbeckend.entities.RefreshToken;
import com.sayan.auth.myauthappbeckend.entities.User;
import com.sayan.auth.myauthappbeckend.repositories.RefreshTokenRepository;
import com.sayan.auth.myauthappbeckend.repositories.UserRepository;
import com.sayan.auth.myauthappbeckend.security.service.CookieService;
import com.sayan.auth.myauthappbeckend.security.service.JwtService;
import com.sayan.auth.myauthappbeckend.security.service.SecurityService;
import com.sayan.auth.myauthappbeckend.services.AuthService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.UUID;

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
    private final RefreshTokenRepository refreshTokenRepository;
    private final CookieService cookieService;

    @PostMapping("/register")
    public ResponseEntity<UserDTO> registerUser (@RequestBody UserDTO userDTO){

        userDTO.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        UserDTO user = authService.registerUser(userDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);

    }

    // Refresh Token and Access Token Renewal
    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refreshToken(
          @RequestBody(required = false) RefreshTokenRequest body,
          HttpServletRequest request,
          HttpServletResponse response
    ){
        String refreshToken = cookieService.readRefreshTokenFromRequest(body,request).orElseThrow(()->new BadCredentialsException("Invalid Refresh Token"));
        if(!jwtService.isRefreshToken(refreshToken)){
            throw new BadCredentialsException("Invalid Refresh Token Type");
        }

        String jti = jwtService.getJti(refreshToken);
        UUID userId = jwtService.getUserId(refreshToken);
       RefreshToken token = refreshTokenRepository.findByJti(jti).orElseThrow(()->new BadCredentialsException("Refresh Token not determined"));
        if(token.isRevoked()){
            throw new BadCredentialsException("Refresh Token has been revoked !");
        }
        if(token.getExpiredAt().isBefore(Instant.now())){
            throw new BadCredentialsException("Refresh Token has been expired !");
        }

        if(!token.getUser().getId().equals(userId)){
            throw new BadCredentialsException("Refresh Token doesn't belong to this user!");
        }

        // Refresh Token Rotation
        String newJti = UUID.randomUUID().toString();
        token.setReplacedByToken(newJti);

        refreshTokenRepository.save(token);

        User user = token.getUser();
        var newRefreshTokenOb = RefreshToken.builder()
                .createdAt(Instant.now())
                .expiredAt(Instant.now().plusSeconds(jwtService.getRefreshTtlSeconds()))
                .jti(newJti)
                .revoked(false)
                .user(user)
                .build();
        refreshTokenRepository.save(newRefreshTokenOb);

        String newAccessToken = jwtService.generateAccessToken(user);
        String newRefreshToken = jwtService.generateRefreshToken(user,newRefreshTokenOb.getJti());

        cookieService.attachRefreshCookie(response,newRefreshToken, (int) jwtService.getRefreshTtlSeconds());
        cookieService.addNoStoreHeader(response);

        TokenResponse tokenResponse = TokenResponse.of(newAccessToken,newRefreshToken,jwtService.getAccessTtlSeconds(),modelMapper.map(user, UserDTO.class));
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(tokenResponse);
    }


    @PostMapping("/login")
    public ResponseEntity<TokenResponse> loginUser (@RequestBody LoginRequest loginRequest, HttpServletResponse response){
        // authenticate user
       Authentication authentication =  securityService.authenticate(loginRequest);
        User user = userRepository.findByEmail(loginRequest.getEmail()).orElseThrow(()->new BadCredentialsException("User not found!"));
        if(!user.isEnabled()){
            throw  new DisabledException("User is disabled ");
        }

        String jti = UUID.randomUUID().toString();
        var refToken = RefreshToken.builder()
                .jti(jti)
                .user(user)
                .createdAt(Instant.now())
                .expiredAt(Instant.now().plusSeconds(jwtService.getRefreshTtlSeconds()))
                .revoked(false)
                .build();
        // refresh token save information
        refreshTokenRepository.save(refToken);

        // Jwt Access Token
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user,refToken.getJti());

        //Use cookieService to attach refreshToken
        cookieService.attachRefreshCookie(response,refreshToken,Integer.valueOf((int) jwtService.getRefreshTtlSeconds()));
        cookieService.addNoStoreHeader(response);

       TokenResponse tokenResponse = TokenResponse.of(accessToken,refreshToken,jwtService.getAccessTtlSeconds(),modelMapper.map(user, UserDTO.class));
       return ResponseEntity.status(HttpStatus.ACCEPTED).body(tokenResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response){
        cookieService.readRefreshTokenFromRequest(null,request).ifPresent(token->{
            try{
                if(jwtService.isRefreshToken(token)){
                    String jti = jwtService.getJti(token);
                    refreshTokenRepository.findByJti(jti).ifPresent(rt->{
                        rt.setRevoked(true);
                        refreshTokenRepository.save(rt);
                    });
                }
            }catch (JwtException e){

            }
        });

        cookieService.clearRefreshCookie(response);
        cookieService.addNoStoreHeader(response);
        SecurityContextHolder.clearContext();
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


}
