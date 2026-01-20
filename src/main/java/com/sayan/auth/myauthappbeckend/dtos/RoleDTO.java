package com.sayan.auth.myauthappbeckend.dtos;

import jakarta.persistence.Column;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleDTO {
    private UUID id ;
    private String name;
}
