package com.example.adoption_Manopata.service;

import com.example.adoption_Manopata.model.Comment;
import com.example.adoption_Manopata.model.User;
import com.example.adoption_Manopata.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {
    @Autowired
    private CommentRepository commentRepository;

    public boolean existsByUser(User user) {
        return commentRepository.existsByUser(user);
    }

    // Método para guardar un nuevo comentario
    public Comment saveComment(Comment comment) {
        return commentRepository.save(comment);
    }

    // Método para obtener todos los comentarios
    public List<Comment> getAllComments() {
        return commentRepository.findAll();
    }
}
