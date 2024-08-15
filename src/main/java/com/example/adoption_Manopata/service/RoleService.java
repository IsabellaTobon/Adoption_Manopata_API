package com.example.adoption_Manopata.service;

import com.example.adoption_Manopata.model.Role;
import com.example.adoption_Manopata.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;


public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    public Optional<Role> getRoleByName(String name) {
        return roleRepository.findById(name);
    }

    public Role createRole(Role role) {
        return roleRepository.save(role);
    }
}
