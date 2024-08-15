package com.example.adoption_Manopata.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Post {
    @Id
    @GeneratedValue
    private UUID id;

    private String photo;
    private Date registerDate;
    private String name;
    private Integer age;
    private String animalType;
    private Boolean vaccinated;
    private String breed;
    private Boolean ppp;
    private String city;
    private String province;
    private Boolean available;
    private Integer likes = 0;
    private String description;

    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;
}
