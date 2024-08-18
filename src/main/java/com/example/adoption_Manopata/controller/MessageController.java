package com.example.adoption_Manopata.controller;

import com.example.adoption_Manopata.model.Message;
import com.example.adoption_Manopata.model.Post;
import com.example.adoption_Manopata.model.User;
import com.example.adoption_Manopata.service.MessageService;
import com.example.adoption_Manopata.service.PostService;
import com.example.adoption_Manopata.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
    public ResponseEntity<Message> sendMessage(@RequestParam UUID senderId,
                                               @RequestParam UUID receiverId,
                                               @RequestParam String bodyText,
                                               @RequestParam UUID postId) {
        // Obtener sender, receiver y post a partir de sus IDs
        User sender = userService.getUserById(senderId)
                .orElseThrow(() -> new RuntimeException("Sender not found"));
        User receiver = userService.getUserById(receiverId)
                .orElseThrow(() -> new RuntimeException("Receiver not found"));
        Post post = postService.getPostById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        Message message = messageService.sendMessage(sender, receiver, bodyText, post);
        return ResponseEntity.ok(message);
    }

    @GetMapping("/inbox")
    public ResponseEntity<List<Message>> getInboxMessages(@RequestParam UUID userId) {
        List<Message> messages = messageService.getInboxMessages(userId);
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/sent")
    public ResponseEntity<List<Message>> getSentMessages(@RequestParam UUID userId) {
        List<Message> messages = messageService.getSentMessages(userId);
        return ResponseEntity.ok(messages);
    }
}
