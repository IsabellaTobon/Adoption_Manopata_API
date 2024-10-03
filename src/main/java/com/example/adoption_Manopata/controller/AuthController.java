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
            // Autenticar las credenciales
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getNickname(), authRequest.getPassword())
            );
        } catch (BadCredentialsException e) {
            // Si las credenciales son incorrectas, devolver un error en formato JSON
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Incorrect username or password");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }

        // Cargar detalles del usuario y generar el token JWT
        final UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getNickname());
        final String jwt = jwtUtil.generateToken(userDetails);

        // Obtener el rol del usuario desde UserDetails
        String role = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("USER");  // Por defecto, asignamos el rol 'USER'

        // Obtener el usuario desde la base de datos para obtener el ID
        Optional<User> userOptional = userService.findByNickname(authRequest.getNickname());
        if (!userOptional.isPresent()) {
            // En caso de que no se encuentre el usuario, devolver un error
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("error", "User not found"));
        }

        User user = userOptional.get();
        Long userId = user.getId();

        // Crear una respuesta JSON con el token, rol y userId
        Map<String, Object> response = new HashMap<>();
        response.put("token", jwt);
        response.put("role", role);
        response.put("userId", userId);  // Añadir el userId a la respuesta

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

    // RECUPERACIÓN DE CONTRASEÑA
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

    // CAMBIO DE CONTRASEÑA
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

    // RESTABLECIMIENTO DE CONTRASEÑA
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

    // DESACTIVAR CUENTA
    @PostMapping("/deactivate-account")
    public ResponseEntity<String> deactivateAccount(@RequestBody DeleteAccountRequest deleteAccountRequest) {
        User user = userService.findByNickname(deleteAccountRequest.getNickname())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado."));

        // Verificar la contraseña proporcionada
        if (!passwordEncoder.matches(deleteAccountRequest.getPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Contraseña incorrecta.");
        }

        // Desactivar al usuario
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
