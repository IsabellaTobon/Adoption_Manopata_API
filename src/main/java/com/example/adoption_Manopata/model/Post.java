package com.example.adoption_Manopata.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "posts")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String photo;

    @Temporal(TemporalType.TIMESTAMP)
    private Date registerDate;

    @Column(nullable = false)
    private String name;

    private Integer age;

    @Column(nullable = false)
    private String animalType;

    private Boolean vaccinated;

    private String breed;

    private Boolean ppp;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String province;

    private Boolean available;

    @ManyToMany
    @JoinTable(
            name = "posts_likedByUsers",  // Nombre de la tabla de unión
            joinColumns = @JoinColumn(name = "post_id"),  // Columna que hace referencia a Post
            inverseJoinColumns = @JoinColumn(name = "user_id")  // Columna que hace referencia a User
    )
    private Set<User> likedByUsers = new HashSet<>();

    @Column(length = 500)
    private String description;

    @Column(nullable = false)
    private int likes = 0;

    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    private User user;
}
