package com.example.adoption_Manopata.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

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

    @NotNull
    private int rating;

    // Relación con el usuario que hace el comentario
    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)  // Especificar que userId es la clave foránea
    private User user;
}
