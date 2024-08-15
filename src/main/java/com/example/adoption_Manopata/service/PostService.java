package com.example.adoption_Manopata.service;

import com.example.adoption_Manopata.model.Post;
import com.example.adoption_Manopata.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    public Optional<Post> getPostById(UUID id) {
        return postRepository.findById(id);
    }

    public Post createPost(Post post) {
        return postRepository.save(post);
    }

    public Post updatePost(UUID id, Post postDetails) {
        return postRepository.findById(id)
                .map(post -> {
                    post.setPhoto(postDetails.getPhoto());
                    post.setRegisterDate(postDetails.getRegisterDate());
                    post.setName(postDetails.getName());
                    post.setAge(postDetails.getAge());
                    post.setAnimalType(postDetails.getAnimalType());
                    post.setVaccinated(postDetails.getVaccinated());
                    post.setBreed(postDetails.getBreed());
                    post.setPpp(postDetails.getPpp());
                    post.setCity(postDetails.getCity());
                    post.setProvince(postDetails.getProvince());
                    post.setAvailable(postDetails.getAvailable());
                    post.setLikes(postDetails.getLikes());
                    post.setDescription(postDetails.getDescription());
                    post.setUser(postDetails.getUser());
                    return postRepository.save(post);
                }).orElseThrow(() -> new RuntimeException("Post not found"));
    }

    public void deletePost(UUID id) {
        postRepository.deleteById(id);
    }

}
