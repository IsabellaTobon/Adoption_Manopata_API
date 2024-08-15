package com.example.adoption_Manopata.service;

import com.example.adoption_Manopata.model.Message;
import com.example.adoption_Manopata.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    public List<Message> getAllMessages() {
        return messageRepository.findAll();
    }

    public Optional<Message> getMessageById(UUID id) {
        return messageRepository.findById(id);
    }

    public Message createMessage(Message message) {
        return messageRepository.save(message);
    }

    public Message updateMessage(UUID id, Message messageDetails) {
        return messageRepository.findById(id)
                .map(message -> {
                    message.setBodyText(messageDetails.getBodyText());
                    message.setSentDate(messageDetails.getSentDate());
                    message.setSender(messageDetails.getSender());
                    message.setReceiver(messageDetails.getReceiver());
                    message.setPost(messageDetails.getPost());
                    return messageRepository.save(message);
                }).orElseThrow(() -> new RuntimeException("Message not found"));
    }

    public void deleteMessage(UUID id) {
        messageRepository.deleteById(id);
    }

}
