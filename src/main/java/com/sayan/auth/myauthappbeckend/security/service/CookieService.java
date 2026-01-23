package com.sayan.auth.myauthappbeckend.security.service;

import com.sayan.auth.myauthappbeckend.dtos.RefreshTokenRequest;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Optional;

@Service
@Getter
public class CookieService {

    private final String refreshToken;
    private final boolean cookieSecure;
    private final boolean cookieHttpOnly;
    private final String cookieDomain;
    private final String cookieSameSite;
    private final JwtService jwtService;


    public CookieService(@Value("${security.jwt.refresh-token-cookie-name}") String refreshToken,
                         @Value("${security.jwt.cookie-secure}") boolean cookieSecure,
                         @Value("${security.jwt.cookie-http-only}") boolean cookieHttpOnly,
                         @Value("${security.jwt.cookie-domain}") String cookieDomain,
                         @Value("${security.jwt.cookie-same-site}") String cookieSameSite, JwtService jwtService) {
        this.refreshToken = refreshToken;
        this.cookieSecure = cookieSecure;
        this.cookieHttpOnly = cookieHttpOnly;
        this.cookieDomain = cookieDomain;
        this.cookieSameSite = cookieSameSite;
        this.jwtService = jwtService;
    }

    //Create Method to attach cookie to attach
    public void attachRefreshCookie(HttpServletResponse response, String value, int maxAge ){
       ResponseCookie.ResponseCookieBuilder cookieBuilder = ResponseCookie.from(refreshToken,value)
                .httpOnly(cookieHttpOnly)
                .sameSite(cookieSameSite)
                .secure(cookieSecure)
               .maxAge(maxAge);

       if(cookieDomain!=null && !cookieDomain.isBlank()){
           cookieBuilder.domain(cookieDomain);
       }

       ResponseCookie cookie = cookieBuilder.build();
       response.addHeader(HttpHeaders.SET_COOKIE,cookie.toString());

    }

    public void clearRefreshCookie(HttpServletResponse response){
        ResponseCookie.ResponseCookieBuilder cookieBuilder = ResponseCookie.from(refreshToken,"")
                .httpOnly(cookieHttpOnly)
                .sameSite(cookieSameSite)
                .secure(cookieSecure)
                .maxAge(0)
                .path("/");

        if(cookieDomain!=null && !cookieDomain.isBlank()){
            cookieBuilder.domain(cookieDomain);
        }


        ResponseCookie cookie = cookieBuilder.build();
        response.addHeader(HttpHeaders.SET_COOKIE,cookie.toString());

    }

    public void addNoStoreHeader(HttpServletResponse response){
        response.setHeader(HttpHeaders.CACHE_CONTROL,"no-store");
        response.setHeader("pragma","no-cache");
    }

    public Optional<String> readRefreshTokenFromRequest(RefreshTokenRequest body, HttpServletRequest request) {
        // 1. prefer reading refresh token from cookie
        if(request.getCookies()!=null){
            Optional<String> refreshToken =
                    Optional.ofNullable(request.getCookies())
                            .stream()
                            .flatMap(Arrays::stream)
                            .filter(c -> getRefreshToken().equals(c.getName()))
                            .map(Cookie::getValue)
                            .filter(v -> v != null && !v.isBlank())
                            .findFirst();

            if(refreshToken.isPresent()){
                return refreshToken;
            }
        }

        if(body!=null && body.refreshToken()!=null && !body.refreshToken().isBlank()){
            return Optional.of(body.refreshToken());
        }

        // 3. Custom Cookie
        String refreshHeader = request.getHeader("X-Refresh-Token");
        if(refreshHeader!=null && !refreshHeader.isBlank()){
            return Optional.of(refreshHeader.trim());
        }

        // 4. Authorization Bearer Token
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if(authHeader!=null && authHeader.regionMatches(true,0,"Bearer",0,6)){
            String candidate = authHeader.substring(7);
            if(!candidate.isEmpty()){
                try{
                    if(jwtService.isRefreshToken(candidate)){
                        return Optional.of(candidate);
                    }

                } catch (Exception e) {

                }
            }
        }

        return Optional.empty();
    }





}
