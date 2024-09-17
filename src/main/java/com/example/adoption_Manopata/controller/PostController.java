package com.example.adoption_Manopata.controller;

import com.example.adoption_Manopata.model.Post;
import com.example.adoption_Manopata.model.User;
import com.example.adoption_Manopata.service.FileStorageService;
import com.example.adoption_Manopata.service.PostService;
import com.example.adoption_Manopata.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.Collections;
import java.util.Date;
import java.util.Optional;

@RestController
@RequestMapping("/api/post")
public class PostController {

    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;

    @Autowired
    private FileStorageService fileStorageService;

    // Obtain all posts with filters
    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<Post>>> getAllPosts(
            @RequestParam(required = false) String province,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String breed,
            @RequestParam(required = false) String animalType,
            @RequestParam(required = false) Boolean available,
            @RequestParam(required = false) Boolean isPPP,
            @RequestParam(required = false) Boolean vaccinated,
            Pageable pageable,
            PagedResourcesAssembler<Post> assembler // Usamos PagedResourcesAssembler
    ) {
        // Obtener los posts filtrados
        Page<Post> posts = postService.getFilteredPosts(province, city, breed, animalType, available, isPPP, vaccinated, pageable);

        // Convertir a PagedModel<EntityModel<Post>>
        PagedModel<EntityModel<Post>> pagedModel = assembler.toModel(posts);

        return ResponseEntity.ok(pagedModel);  // Devolver el modelo paginado
    }

    // Obtain post by id
    @GetMapping("/{id}")
    public ResponseEntity<Post> getPostById(@PathVariable Long id) {
        Optional<Post> post = postService.getPostById(id);
        return post.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Create a new post
    @PostMapping("/create")
    public ResponseEntity<?> createPost(
            @RequestParam("file") MultipartFile file,
            @RequestParam("post") String postJson,
            Principal principal) {
        try {
            // Validar si la imagen está presente
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("La imagen es obligatoria");
            }

            // Convertir el JSON del post a un objeto Post usando ObjectMapper
            ObjectMapper objectMapper = new ObjectMapper();
            Post post = objectMapper.readValue(postJson, Post.class);

            // Obtener el usuario autenticado
            String nickname = principal.getName(); // Extrae el nombre del usuario autenticado
            User user = userService.findByNickname(nickname).orElse(null); // Obtén el usuario por el nombre de usuario

            if (user == null) {
                return ResponseEntity.badRequest().body("Usuario no encontrado");
            }

            // Asignar el usuario al post
            post.setUser(user);

            // Guardar la imagen
            String fileName = fileStorageService.storeFile(file);

            // Configurar la ruta de la imagen
            post.setPhoto("/uploads/" + fileName);
            post.setRegisterDate(new Date());

            // Guardar el post en la base de datos
            postService.createPost(post);

            return ResponseEntity.ok(Collections.singletonMap("message", "Post creado exitosamente"));
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Error al subir la imagen: " + e.getMessage());
        }
    }

    // Update a post
    @PutMapping("/{id}")
    public ResponseEntity<Post> updatePost(@PathVariable Long id, @RequestBody Post postDetails) {
        Post post = postService.updatePost(id, postDetails);
        return ResponseEntity.ok(post);
    }

    // Delete a post
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        postService.deletePost(id);
        return ResponseEntity.ok().build();
    }

    // Increment likes of a post
    @PostMapping("/{id}/like")
    public ResponseEntity<Post> likePost(@PathVariable Long id) {
        Post post = postService.incrementLikes(id);
        return ResponseEntity.ok(post);
    }

    // Obtain posts by user id
    @GetMapping("/user/{userId}")
    public Page<Post> getPostsByUser(@PathVariable Long userId,
                                     @RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "10") int size) {
        return postService.getPostsByUser(userId, page, size);
    }

}
