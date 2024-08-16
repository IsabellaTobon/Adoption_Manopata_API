package com.example.adoption_Manopata.controller;

import com.example.adoption_Manopata.model.Post;
import com.example.adoption_Manopata.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/post")
public class PostController {

    @Autowired
    private PostService postService;

    // Obtain all posts with filters
    @GetMapping
    public Page<Post> getAllPosts(
            @RequestParam(required = false) String province,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String breed,
            @RequestParam(required = false) String animalType,
            @RequestParam(required = false) Boolean available,
            @RequestParam(required = false) Boolean isPPP,
            @RequestParam(required = false) Boolean vaccinated,
            Pageable pageable
    ) {
        return postService.getFilteredPosts(province, city, breed, animalType, available, isPPP, vaccinated, pageable);
    }

    // Obtain post by id
    @GetMapping("/{id}")
    public ResponseEntity<Post> getPostById(@PathVariable UUID id) {
        Optional<Post> post = postService.getPostById(id);
        return post.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Create a new post
    @PostMapping
    public Post createPost(@RequestBody Post post) {
        return postService.createPost(post);
    }

    // Update a post
    @PutMapping("/{id}")
    public ResponseEntity<Post> updatePost(@PathVariable UUID id, @RequestBody Post postDetails) {
        Post post = postService.updatePost(id, postDetails);
        return ResponseEntity.ok(post);
    }

    // Delete a post
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable UUID id) {
        postService.deletePost(id);
        return ResponseEntity.ok().build();
    }

    // Increment likes of a post
    @PostMapping("/{id}/like")
    public ResponseEntity<Post> likePost(@PathVariable UUID id) {
        Post post = postService.incrementLikes(id);
        return ResponseEntity.ok(post);
    }

    // Obtain posts by user id
    @GetMapping("/user/{userId}")
    public Page<Post> getPostsByUser(@PathVariable UUID userId,
                                     @RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "10") int size) {
        return postService.getPostsByUser(userId, page, size);
    }

}
