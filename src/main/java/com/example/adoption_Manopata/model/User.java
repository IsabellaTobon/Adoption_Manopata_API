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
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue
@Column(columnDefinition = "CHAR(36)")
    private UUID id;

    private String name;
    private String lastname;

    @Column(unique = true, nullable = false)
    private String nickname;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @ManyToOne
    @JoinColumn(name = "role", referencedColumnName = "name")
    private Role role;

    @Column(nullable = false)
    private boolean deleted = false;

    @Column(nullable = false)
    private boolean active = true;

}
