package com.example.adoption_Manopata.controller;

import com.example.adoption_Manopata.dto.AuthRequest;
import com.example.adoption_Manopata.dto.ChangePasswordRequest;
import com.example.adoption_Manopata.dto.ForgotPasswordRequest;
import com.example.adoption_Manopata.dto.ResetPasswordRequest;
import com.example.adoption_Manopata.model.Role;
import com.example.adoption_Manopata.model.User;
import com.example.adoption_Manopata.security.JwtUtil;
import com.example.adoption_Manopata.service.EmailService;
import com.example.adoption_Manopata.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;


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
    public ResponseEntity<Map<String, String>> createAuthenticationToken(@RequestBody AuthRequest authRequest) throws Exception {
        try {
            // Autenticar las credenciales
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getNickname(), authRequest.getPassword())
            );
        } catch (BadCredentialsException e) {
            // Si las credenciales son incorrectas, devolver un error en formato JSON
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Incorrect username or password");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }

        // Cargar detalles del usuario y generar el token JWT
        final UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getNickname());
        final String jwt = jwtUtil.generateToken(userDetails);

        // Crear una respuesta JSON con el token
        Map<String, String> response = new HashMap<>();
        response.put("token", jwt);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> registerUser(@RequestBody User user) {

        try {
            // Delegate user creation to the service, which handles the role and validation
            userService.createUser(user);

            // Create a response map with a message
            Map<String, String> response = new HashMap<>();
            response.put("message", "Usuario registrado correctamente.");

            // Return the response as JSON
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            // Return validation error in JSON format
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            // Return general error in JSON format
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Ocurrió un error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }


    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> forgotPassword(@RequestBody ForgotPasswordRequest forgotPasswordRequest) {
        User user = userService.findByEmail(forgotPasswordRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("Error: Email no registrado."));

        String token = jwtUtil.generateTokenWithEmail(user.getEmail());
        String resetLink = "http://localhost:4200/reset-password?token=" + token;

        emailService.sendEmail(user.getEmail(), "Restablecimiento de Contraseña",
                "Haz clic en el siguiente enlace para restablecer tu contraseña: " + resetLink);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Se ha enviado un enlace de restablecimiento de contraseña a tu email.");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/change-password")
    public ResponseEntity<Map<String, String>> changePassword(@RequestBody ChangePasswordRequest changePasswordRequest) {
        User user = userService.findByNickname(changePasswordRequest.getNickname())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        boolean isPasswordChanged = userService.changePassword(user.getId(), changePasswordRequest.getOldPassword(), changePasswordRequest.getNewPassword());

        Map<String, String> response = new HashMap<>();
        if (isPasswordChanged) {
            response.put("message", "Contraseña actualizada exitosamente.");
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "Error al cambiar la contraseña.");
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(@RequestBody ResetPasswordRequest resetPasswordRequest) {
        String token = resetPasswordRequest.getToken();

        try {
            Claims claims = jwtUtil.extractAllClaims(token);
            if ("password_reset".equals(claims.getSubject())) {
                String email = claims.get("email", String.class);
                User user = userService.findByEmail(email)
                        .orElseThrow(() -> new RuntimeException("Usuario no encontrado."));

                user.setPassword(passwordEncoder.encode(resetPasswordRequest.getNewPassword()));
                userService.save(user);

                Map<String, String> response = new HashMap<>();
                response.put("message", "Contraseña restablecida correctamente.");
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(Map.of("message", "Token de restablecimiento de contraseña no válido."));
            }
        } catch (JwtException e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Token de restablecimiento de contraseña no válido o expirado."));
        }
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<Map<String, String>> refreshToken(HttpServletRequest request) {
        String token = jwtUtil.resolveToken(request);

        if (token != null) {
            String nickname = jwtUtil.extractNickname(token);
            UserDetails userDetails = userDetailsService.loadUserByUsername(nickname);

            if (jwtUtil.validateToken(token, userDetails)) {
                String newToken = jwtUtil.generateToken(userDetails);
                return ResponseEntity.ok(Map.of("token", newToken));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Token inválido."));
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Token inválido o expirado."));
        }
    }
}
