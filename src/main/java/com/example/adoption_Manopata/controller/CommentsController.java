package com.example.adoption_Manopata.controller;

import com.example.adoption_Manopata.model.Comment;
import com.example.adoption_Manopata.model.User;
import com.example.adoption_Manopata.service.CommentService;
import com.example.adoption_Manopata.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/comments")
public class CommentsController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private UserService userService;

    @PostMapping("/create")
    public ResponseEntity<?> createComment(@RequestBody Comment comment, Principal principal) {
        // Verificar que el usuario está autenticado
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Usuario no autenticado"));
        }

        // Obtener el nickname del usuario autenticado desde el JWT
        String nickname = principal.getName();
        Optional<User> userOpt = userService.findByNickname(nickname);

        if (!userOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Usuario no encontrado"));
        }

        User user = userOpt.get();

        // Verificar si el usuario ya ha hecho un comentario
        if (commentService.existsByUser(user)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "El usuario ya ha hecho un comentario"));
        }

        // Asignar el nombre y el usuario al comentario
        comment.setName(user.getNickname());
        comment.setUser(user);

        // Guardar el comentario
        Comment savedComment = commentService.saveComment(comment);

        // Devolver una respuesta JSON con el mensaje y el comentario guardado
        return ResponseEntity.ok(Map.of("message", "Comentario creado exitosamente", "comment", savedComment));
    }

    @GetMapping
    public List<Comment> getComments() {
        return commentService.getAllComments();
    }
}
