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
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getNickname(), authRequest.getPassword())
            );
        } catch (BadCredentialsException e) {
            throw new Exception("Incorrect username or password", e);
        }

        final UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getNickname());

        return jwtUtil.generateToken(userDetails);
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody User user) {

        try {
            // Delegate user creation to the service, which handles the role and validation
            userService.createUser(user);
            // Return answer
            return ResponseEntity.ok("Usuario registrado correctamente.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());  // Retorna cualquier error de validación
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ocurrió un error: " + e.getMessage());
        }
    }


    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody ForgotPasswordRequest forgotPasswordRequest) {
        User user = userService.findByEmail(forgotPasswordRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("Error: Email no registrado."));

        String token = jwtUtil.generateTokenWithEmail(user.getEmail());
        String resetLink = "http://localhost:4200/reset-password?token=" + token;

        emailService.sendEmail(user.getEmail(), "Restablecimiento de Contraseña",
                "Haz clic en el siguiente enlace para restablecer tu contraseña: " + resetLink);

        return ResponseEntity.ok("Se ha enviado un enlace de restablecimiento de contraseña a tu email.");
    }

    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestBody ChangePasswordRequest changePasswordRequest) {
        User user = userService.findByNickname(changePasswordRequest.getNickname())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        boolean isPasswordChanged = userService.changePassword(user.getId(), changePasswordRequest.getOldPassword(), changePasswordRequest.getNewPassword());

        if (isPasswordChanged) {
            return ResponseEntity.ok("Contraseña actualizada exitosamente.");
        } else {
            return ResponseEntity.badRequest().body("Error al cambiar la contraseña.");
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequest resetPasswordRequest) {
        String token = resetPasswordRequest.getToken();

        try {
            Claims claims = jwtUtil.extractAllClaims(token);
            if ("password_reset".equals(claims.getSubject())) {
                String email = claims.get("email", String.class);
                User user = userService.findByEmail(email)
                        .orElseThrow(() -> new RuntimeException("Usuario no encontrado."));

                user.setPassword(passwordEncoder.encode(resetPasswordRequest.getNewPassword()));
                userService.save(user);

                return ResponseEntity.ok("Contraseña restablecida correctamente.");
            } else {
                return ResponseEntity.badRequest().body("Token de restablecimiento de contraseña no válido.");
            }
        } catch (JwtException e) {
            return ResponseEntity.badRequest().body("Token de restablecimiento de contraseña no válido o expirado.");
        }
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<String> refreshToken(HttpServletRequest request) {
        String token = jwtUtil.resolveToken(request);

        if (token != null) {
            String nickname = jwtUtil.extractNickname(token);
            UserDetails userDetails = userDetailsService.loadUserByUsername(nickname);

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

}
