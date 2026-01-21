package com.sayan.auth.myauthappbeckend.security.service;

import com.sayan.auth.myauthappbeckend.entities.Role;
import com.sayan.auth.myauthappbeckend.entities.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Getter
@Setter
public class JwtService {

    private final SecretKey key;
    private final long accessTtlSeconds;
    private final long refreshTtlSeconds;
    private final String issuer;

    public JwtService(@Value("${security.jwt.access-ttl-seconds}") long accessTtlSeconds,
                      @Value("${security.jwt.refresh-ttl-seconds}") long refreshTtlSeconds,
                      @Value("${security.jwt.issuer}") String issuer,
                       @Value("${security.jwt.secret}") String key) {

        if(key==null && key.length()<64){
            throw new IllegalArgumentException("Invalid Secret Key");
        }

        this.key = Keys.hmacShaKeyFor(key.getBytes(StandardCharsets.UTF_8));
        this.accessTtlSeconds = accessTtlSeconds;
        this.refreshTtlSeconds = refreshTtlSeconds;
        this.issuer = issuer;
    }



    // Generate Token:
    public String generateAccessToken(User user){
        Instant now = Instant.now();
        List<String> roles = user.getRoles()==null
                ? List.of()
                : user.getRoles().stream().map(Role::getName).toList();


        return Jwts.builder()
                .id(UUID.randomUUID().toString())
                .subject(user.getId().toString())
                .issuer(issuer)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(accessTtlSeconds)))
                .claims(Map.of(
                        "email" , user.getEmail(),
                        "roles" , roles,
                        "typ" , "access"
                ))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }


    // Refresh Token
    public String generateRefreshToken(User user,String jti){
        Instant now = Instant.now();

        return Jwts.builder()
                .id(jti)
                .subject(user.getEmail())
                .issuer(issuer)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(accessTtlSeconds)))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    //Parse The Token
    public Jws<Claims> parse(String token){

            return Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
    }

    // Check if Access Token
    public boolean isAccessToken(String token){
        Claims c = parse(token).getPayload();
        return "access".equals(c.get("typ"));
    }

    // Check if Refresh Token
    public boolean isRefreshToken(String token){
        Claims c = parse(token).getPayload();
        return "refresh".equals(c.get("typ"));
    }

    // Get User Id from Jwt
    public UUID getUserId(String token){
        Claims c = parse(token).getPayload();
        return UUID.fromString(c.getSubject());
    }

    // Get Token Id from Jwt
    public UUID getTokenId(String token){
        Claims c = parse(token).getPayload();
        return UUID.fromString(c.getId());
    }


}
