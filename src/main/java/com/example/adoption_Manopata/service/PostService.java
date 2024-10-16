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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

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
    public Optional<Post> getPostById(Long id) {
        return postRepository.findById(id);
    }

    public List<Post> getPostsByIds(List<Long> ids) {
        return postRepository.findAllById(ids);
    }
    
    // Create a new post
    public void createPost(Post post) {
        if (post.getPhoto() == null || post.getPhoto().isEmpty()) {
            throw new IllegalArgumentException("La imagen es obligatoria para crear un post.");
        }
        postRepository.save(post);
    }

    // GET ALL PROVINCES FROM THE POSTS TABLE
    public List<String> getAllProvinces() {
        return postRepository.findAllProvinces();
    }

    // GET THE CITIES ACCORDING TO THE PROVINCE FROM THE POSTS TABLE
    public List<String> getCitiesByProvince(String province) {
        return postRepository.findCitiesByProvince(province);
    }

    // GET THE BREEDS ACCORDING TO THE TYPE OF ANIMAL FROM THE POSTS TABLE
    public List<String> getBreedsByAnimalType(String animalType) {
        return postRepository.findBreedsByAnimalType(animalType);
    }

    // UPDATE POST
    public Post updatePost(Long id, Post postDetails) {
        return postRepository.findById(id)
                .map(post -> {
                    if (postDetails.getPhoto() == null || postDetails.getPhoto().isEmpty()) {
                        throw new IllegalArgumentException("La imagen es obligatoria para actualizar el post.");
                    }
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

    // DELETE POST
    public void deletePost(Long id) {
        postRepository.findById(id).ifPresent(post -> {
            // PATH WHERE THE IMAGES ARE STORED
            Path imagePath = Paths.get("path/to/your/images/directory", post.getPhoto());

            // DELETE THE ASSOCIATED IMAGE
            try {
                Files.deleteIfExists(imagePath);
            } catch (IOException e) {
                throw new RuntimeException("Error al eliminar la imagen asociada", e);
            }

            // DELETE POST
            postRepository.deleteById(id);
        });
    }

    // INCREASE LIKES OF A POST
    public Post incrementLikes(Long postId) {
        Optional<Post> optionalPost = postRepository.findById(postId);

        if (optionalPost.isPresent()) {
            Post post = optionalPost.get();
            post.setLikes(post.getLikes() + 1);
            postRepository.save(post);
            return post;
        } else {
            return null;
        }
    }

    // OBTAIN ALL POSTS BY USER ID
    public Page<Post> getPostsByUser(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return postRepository.findByUserId(userId, pageable);
    }

}
