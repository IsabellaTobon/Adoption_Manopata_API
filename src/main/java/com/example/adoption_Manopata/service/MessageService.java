package com.example.adoption_Manopata.service;

import com.example.adoption_Manopata.model.Message;
import com.example.adoption_Manopata.model.Post;
import com.example.adoption_Manopata.model.User;
import com.example.adoption_Manopata.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    // METHOD FOR SENDING A MESSAGE
    public Message sendMessage(User sender, User receiver, String bodyText, Post post) {
        Message message = new Message();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setBodyText(bodyText);
        message.setSentDate(new Timestamp(System.currentTimeMillis()));
        message.setPost(post);
        return messageRepository.save(message);
    }

    // OBTAIN ALL MESSAGES RECEIVED
    public List<Message> getInboxMessages(Long userId) {
        return messageRepository.findByReceiverId(userId);
    }

    // OBTAIN ALL MESSAGES SENT
    public List<Message> getSentMessages(Long userId) {
        return messageRepository.findBySenderId(userId);
    }

    // GET MESSAGE HISTORY BETWEEN TWO USERS
    public List<Message> getChatHistory(Long userId1, Long userId2) {
        return messageRepository.findChatHistoryBetweenUsers(userId1, userId2);
    }
}
