package com.example.adoption_Manopata.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

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

    private Integer likes = 0;

    @Column(length = 500)
    private String description;

    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    private User user;
}
