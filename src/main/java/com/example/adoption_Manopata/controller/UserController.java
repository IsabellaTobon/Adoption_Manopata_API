package com.example.adoption_Manopata.controller;

import com.example.adoption_Manopata.dto.DeleteAccountRequest;
import com.example.adoption_Manopata.model.User;
import com.example.adoption_Manopata.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        Optional<User> user = userService.getUserById(id);
        return user.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/nickname/{nickname}")
    public ResponseEntity<User> getUserByNickname(@PathVariable String nickname) {
        Optional<User> user = userService.findByNickname(nickname);
        return user.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public User createUser(@RequestBody User user) {
        return userService.createUser(user);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody User userDetails) {
        // Get the authenticated user
        UserDetails loggedInUser = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String loggedInUsername = loggedInUser.getUsername();

        // Search the user in the database
        Optional<User> optionalUser = userService.getUserById(id);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            // Verify if the nickname in the request matches the logged-in user
            if (!user.getNickname().equals(loggedInUsername)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No tienes permiso para actualizar este usuario.");
            }

            // Verify the password before proceeding with the update
            if (!passwordEncoder.matches(userDetails.getPassword(), user.getPassword())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("La contraseña es incorrecta.");
            }

            // Update data (except role and ID)
            if (userDetails.getName() != null) user.setName(userDetails.getName());
            if (userDetails.getLastname() != null) user.setLastname(userDetails.getLastname());
            if (userDetails.getEmail() != null) user.setEmail(userDetails.getEmail());

            // If a new password is provided, encrypt it and save it
            if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
                user.setPassword(passwordEncoder.encode(userDetails.getPassword()));
            }

            userService.save(user);

            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado.");
        }
    }


    @PostMapping("/delete-account")
    public ResponseEntity<String> deleteAccount(@RequestBody DeleteAccountRequest deleteAccountRequest) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String loggedInUsername = userDetails.getUsername();

        // Search User in database
        User user = userService.findByNickname(loggedInUsername)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado."));

        // Verify if the nickname in the request matches the logged-in user
        if (!user.getNickname().equals(deleteAccountRequest.getNickname())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No tienes permiso para eliminar esta cuenta.");
        }

        // Try to delete the account (logic delete)
        boolean deleted = userService.deleteUser(user.getId(), deleteAccountRequest.getPassword());

        if (deleted) {
            return ResponseEntity.ok("Cuenta eliminada exitosamente.");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No se pudo eliminar la cuenta.");
        }
    }
}