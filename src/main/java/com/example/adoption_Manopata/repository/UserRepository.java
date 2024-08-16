package com.example.adoption_Manopata.repository;

import com.example.adoption_Manopata.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository <User, UUID> {

    Optional<User> findByNickname(String nickname);
    Optional<User> findByEmail(String email);
    boolean existsByNickname(String nickname);
    boolean existsByEmail(String email);

    Optional<User> findByNicknameAndDeletedFalse(String nickname);

    Optional<User> findByEmailAndDeletedFalse(String email);

    boolean existsByNicknameAndDeletedFalse(String nickname);

    boolean existsByEmailAndDeletedFalse(String email);

}
