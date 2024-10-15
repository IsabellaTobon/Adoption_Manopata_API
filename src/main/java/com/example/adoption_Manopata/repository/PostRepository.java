package com.example.adoption_Manopata.repository;

import com.example.adoption_Manopata.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface PostRepository extends JpaRepository<Post, Long>, JpaSpecificationExecutor<Post> {

    // METHOD TO FIND ALL POSTS BY USER ID WITH PAGINATION
    Page<Post> findByUserId(Long userId, Pageable pageable);

    // METHOD TO FIND POSTS BY FILTERS WITH PAGINATION
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

    // OBTAIN ALL PROVINCES
    @Query("SELECT DISTINCT p.province FROM Post p")
    List<String> findAllProvinces();

    // GET CITIES BY PROVINCE
    @Query("SELECT DISTINCT p.city FROM Post p WHERE p.province = :province")
    List<String> findCitiesByProvince(@Param("province") String province);

    // GET BREEDS BASED ON ANIMAL TYPE
    @Query("SELECT DISTINCT p.breed FROM Post p WHERE p.animalType = :animalType")
    List<String> findBreedsByAnimalType(@Param("animalType") String animalType);
}
