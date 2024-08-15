package com.example.adoption_Manopata.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue
    private UUID id;

    private String name;
    private String lastname;
    private String nickname;
    private String email;
    private String password;

    @ManyToOne
    @JoinColumn(name = "role")
    private Role role;
}
