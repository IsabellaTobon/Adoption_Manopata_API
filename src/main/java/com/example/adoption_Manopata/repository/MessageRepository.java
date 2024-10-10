package com.example.adoption_Manopata.repository;

import com.example.adoption_Manopata.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageRepository extends JpaRepository <Message, Long> {

    List<Message> findByReceiverId(Long receiverId);

    List<Message> findBySenderId(Long senderId);

    // Consulta personalizada para obtener el historial de mensajes entre dos usuarios
    @Query("SELECT m FROM Message m WHERE (m.sender.id = :userId1 AND m.receiver.id = :userId2) " +
            "OR (m.sender.id = :userId2 AND m.receiver.id = :userId1) " +
            "ORDER BY m.sentDate ASC")
    List<Message> findChatHistoryBetweenUsers(@Param("userId1") Long userId1, @Param("userId2") Long userId2);
}
