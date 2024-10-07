package com.example.adoption_Manopata.controller;

import com.example.adoption_Manopata.model.Message;
import com.example.adoption_Manopata.model.Post;
import com.example.adoption_Manopata.model.User;
import com.example.adoption_Manopata.service.MessageService;
import com.example.adoption_Manopata.service.PostService;
import com.example.adoption_Manopata.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/messages")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private UserService userService;

    @Autowired
    private PostService postService;

    @PostMapping("/send")
    public ResponseEntity<Message> sendMessage(@RequestBody Map<String, Object> payload) {
        // Extraer senderId, receiverId, bodyText, y postId desde el JSON
        Long senderId = Long.valueOf(payload.get("senderId").toString());
        Long receiverId = Long.valueOf(payload.get("receiverId").toString());
        String bodyText = payload.get("bodyText").toString();
        Long postId = Long.valueOf(payload.get("postId").toString());

        // Obtener sender, receiver y post a partir de sus IDs
        User sender = userService.getUserById(senderId)
                .orElseThrow(() -> new RuntimeException("Sender not found"));
        User receiver = userService.getUserById(receiverId)
                .orElseThrow(() -> new RuntimeException("Receiver not found"));
        Post post = postService.getPostById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        // Crear el mensaje y enviarlo
        Message message = messageService.sendMessage(sender, receiver, bodyText, post);
        return ResponseEntity.ok(message);
    }

    @GetMapping("/inbox")
    public ResponseEntity<List<Message>> getInboxMessages(@RequestParam Long userId) {
        try {
            List<Message> messages = messageService.getInboxMessages(userId);
            return ResponseEntity.ok(messages);
        } catch (Exception e) {
            // Manejar el error adecuadamente y devolver un error con más contexto
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

}
