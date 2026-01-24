package com.sayan.auth.myauthappbeckend.security;

import com.sayan.auth.myauthappbeckend.entities.Provider;
import com.sayan.auth.myauthappbeckend.entities.RefreshToken;
import com.sayan.auth.myauthappbeckend.entities.User;
import com.sayan.auth.myauthappbeckend.repositories.RefreshTokenRepository;
import com.sayan.auth.myauthappbeckend.repositories.UserRepository;
import com.sayan.auth.myauthappbeckend.security.service.CookieService;
import com.sayan.auth.myauthappbeckend.security.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final CookieService cookieService;

    private static Logger logger = LoggerFactory.getLogger(OAuth2SuccessHandler.class);
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        // username
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        // identify User
        String registrationId = "unknown";
        if(authentication instanceof OAuth2AuthenticationToken token){
            registrationId = token.getAuthorizedClientRegistrationId();
        }


        User user;
        switch (registrationId){
            case "google" -> {
                String googleId = oAuth2User.getAttributes().getOrDefault("sub", "").toString();

                String googleEmail = oAuth2User.getAttributes().getOrDefault("email", "").toString();
                String googleName = oAuth2User.getAttributes().getOrDefault("name", "").toString();
                String googleImage = oAuth2User.getAttributes().getOrDefault("picture", "").toString();
                user = User.builder()
                        .email(googleEmail)
                        .name(googleName)
                        .provider(Provider.GOOGLE)
                        .build();

                logger.info("Name : {}", googleName);
                logger.info("Email : {}", googleEmail);
                logger.info("ID : {}", googleId);
                logger.info("Image : {}", googleImage);


                userRepository.findByEmail(googleEmail).ifPresentOrElse(u1 -> {
                }, () -> userRepository.save(user));
            }
                default -> {
                    throw new RuntimeException("Invalid Registration ID");
                }
        }

        // user gmail
        //new user create
        // jwt token

        String jti = UUID.randomUUID().toString();
        RefreshToken newRef = RefreshToken.builder()
                .user(user)
                .jti(jti)
                .revoked(false)
                .createdAt(Instant.now())
                .expiredAt(Instant.now().plusSeconds(jwtService.getRefreshTtlSeconds()))
                .build();

        refreshTokenRepository.save(newRef);
        String refreshToken = jwtService.generateRefreshToken(user, newRef.getJti());
        String accessToken = jwtService.generateAccessToken(user);
        cookieService.attachRefreshCookie(response,refreshToken,(int)jwtService.getRefreshTtlSeconds());



        response.getWriter().write("Login Successful");
    }
}
