package com.example.adoption_Manopata.service;

import com.example.adoption_Manopata.model.Message;
import com.example.adoption_Manopata.model.Post;
import com.example.adoption_Manopata.model.User;
import com.example.adoption_Manopata.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    // Method for sending a message
    public Message sendMessage(User sender, User receiver, String bodyText, Post post) {
        Message message = new Message();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setBodyText(bodyText);
        message.setSentDate(new Timestamp(System.currentTimeMillis()));
        message.setPost(post);
        return messageRepository.save(message);
    }

    // Obtain all messages received
    public List<Message> getInboxMessages(UUID userId) {
        return messageRepository.findByReceiverId(userId);
    }

    // Obtain all messages sent
    public List<Message> getSentMessages(UUID userId) {
        return messageRepository.findBySenderId(userId);
    }

}
