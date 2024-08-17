package com.example.adoption_Manopata.service;

import com.example.adoption_Manopata.model.Post;
import com.example.adoption_Manopata.repository.PostRepository;
import com.example.adoption_Manopata.specification.PostSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    // Obtain all posts with filters
    public Page<Post> getFilteredPosts(String province, String city, String breed, String animalType, Boolean available, Boolean isPPP, Boolean vaccinated, Pageable pageable) {
        Specification<Post> spec = Specification
                .where(PostSpecification.hasProvince(province))
                .and(PostSpecification.hasCity(city))
                .and(PostSpecification.hasBreed(breed))
                .and(PostSpecification.hasAnimalType(animalType))
                .and(PostSpecification.isAvailable(available))
                .and(PostSpecification.isPPP(isPPP))
                .and(PostSpecification.isVaccinated(vaccinated));

        return postRepository.findAll(spec, pageable);
    }

    // Obtain post by id
    public Optional<Post> getPostById(UUID id) {
        return postRepository.findById(id);
    }

    // Create a new post
    public Post createPost(Post post) {
        return postRepository.save(post);
    }

    // Update a post
    public Post updatePost(UUID id, Post postDetails) {
        return postRepository.findById(id)
                .map(post -> {
                    post.setName(postDetails.getName());
                    post.setBreed(postDetails.getBreed());
                    post.setAnimalType(postDetails.getAnimalType());
                    post.setAge(postDetails.getAge());
                    post.setCity(postDetails.getCity());
                    post.setProvince(postDetails.getProvince());
                    post.setDescription(postDetails.getDescription());
                    post.setPpp(postDetails.getPpp());
                    post.setVaccinated(postDetails.getVaccinated());
                    post.setAvailable(postDetails.getAvailable());
                    post.setPhoto(postDetails.getPhoto());
                    return postRepository.save(post);
                }).orElseThrow(() -> new RuntimeException("Post not found"));
    }

    // Delete a post
    public void deletePost(UUID id) {
        postRepository.deleteById(id);
    }

    // Increase likes of a post
    public Post incrementLikes(UUID id) {
        return postRepository.findById(id)
                .map(post -> {
                    post.setLikes(post.getLikes() + 1);
                    return postRepository.save(post);
                }).orElseThrow(() -> new RuntimeException("Post not found"));
    }

    // Obtain all posts by user id
    public Page<Post> getPostsByUser(UUID userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return postRepository.findByUserId(userId, pageable);
    }

}
