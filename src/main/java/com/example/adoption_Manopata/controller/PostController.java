package com.example.adoption_Manopata.controller;

import com.example.adoption_Manopata.model.Post;
import com.example.adoption_Manopata.model.User;
import com.example.adoption_Manopata.repository.PostRepository;
import com.example.adoption_Manopata.service.FileStorageService;
import com.example.adoption_Manopata.service.PostService;
import com.example.adoption_Manopata.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/post")
public class PostController {

    @Autowired
    private PostService postService;

    @Autowired
    private PostRepository postRepository;

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
            PagedResourcesAssembler<Post> assembler,  // Usamos PagedResourcesAssembler
            Principal principal // Para obtener el usuario autenticado
    ) {
        // Obtener los posts filtrados
        Page<Post> posts = postService.getFilteredPosts(province, city, breed, animalType, available, isPPP, vaccinated, pageable);

        final User user;

        if (principal != null) {
            String nickname = principal.getName();
            user = userService.findByNickname(nickname).orElse(null);
        } else {
            user = null;
        }

        posts.forEach(post -> {
            if (user != null) {
                boolean hasLiked = post.getLikedByUsers().contains(user);
                post.setUserHasLiked(hasLiked);
                System.out.println("Post " + post.getId() + " - Usuario " + user.getId() + " ha dado like: " + hasLiked);
            } else {
                post.setUserHasLiked(false);
            }
        });

        // Convertir a PagedModel<EntityModel<Post>>
        PagedModel<EntityModel<Post>> pagedModel = assembler.toModel(posts);

        return ResponseEntity.ok(pagedModel);
    }

    // Obtain post by id
    @GetMapping("/{id}")
    public ResponseEntity<Post> getPostById(@PathVariable Long id) {
        Optional<Post> post = postService.getPostById(id);
        return post.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/by-ids")
    public ResponseEntity<List<Post>> getPostsByIds(@RequestParam List<Long> ids) {
        List<Post> posts = postService.getPostsByIds(ids);
        return ResponseEntity.ok(posts);
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

    // Incrementar likes de un post
    @PostMapping("/{id}/like")
    public ResponseEntity<?> likePost(@PathVariable Long id, Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no autenticado");
        }

        String nickname = principal.getName();  // Usamos el nombre de usuario autenticado (nickname)
        Optional<User> userOpt = userService.findByNickname(nickname);
        if (!userOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
        }
        User user = userOpt.get();

        Optional<Post> postOpt = postService.getPostById(id);
        if (!postOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Post no encontrado");
        }
        Post post = postOpt.get();

        // Verificar si el usuario ya dio like
        if (post.getLikedByUsers().contains(user)) {
            // Si ya dio like, lo quitamos y reducimos los likes
            post.getLikedByUsers().remove(user);
            post.setLikes(post.getLikes() - 1);
        } else {
            // Si no ha dado like, lo añadimos y aumentamos los likes
            post.getLikedByUsers().add(user);
            post.setLikes(post.getLikes() + 1);
        }

        postRepository.save(post);  // Guardar los cambios usando el repositorio directamente
        return ResponseEntity.ok(Collections.singletonMap("likes", post.getLikes()));
    }

    // Obtener todas las provincias
    @GetMapping("/provinces")
    public ResponseEntity<List<String>> getProvinces() {
        List<String> provinces = postService.getAllProvinces();
        return ResponseEntity.ok(provinces);
    }

    // Obtener ciudades según la provincia
    @GetMapping("/cities")
    public ResponseEntity<List<String>> getCities(@RequestParam String province) {
        List<String> cities = postService.getCitiesByProvince(province);
        return ResponseEntity.ok(cities);
    }

    // Obtener razas según el tipo de animal
    @GetMapping("/breeds")
    public ResponseEntity<List<String>> getBreeds(@RequestParam String animalType) {
        List<String> breeds = postService.getBreedsByAnimalType(animalType);
        return ResponseEntity.ok(breeds);
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

    // Obtain posts by user id
    @GetMapping("/user/{userId}")
    public Page<Post> getPostsByUser(@PathVariable Long userId,
                                     @RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "10") int size) {
        return postService.getPostsByUser(userId, page, size);
    }

}
