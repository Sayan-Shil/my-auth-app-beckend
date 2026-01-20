package com.sayan.auth.myauthappbeckend.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder


@Entity
public class Role {
    @Id
    @Column(name = "role_id")
    private UUID id = UUID.randomUUID();
    @Column(name = "role_name",unique = true,nullable = false)
    private String name;

}
