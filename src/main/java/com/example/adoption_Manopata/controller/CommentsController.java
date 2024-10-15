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
import java.sql.Timestamp;
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
        // VERIFY THAT THE USER IS AUTHENTICATED
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Usuario no autenticado"));
        }

        // GET THE NICKNAME OF THE AUTHENTICATED USER FROM THE JWT
        String nickname = principal.getName();
        Optional<User> userOpt = userService.findByNickname(nickname);

        if (!userOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Usuario no encontrado"));
        }

        User user = userOpt.get();

        // CHECK IF THE USER HAS ALREADY MADE A COMMENT
        if (commentService.existsByUser(user)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "El usuario ya ha hecho un comentario"));
        }

        // ASSIGN THE NAME AND USER TO THE COMMENT
        comment.setName(user.getNickname());
        comment.setUser(user);
        comment.setCommentDate(new Timestamp(System.currentTimeMillis()));

        // SAVE COMMENT
        Comment savedComment = commentService.saveComment(comment);

        // RETURN A JSON RESPONSE WITH THE MESSAGE AND THE SAVED COMMENT
        return ResponseEntity.ok(Map.of("message", "Comentario creado exitosamente", "comment", savedComment));
    }

    @GetMapping
    public List<Comment> getComments() {
        List<Comment> comments = commentService.getAllComments();

        // MAKE SURE EACH USER HAS A DEFAULT IMAGE IF THE PHOTO FIELD IS NULL
        comments.forEach(comment -> {
            User user = comment.getUser();
            if (user.getPhoto() == null || user.getPhoto().isEmpty()) {
                user.setPhoto("/images/default-image.webp");
            }
        });

        return comments;
    }
}
