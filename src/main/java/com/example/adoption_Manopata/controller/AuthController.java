package com.example.adoption_Manopata.controller;

import com.example.adoption_Manopata.dto.AuthRequest;
import com.example.adoption_Manopata.dto.ChangePasswordRequest;
import com.example.adoption_Manopata.dto.ForgotPasswordRequest;
import com.example.adoption_Manopata.model.User;
import com.example.adoption_Manopata.security.JwtUtil;
import com.example.adoption_Manopata.service.EmailService;
import com.example.adoption_Manopata.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    @PostMapping("/login")
    public String createAuthenticationToken(@RequestBody AuthRequest authRequest) throws Exception {
        // Authentication
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getNickname(), authRequest.getPassword())
        );

        // Load user details
        final UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getNickname());


        // Generate the JWT token using the user details
        final String jwt = jwtUtil.generateToken(userDetails);

        return jwt;
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody User user) {
        if (userService.existsByNickname(user.getNickname())) {
            return ResponseEntity.badRequest().body("Error: El nickname ya está en uso.");
        }

        if (userService.existsByEmail(user.getEmail())) {
            return ResponseEntity.badRequest().body("Error: El email ya está en uso.");
        }

        // Encode password
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userService.save(user);
        return ResponseEntity.ok("Usuario registrado exitosamente.");
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody ForgotPasswordRequest forgotPasswordRequest) {
        User user = userService.findByEmail(forgotPasswordRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("Error: Email no registrado."));

        String token = jwtUtil.generateTokenWithEmail(user.getEmail());
        String resetLink = "http://localhost:4200/reset-password?token=" + token;

        // Enviar email con el enlace de restablecimiento de contraseña
        emailService.sendEmail(user.getEmail(), "Restablecimiento de Contraseña",
                "Haz clic en el siguiente enlace para restablecer tu contraseña: " + resetLink);

        return ResponseEntity.ok("Se ha enviado un enlace de restablecimiento de contraseña a tu email.");
    }

    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestBody ChangePasswordRequest changePasswordRequest) {
        // Search the user by nickname
        User user = userService.findByNickname(changePasswordRequest.getNickname())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Try to change the password
        try {
            boolean isPasswordChanged = userService.changePassword(user.getId(), changePasswordRequest.getOldPassword(), changePasswordRequest.getNewPassword());

            if (isPasswordChanged) {
                return ResponseEntity.ok("Contraseña actualizada exitosamente.");
            } else {
                return ResponseEntity.badRequest().body("Error al cambiar la contraseña.");
            }
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
