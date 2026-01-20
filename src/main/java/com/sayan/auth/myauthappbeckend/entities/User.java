package com.sayan.auth.myauthappbeckend.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;


@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor


@Entity
@Table(name="users")
public class User {

    @Id
    @GeneratedValue(strategy=GenerationType.UUID)
    @Column(name = "user_id")
    private UUID id;
    @Column(name = "user_name", length = 500)
    private String name;
    @Column(name = "user_mail", unique = true,length = 500)
    @Email
    private String email;
    private String password;
    private String image;


    private boolean enabled=true;
    private Instant createdAt = Instant.now();
    private Instant updatedAt = Instant.now();

    @Enumerated(EnumType.STRING)
    private Provider provider=Provider.LOCAL;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> role= new HashSet<>();

    @PrePersist
    protected void onCreated(){
        Instant time = Instant.now();
        if(createdAt==null) createdAt = time;
        updatedAt = time;
    }

    @PreUpdate
    protected void onUpdated(){
      updatedAt = Instant.now();
    }
}
