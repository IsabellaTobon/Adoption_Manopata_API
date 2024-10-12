package com.example.adoption_Manopata.model;

import jakarta.persistence.*;
import lombok.Data;

import jakarta.validation.constraints.NotNull;
import java.sql.Timestamp;

@Entity
@Data
@Table(name = "comments", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"userId"})  // Restringir un comentario por usuario
})
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String name;

    @NotNull
    private String text;

    @Column(nullable = false)
    private Timestamp commentDate;

    @NotNull
    private int rating;

    // Relación con el usuario que hace el comentario
    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)  // Especificar que userId es la clave foránea
    private User user;
}
