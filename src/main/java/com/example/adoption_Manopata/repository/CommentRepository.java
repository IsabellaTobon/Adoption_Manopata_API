package com.example.adoption_Manopata.repository;

import com.example.adoption_Manopata.model.Comment;
import com.example.adoption_Manopata.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    // Método para verificar si el usuario ya ha hecho un comentario
    boolean existsByUser(User user);
}
