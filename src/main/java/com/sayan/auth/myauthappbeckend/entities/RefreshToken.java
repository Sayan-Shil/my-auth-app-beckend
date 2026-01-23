package com.sayan.auth.myauthappbeckend.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Entity
@Table(name = "refresh_tokens" , indexes = {
        @Index(name = "refresh_tokens_jti_index" ,columnList = "jti" , unique = true),
        @Index(name="refresh_tokens_user_index" , columnList = "user_id")
})
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "jti" , nullable = false , updatable = false , unique = true)
    private String jti;

    @ManyToOne
    @JoinColumn(name = "user_id" , nullable = false, updatable = false)
    private User user;

    @Column(nullable = false)
    private boolean revoked ;
    private String replacedByToken;
    @Column(nullable = false , updatable = false)
    private Instant createdAt = Instant.now();
    @Column(nullable = false)
    private Instant expiredAt = Instant.now();



}
