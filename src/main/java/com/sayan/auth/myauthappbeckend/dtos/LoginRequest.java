package com.sayan.auth.myauthappbeckend.dtos;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequest {
    String email;
    String password;
}
