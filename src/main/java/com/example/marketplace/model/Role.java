package com.example.marketplace.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)

    private String name;

    private String description;
}