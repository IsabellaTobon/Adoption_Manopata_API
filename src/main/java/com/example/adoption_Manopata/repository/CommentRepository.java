package com.example.adoption_Manopata.repository;

import com.example.adoption_Manopata.model.Comment;
import com.example.adoption_Manopata.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    // METHOD TO CHECK IF THE USER HAS ALREADY MADE A COMMENT
    boolean existsByUser(User user);
}
