package com.sayan.auth.myauthappbeckend.dtos;

import com.sayan.auth.myauthappbeckend.entities.Provider;
import com.sayan.auth.myauthappbeckend.entities.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {
    private UUID id;
    private String name;
    private String email;
    private String password;
    private String image;
    private boolean enabled=true;
    private Instant createdAt = Instant.now();
    private Instant updatedAt = Instant.now();
    private Provider provider=Provider.LOCAL;
    private Set<RoleDTO> role = new HashSet<>();
}
