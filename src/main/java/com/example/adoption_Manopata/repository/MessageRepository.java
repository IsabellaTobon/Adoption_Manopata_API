package com.example.adoption_Manopata.repository;

import com.example.adoption_Manopata.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MessageRepository extends JpaRepository <Message, UUID> {

    List<Message> findByReceiverId(UUID receiverId);

    List<Message> findBySenderId(UUID senderId);
}
