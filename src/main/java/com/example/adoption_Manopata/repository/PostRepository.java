package com.example.adoption_Manopata.repository;

import com.example.adoption_Manopata.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;


@Repository
public interface PostRepository extends JpaRepository<Post, Long>, JpaSpecificationExecutor<Post> {

    // Method to find all posts by user id with pagination
    Page<Post> findByUserId(Long userId, Pageable pageable);

    // Method to find posts by filters with pagination
    Page<Post> findByProvinceAndCityAndBreedAndAnimalTypeAndAvailableAndPppAndVaccinated(
            String province,
            String city,
            String breed,
            String animalType,
            Boolean available,
            Boolean ppp,
            Boolean vaccinated,
            Pageable pageable
    );
}
