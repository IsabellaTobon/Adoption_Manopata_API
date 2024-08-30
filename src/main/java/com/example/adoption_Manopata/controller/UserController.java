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
import java.util.Optional;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private MyUserDetailsService myUserDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

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

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(HttpServletRequest request) {
        String token = jwtUtil.resolveToken(request);

        if (token != null) {
            String nickname = jwtUtil.extractNickname(token);  // Extract nickname from the token
            UserDetails userDetails = myUserDetailsService.loadUserByUsername(nickname);

            // Pass both arguments to validate the token
            if (jwtUtil.validateToken(token, userDetails)) {
                String newToken = jwtUtil.generateToken(userDetails);
                return ResponseEntity.ok(newToken);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido.");
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido o expirado.");
        }
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
            if (userDetails.getNickname() != null && !userDetails.getNickname().equals(user.getNickname())) {
                // Check if the new nickname is already in use
                if (userService.existsByNickname(userDetails.getNickname())) {
                    return ResponseEntity.status(HttpStatus.CONFLICT).body("El nombre de usuario ya está en uso.");
                }
                user.setNickname(userDetails.getNickname());
            }

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

    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestBody ChangePasswordRequest changePasswordRequest) {
        // Get the authenticated user
        UserDetails loggedInUser = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String loggedInUsername = loggedInUser.getUsername();

        // Load user details using MyUserDetailsService
        UserDetails userDetails = myUserDetailsService.loadUserByUsername(changePasswordRequest.getNickname());

        // Verify if the nickname in the request matches the logged-in user
        if (!userDetails.getUsername().equals(loggedInUsername)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No tienes permiso para cambiar la contraseña de este usuario.");
        }

        // Convert UserDetails to User entity for further processing
        User user = userService.findByNickname(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Verify the old password before proceeding with the update
        if (!passwordEncoder.matches(changePasswordRequest.getOldPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("La contraseña actual es incorrecta.");
        }

        // Set the new password
        user.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
        userService.save(user);

        return ResponseEntity.ok("Contraseña actualizada correctamente.");
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody ForgotPasswordRequest forgotPasswordRequest) {
        Optional<User> userOptional = userService.findByEmail(forgotPasswordRequest.getEmail());

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            // Generate a token for password reset
            String resetToken = jwtUtil.generatePasswordResetToken(user);

            // Send an email with the reset link
            String resetLink = "http://localhost:8080/api/user/reset-password?token=" + resetToken;
            emailService.sendPasswordResetEmail(user.getEmail(), resetLink);

            return ResponseEntity.ok("Se ha enviado un correo electrónico para restablecer la contraseña.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se encontró ningún usuario con ese correo electrónico.");
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequest resetPasswordRequest) {
        String token = resetPasswordRequest.getToken();

        try {
            Claims claims = jwtUtil.extractAllClaims(token);

            // Verify if the token is for password reset
            if ("password_reset".equals(claims.getSubject())) {

                // Extract the email from the token
                String email = claims.get("email", String.class);
                Optional<User> userOptional = userService.findByEmail(email);

                // Verify if the user exists
                if (userOptional.isPresent()) {
                    User user = userOptional.get();

                    // Set the new password
                    user.setPassword(passwordEncoder.encode(resetPasswordRequest.getNewPassword()));
                    userService.save(user);

                    return ResponseEntity.ok("Contraseña restablecida correctamente.");
                } else {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado.");
                }
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Token de restablecimiento de contraseña no válido.");
            }
        } catch (JwtException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Token de restablecimiento de contraseña no válido o expirado.");
        }
    }

    @PostMapping("/delete-account")
    public ResponseEntity<String> deleteAccount(@RequestBody DeleteAccountRequest deleteAccountRequest) {
        // Get the authenticated user
        UserDetails loggedInUser = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String loggedInUsername = loggedInUser.getUsername();

        // Load user details using MyUserDetailsService
        UserDetails userDetails = myUserDetailsService.loadUserByUsername(loggedInUsername);

        // Verify if the nickname in the request matches the logged-in user
        if (!userDetails.getUsername().equals(deleteAccountRequest.getNickname())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No tienes permiso para eliminar esta cuenta.");
        }

        // Convert UserDetails to User entity for further processing
        User user = userService.findByNickname(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado."));

        // Try to delete the account (logical delete)
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
