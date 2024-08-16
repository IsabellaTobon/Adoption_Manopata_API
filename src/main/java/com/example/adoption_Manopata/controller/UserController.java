package com.example.adoption_Manopata.controller;

import com.example.adoption_Manopata.dto.DeleteAccountRequest;
import com.example.adoption_Manopata.model.User;
import com.example.adoption_Manopata.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable UUID id) {
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
    public ResponseEntity<User> updateUser(@PathVariable UUID id, @RequestBody User userDetails) {
        User user = userService.updateUser(id, userDetails);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/delete-account")
    public ResponseEntity<String> deleteAccount(@RequestBody DeleteAccountRequest deleteAccountRequest) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String loggedInUsername = userDetails.getUsername();

        // Search User in database
        User user = userService.findByNickname(loggedInUsername)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado."));

        // Verify if the nickname in the request matches the logged in user
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
