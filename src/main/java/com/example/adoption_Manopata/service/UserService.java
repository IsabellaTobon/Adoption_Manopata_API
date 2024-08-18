package com.example.adoption_Manopata.service;

import com.example.adoption_Manopata.model.User;
import com.example.adoption_Manopata.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(UUID id) {
        return userRepository.findById(id);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmailAndDeletedFalse(email);
    }

    public Optional<User> findByNickname(String nickname) {
        return userRepository.findByNicknameAndDeletedFalse(nickname);
    }

    public User createUser(User user) {
        return userRepository.save(user);
    }

    public boolean existsByNickname(String nickname) {
        return userRepository.existsByNicknameAndDeletedFalse(nickname);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmailAndDeletedFalse(email);
    }

    public void save(User user) {
        userRepository.save(user);
    }

    public User updateUser(UUID id, User userDetails) {
        return userRepository.findById(id)
                .map(user -> {
                    user.setName(userDetails.getName());
                    user.setLastname(userDetails.getLastname());
                    user.setNickname(userDetails.getNickname());
                    user.setEmail(userDetails.getEmail());
                    user.setPassword(userDetails.getPassword());
                    user.setRole(userDetails.getRole());
                    return userRepository.save(user);
                }).orElseThrow(() -> new RuntimeException("User not found"));
    }

    public boolean changePassword(UUID userId, String oldPassword, String newPassword) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            // Verify if the old password matches whith the one stored
            if (passwordEncoder.matches(oldPassword, user.getPassword())) {
                // If the old password matches, encode the new password and save it
                user.setPassword(passwordEncoder.encode(newPassword));
                userRepository.save(user);
                return true;
            } else {
                throw new RuntimeException("La contraseña antigua es incorrecta.");
            }
        }
        return false;
    }

    public boolean deleteUser(UUID userId, String password) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            // Verify the password
            if (passwordEncoder.matches(password, user.getPassword())) {
                user.setDeleted(true);  // Mark the user as deleted
                userRepository.save(user);
                return true;
            } else {
                throw new RuntimeException("Contraseña incorrecta. No se puede eliminar la cuenta.");
            }
        } else {
            throw new RuntimeException("Usuario no encontrado.");
        }
    }

    public void deactivateUser(UUID id) {
        userRepository.findById(id).ifPresent(user -> {
            user.setActive(false);
            userRepository.save(user);
        });
    }
}
