package com.example.adoption_Manopata.service;

import com.example.adoption_Manopata.model.Role;
import com.example.adoption_Manopata.model.User;
import com.example.adoption_Manopata.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmailAndDeletedFalse(email);
    }

    public Optional<User> findByNickname(String nickname) {
        return userRepository.findByNicknameAndDeletedFalse(nickname);
    }

    public User createUser(User user) {

        // Checks if role is null or empty, and sets "USER" by default
        if (user.getRole() == null || user.getRole().getName().isEmpty()) {
            user.setRole(new Role("USER"));
        }

        // Encode the password before saving it
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        return userRepository.save(user);
    }

    public boolean existsByNickname(String nickname) {
        return userRepository.existsByNicknameAndDeletedFalse(nickname);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmailAndDeletedFalse(email);
    }

    // Save user changes
    public void save(User user) {
        userRepository.save(user);
    }

    // Update user data
    public User updateUser(Long id, User userDetails) {
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

    public boolean changePassword(Long userId, String oldPassword, String newPassword) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            // Verify if the old password matches with the one stored
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

    // Method to delete a user (mark as deleted)
    public boolean deleteUser(Long userId, String password) {
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

    // Deactivate user
    public void deactivateUser(Long id) {
        userRepository.findById(id).ifPresent(user -> {
            user.setActive(false);
            userRepository.save(user);
        });
    }
}
