package com.example.adoption_Manopata.repository;

import com.example.adoption_Manopata.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository <User, UUID> {

    Optional<User> findByNickname(String nickname);
}
