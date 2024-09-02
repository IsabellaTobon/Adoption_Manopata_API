package com.example.adoption_Manopata.controller;

import com.example.adoption_Manopata.dto.ChangePasswordRequest;
import com.example.adoption_Manopata.dto.DeleteAccountRequest;
import com.example.adoption_Manopata.dto.ForgotPasswordRequest;
import com.example.adoption_Manopata.dto.ResetPasswordRequest;
import com.example.adoption_Manopata.model.User;
import com.example.adoption_Manopata.security.JwtUtil;
import com.example.adoption_Manopata.service.EmailService;
import com.example.adoption_Manopata.service.MyUserDetailsService;
import com.example.adoption_Manopata.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
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

    @PostMapping("/check-nickname")
    public ResponseEntity<Boolean> checkNicknameAvailability(@RequestBody Map<String, String> request) {
        String nickname = request.get("nickname");
        boolean exists = userService.existsByNickname(nickname);
        return ResponseEntity.ok(!exists);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody User userDetails) {
        UserDetails loggedInUser = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String loggedInUsername = loggedInUser.getUsername();

        Optional<User> optionalUser = userService.getUserById(id);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            if (!user.getNickname().equals(loggedInUsername)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No tienes permiso para actualizar este usuario.");
            }

            if (!passwordEncoder.matches(userDetails.getPassword(), user.getPassword())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("La contraseña es incorrecta.");
            }

            if (userDetails.getName() != null) user.setName(userDetails.getName());
            if (userDetails.getLastname() != null) user.setLastname(userDetails.getLastname());
            if (userDetails.getEmail() != null) user.setEmail(userDetails.getEmail());
            if (userDetails.getNickname() != null && !userDetails.getNickname().equals(user.getNickname())) {
                if (userService.existsByNickname(userDetails.getNickname())) {
                    return ResponseEntity.status(HttpStatus.CONFLICT).body("El nombre de usuario ya está en uso.");
                }
                user.setNickname(userDetails.getNickname());
            }

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
        UserDetails loggedInUser = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String loggedInUsername = loggedInUser.getUsername();

        User user = userService.findByNickname(loggedInUsername)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado."));

        boolean deleted = userService.deleteUser(user.getId(), deleteAccountRequest.getPassword());

        if (deleted) {
            return ResponseEntity.ok("Cuenta eliminada exitosamente.");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No se pudo eliminar la cuenta.");
        }
    }

    // Activate user (ADMIN)
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/activate-user/{id}")
    public ResponseEntity<String> activateUser(@PathVariable Long id) {
        Optional<User> userOptional = userService.getUserById(id);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setActive(true);
            userService.save(user);
            return ResponseEntity.ok("Usuario activado correctamente.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado.");
        }
    }
}
