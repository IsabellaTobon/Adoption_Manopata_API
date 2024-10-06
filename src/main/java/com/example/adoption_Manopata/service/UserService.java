package com.example.adoption_Manopata.service;

import com.example.adoption_Manopata.model.Role;
import com.example.adoption_Manopata.model.User;
import com.example.adoption_Manopata.repository.RoleRepository;
import com.example.adoption_Manopata.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Obtener todos los usuarios
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Obtener usuario por ID
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    // Buscar usuario por email
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmailAndDeletedFalse(email);
    }

    // Buscar usuario por nickname
    public Optional<User> findByNickname(String nickname) {
        return userRepository.findByNicknameAndDeletedFalse(nickname);
    }

    // Crear nuevo usuario
    public void createUser(User user) {
        if (user.getLastname() == null || user.getLastname().isEmpty()) {
            throw new IllegalArgumentException("El apellido es obligatorio.");
        }

        if (existsByNickname(user.getNickname())) {
            throw new IllegalArgumentException("Error: El nickname ya está en uso.");
        }

        if (existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Error: El email ya está en uso.");
        }

        Role userRole = roleRepository.findByName("USER");
        if (userRole == null) {
            throw new RuntimeException("Role 'USER' no encontrado");
        }

        if (user.getPhoto() == null || user.getPhoto().isEmpty()) {
            user.setPhoto("/images/default-image.webp");  // Ruta de la imagen por defecto
        }

        user.setRole(userRole);
        user.setPassword(passwordEncoder.encode(user.getPassword()));  // Codificar contraseña
        userRepository.save(user);
    }

    // Verificar si el nickname ya existe
    public boolean existsByNickname(String nickname) {
        return userRepository.existsByNicknameAndDeletedFalse(nickname);
    }

    // Verificar si el email ya existe
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmailAndDeletedFalse(email);
    }

    // Verificar disponibilidad de email
    public boolean isEmailAvailable(String email) {
        return !userRepository.existsByEmailAndDeletedFalse(email);
    }

    // Guardar cambios en un usuario
    public void save(User user) {
        userRepository.save(user);
    }

    // Actualizar datos de usuario
    public User updateUser(Long id, User userDetails) {
        return userRepository.findById(id)
                .map(user -> {
                    user.setName(userDetails.getName());
                    user.setLastname(userDetails.getLastname());
                    user.setNickname(userDetails.getNickname());
                    user.setEmail(userDetails.getEmail());
                    if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
                        user.setPassword(passwordEncoder.encode(userDetails.getPassword()));
                    }
                    user.setRole(userDetails.getRole());
                    return userRepository.save(user);
                }).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    // Cambiar contraseña del usuario
    public boolean changePassword(Long userId, String oldPassword, String newPassword) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            if (passwordEncoder.matches(oldPassword, user.getPassword())) {
                user.setPassword(passwordEncoder.encode(newPassword));
                userRepository.save(user);
                return true;
            } else {
                throw new RuntimeException("La contraseña antigua es incorrecta.");
            }
        }
        return false;
    }

    // Método para que el admin desactive al usuario sin contraseña
    public void deactivateUser(Long userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setActive(false); // Desactiva al usuario
            userRepository.save(user);
        } else {
            throw new RuntimeException("Usuario no encontrado.");
        }
    }

    // Método para que el usuario desactive su propia cuenta con contraseña
    public void deactivateUser(Long userId, String password) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            // Verificar la contraseña
            if (passwordEncoder.matches(password, user.getPassword())) {
                user.setActive(false); // Desactivar al usuario
                userRepository.save(user);
            } else {
                throw new RuntimeException("Contraseña incorrecta. No se puede desactivar la cuenta.");
            }
        } else {
            throw new RuntimeException("Usuario no encontrado.");
        }
    }

    // Eliminar físicamente una cuenta (por el admin)
    public void deleteUserByAdmin(Long userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isPresent()) {
            userRepository.delete(optionalUser.get());
        } else {
            throw new RuntimeException("Usuario no encontrado.");
        }
    }
}
