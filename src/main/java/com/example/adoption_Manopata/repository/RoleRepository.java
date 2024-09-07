package com.example.adoption_Manopata.repository;

import com.example.adoption_Manopata.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository <Role, String> {
    Role findByName(String name);
}
