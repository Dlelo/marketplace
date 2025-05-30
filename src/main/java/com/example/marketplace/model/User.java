package com.example.marketplace.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String email;
    private String password;

   @ManyToMany(fetch = FetchType.EAGER)
   @JoinTable(
           name="user_roles",
           joinColumns = @JoinColumn(name="user_id"),
           inverseJoinColumns = @JoinColumn(name="role_id")
   )
   @Builder.Default
    private Set<Role> roles = new HashSet<>();
}
