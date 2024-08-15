package com.example.adoption_Manopata.repository;

import com.example.adoption_Manopata.model.Protectors;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProtectorsRepository extends JpaRepository<Protectors, UUID> {
}
