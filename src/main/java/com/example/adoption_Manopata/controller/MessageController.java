package com.example.adoption_Manopata.controller;

import com.example.adoption_Manopata.model.Message;
import com.example.adoption_Manopata.model.Post;
import com.example.adoption_Manopata.model.User;
import com.example.adoption_Manopata.service.MessageService;
import com.example.adoption_Manopata.service.PostService;
import com.example.adoption_Manopata.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private UserService userService;

    @Autowired
    private PostService postService;

    /**
     * Enviar un mensaje entre usuarios
     *
     * @param payload Cuerpo del mensaje que incluye senderId, receiverId, bodyText, y postId
     * @return Message creado
     */
    @PostMapping("/send")
    public ResponseEntity<?> sendMessage(@RequestBody @Valid Map<String, Object> payload) {
        try {
            // Extraer senderId, receiverId, bodyText, y postId desde el JSON
            Long senderId = Long.valueOf(payload.get("senderId").toString());
            Long receiverId = Long.valueOf(payload.get("receiverId").toString());
            String bodyText = payload.get("bodyText").toString();
            Long postId = Long.valueOf(payload.get("postId").toString());

            // Obtener sender, receiver y post a partir de sus IDs
            Optional<User> senderOpt = userService.getUserById(senderId);
            Optional<User> receiverOpt = userService.getUserById(receiverId);
            Optional<Post> postOpt = postService.getPostById(postId);

            if (!senderOpt.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Sender not found");
            }

            if (!receiverOpt.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Receiver not found");
            }

            if (!postOpt.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Post not found");
            }

            User sender = senderOpt.get();
            User receiver = receiverOpt.get();
            Post post = postOpt.get();

            // Crear el mensaje y enviarlo
            Message message = messageService.sendMessage(sender, receiver, bodyText, post);
            return ResponseEntity.status(HttpStatus.CREATED).body(message);
        } catch (Exception e) {
            // Manejar error inesperado
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error while sending the message: " + e.getMessage());
        }
    }

    /**
     * Obtener los mensajes recibidos en la bandeja de entrada del usuario
     *
     * @param userId Id del usuario que recibe los mensajes
     * @return Lista de mensajes recibidos
     */
    @GetMapping("/inbox")
    public ResponseEntity<?> getInboxMessages(@RequestParam Long userId) {
        try {
            Optional<User> userOpt = userService.getUserById(userId);
            if (!userOpt.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }

            List<Message> messages = messageService.getInboxMessages(userId);
            return ResponseEntity.ok(messages);
        } catch (Exception e) {
            // Manejar error inesperado
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error while retrieving inbox messages: " + e.getMessage());
        }
    }

    @GetMapping("/history")
    public ResponseEntity<List<Message>> getChatHistory(@RequestParam Long userId1, @RequestParam Long userId2) {
        try {
            List<Message> messages = messageService.getChatHistory(userId1, userId2);
            return ResponseEntity.ok(messages);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }
}
