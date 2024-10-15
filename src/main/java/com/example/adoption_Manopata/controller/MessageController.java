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
     * SEND A MESSAGE BETWEEN USERS
     *
     * @param payload MESSAGE BODY INCLUDING SENDERID, RECEIVERID, BODYTEXT, AND POSTID
     * @return MESSAGE CREATE
     */
    @PostMapping("/send")
    public ResponseEntity<?> sendMessage(@RequestBody @Valid Map<String, Object> payload) {
        try {
            // EXTRACT SENDERID, RECEIVERID, BODYTEXT, AND POSTID FROM THE JSON
            Long senderId = Long.valueOf(payload.get("senderId").toString());
            Long receiverId = Long.valueOf(payload.get("receiverId").toString());
            String bodyText = payload.get("bodyText").toString();
            Long postId = Long.valueOf(payload.get("postId").toString());

            // GET SENDER, RECEIVER AND POST FROM THEIR IDS
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

            // CREATE THE MESSAGE AND SEND IT
            Message message = messageService.sendMessage(sender, receiver, bodyText, post);
            return ResponseEntity.status(HttpStatus.CREATED).body(message);
        } catch (Exception e) {
            // HANDLING UNEXPECTED ERROR
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error while sending the message: " + e.getMessage());
        }
    }

    /**
     * GET MESSAGES RECEIVED IN THE USER'S INBOX
     *
     * @param userId ID OF THE USER WHO RECEIVES THE MESSAGES
     * @return LIST OF RECEIVED MESSAGES
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
            // HANDLING UNEXPECTED ERROR
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
