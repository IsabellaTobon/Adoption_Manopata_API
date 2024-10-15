package com.example.adoption_Manopata.controller;

import com.example.adoption_Manopata.dto.*;
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
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


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
    public ResponseEntity<Map<String, Object>> createAuthenticationToken(@RequestBody AuthRequest authRequest) throws Exception {
        try {
            // AUTHENTICATE CREDENTIALS
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getNickname(), authRequest.getPassword())
            );
        } catch (BadCredentialsException e) {
            // IF THE CREDENTIALS ARE INCORRECT, RETURN AN ERROR IN JSON FORMAT
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Incorrect username or password");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }

        // LOAD USER DETAILS AND GENERATE JWT TOKEN
        final UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getNickname());
        final String jwt = jwtUtil.generateToken(userDetails);

        // GET THE USER ROLE FROM UserDetails
        String role = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("USER");  // BY DEFAULT, WE ASSIGN THE ROLE 'USER'

        // GET THE USER FROM THE DATABASE TO GET THE ID
        Optional<User> userOptional = userService.findByNickname(authRequest.getNickname());
        if (!userOptional.isPresent()) {
            // IF THE USER IS NOT FOUND, RETURN AN ERROR
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("error", "User not found"));
        }

        User user = userOptional.get();
        Long userId = user.getId();

        // CREATE A JSON RESPONSE WITH THE TOKEN, ROLE AND USERID
        Map<String, Object> response = new HashMap<>();
        response.put("token", jwt);
        response.put("role", role);
        response.put("userId", userId);  // ADD THE USERID TO THE RESPONSE

        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> registerUser(@RequestBody User user) {

        try {
            // DELEGATE USER CREATION TO THE SERVICE, WHICH HANDLES THE ROLE AND VALIDATION
            userService.createUser(user);

            // CREATE A RESPONSE MAP WITH A MESSAGE
            Map<String, String> response = new HashMap<>();
            response.put("message", "Usuario registrado correctamente.");

            // RETURN THE RESPONSE AS JSON
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            // RETURN VALIDATION ERROR IN JSON FORMAT
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            // RETURN GENERAL ERROR IN JSON FORMAT
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Ocurrió un error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // PASSWORD RECOVERY
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

    // CHANGE PASSWORD
    @PostMapping("/change-password")
    public ResponseEntity<Map<String, String>> changePassword(@RequestBody ChangePasswordRequest changePasswordRequest) {
        UserDetails loggedInUser = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String loggedInUsername = loggedInUser.getUsername();

        User user = userService.findByNickname(loggedInUsername)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        boolean isPasswordChanged = userService.changePassword(user.getId(), changePasswordRequest.getOldPassword(), changePasswordRequest.getNewPassword());

        if (isPasswordChanged) {
            return ResponseEntity.ok(Collections.singletonMap("message", "Contraseña cambiada con éxito."));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Collections.singletonMap("message", "Error al cambiar la contraseña."));
        }
    }

    // RESET PASSWORD
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

    // DEACTIVATE ACCOUNT
    @PostMapping("/deactivate-account")
    public ResponseEntity<String> deactivateAccount(@RequestBody DeleteAccountRequest deleteAccountRequest) {
        User user = userService.findByNickname(deleteAccountRequest.getNickname())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado."));

        // VERIFY THE PASSWORD PROVIDED
        if (!passwordEncoder.matches(deleteAccountRequest.getPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Contraseña incorrecta.");
        }

        // DEACTIVATE USER
        userService.deactivateUser(user.getId(), deleteAccountRequest.getPassword());

        return ResponseEntity.ok("Cuenta desactivada exitosamente.");
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
