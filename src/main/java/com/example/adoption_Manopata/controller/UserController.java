package com.example.adoption_Manopata.controller;

import com.example.adoption_Manopata.dto.DeleteAccountRequest;
import com.example.adoption_Manopata.model.User;
import com.example.adoption_Manopata.service.FileStorageService;
import com.example.adoption_Manopata.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Obtener todos los usuarios (solo admin)
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    // Obtener perfil de usuario
    @GetMapping("/profile/{id}")
    public ResponseEntity<?> getUserProfile(@PathVariable Long id) {
        Optional<User> userOptional = userService.getUserById(id);

        if (!userOptional.isPresent()) {
            System.out.println("Usuario no encontrado para ID: " + id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado.");
        }

        User user = userOptional.get();
        System.out.println("Foto del usuario: " + user.getPhoto());
        UserDetails loggedInUser = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String loggedInUsername = loggedInUser.getUsername();

        // Verificar si el usuario autenticado es el mismo que está intentando acceder
        System.out.println("Usuario autenticado: " + loggedInUsername + ", Usuario solicitado: " + user.getNickname());

        if (!user.getNickname().equals(loggedInUsername)) {
            System.out.println("Acceso denegado al perfil del usuario: " + user.getNickname());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No tienes permiso para ver este perfil.");
        }

        System.out.println("Acceso concedido al perfil del usuario: " + user.getNickname());
        return ResponseEntity.ok(user);
    } // pues entonces por teams


    // Actualizar datos de usuario
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody User userDetails) {
        UserDetails loggedInUser = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String loggedInUsername = loggedInUser.getUsername();

        Optional<User> optionalUser = userService.getUserById(id);

        if (!optionalUser.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado.");
        }

        User user = optionalUser.get();

        if (!user.getNickname().equals(loggedInUsername)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No tienes permiso para actualizar este usuario.");
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

        userService.save(user);
        return ResponseEntity.ok(user);
    }

    // Subir imagen de perfil
    @PutMapping("/{id}/profile-image")
    public ResponseEntity<?> updateProfileImage(@PathVariable Long id, @RequestParam("image") MultipartFile imageFile) {
        try {
            // Log para verificar que el archivo se está recibiendo correctamente
            System.out.println("Recibiendo archivo: " + imageFile.getOriginalFilename());

            Optional<User> optionalUser = userService.getUserById(id);

            if (!optionalUser.isPresent()) {
                System.out.println("Usuario no encontrado: " + id);  // Log si no se encuentra el usuario
                return ResponseEntity.status(404).body("Usuario no encontrado.");
            }

            User user = optionalUser.get();
            UserDetails loggedInUser = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String loggedInUsername = loggedInUser.getUsername();

            if (!user.getNickname().equals(loggedInUsername)) {
                System.out.println("Usuario no autorizado para actualizar imagen: " + loggedInUsername);  // Log si no tiene permisos
                return ResponseEntity.status(403).body("No tienes permiso para actualizar la imagen de este usuario.");
            }

            // Log para verificar si la imagen es válida y no está vacía
            if (imageFile.isEmpty()) {
                System.out.println("El archivo de imagen está vacío");
                return ResponseEntity.badRequest().body("La imagen es obligatoria");
            }

            // Guardar la imagen utilizando el servicio de almacenamiento de archivos
            String fileName = fileStorageService.storeFile(imageFile);
            System.out.println("Imagen guardada con nombre: " + fileName);  // Log para confirmar que el archivo se ha guardado

            user.setPhoto("/uploads/" + fileName);
            userService.save(user);  // Guardar los datos actualizados del usuario
            System.out.println("Usuario guardado con imagen actualizada: " + user.getPhoto());  // Verificar que el usuario se guarda correctamente con la imagen

            return ResponseEntity.ok(Collections.singletonMap("fileName", fileName));
        } catch (Exception e) {
            System.out.println("Error al actualizar la imagen: " + e.getMessage());  // Log para capturar cualquier error
            return ResponseEntity.status(500).body("Error al actualizar la imagen de perfil.");
        }
    }

    // Desactivar cuenta (en lugar de eliminar)
    @PostMapping("/{id}/deactivate-account")
    public ResponseEntity<String> deactivateAccount(@PathVariable Long id, @RequestBody DeleteAccountRequest deleteAccountRequest) {
        UserDetails loggedInUser = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String loggedInUsername = loggedInUser.getUsername();

        Optional<User> userOptional = userService.getUserById(id);
        if (!userOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado.");
        }

        User user = userOptional.get();
        if (!user.getNickname().equals(loggedInUsername)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No tienes permiso para desactivar esta cuenta.");
        }

        // Verificar la contraseña proporcionada
        if (!passwordEncoder.matches(deleteAccountRequest.getPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("La contraseña es incorrecta.");
        }

        // Desactivar el usuario en lugar de eliminarlo
        user.setActive(false);  // Esto asume que tienes un campo "active" en tu modelo User
        userService.save(user);

        return ResponseEntity.ok("Cuenta desactivada exitosamente.");
    }
}