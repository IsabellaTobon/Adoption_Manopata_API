package com.example.adoption_Manopata.repository;

import com.example.adoption_Manopata.model.Protector;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface ProtectorsRepository extends JpaRepository<Protector, Long> {

    List<Protector> findAll();
    Optional<Protector> findById(Long id);
    Optional<Protector> findByName(String name);

}
