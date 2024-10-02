package com.example.adoption_Manopata.repository;

import com.example.adoption_Manopata.model.Protector;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;


public interface ProtectorsRepository extends JpaRepository<Protector, Long> {

    Optional<Protector> findByName(String name);

    @Query("SELECT DISTINCT p.province FROM Protector p")
    List<String> findAllProvinces();

    @Query("SELECT DISTINCT p.city FROM Protector p WHERE p.province = ?1")
    List<String> findCitiesByProvince(String province);
}
