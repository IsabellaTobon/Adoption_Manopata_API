package com.example.adoption_Manopata.model;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Data
@Table(name = "messages")
public class Message {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String bodyText;

    @Column(nullable = false)
    private Timestamp sentDate;

    @ManyToOne
    @JoinColumn(name = "senderId", nullable = false)
    private User sender;

    @ManyToOne
    @JoinColumn(name = "receiverId", nullable = false)
    private User receiver;

    @ManyToOne
    @JoinColumn(name = "postId", nullable = false)
    private Post post;

}
