package com.example.adoption_Manopata.controller;

import com.example.adoption_Manopata.model.Protectors;
import com.example.adoption_Manopata.service.ProtectorsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/protectors")
public class ProtectorsController {

    @Autowired
    private ProtectorsService protectorsService;

    // Obtain all protectors
    @GetMapping
    public List<Protectors> getAllProtectors() {
        return protectorsService.getAllProtectors();
    }

    // Obtain protector by id
    @GetMapping("/{id}")
    public ResponseEntity<Protectors> getProtectorById(@PathVariable UUID id) {
        Optional<Protectors> protectors = protectorsService.getProtectorById(id);
        return protectors.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Create a new protector
    @PostMapping
    public Protectors createProtector(@RequestBody Protectors protectors) {
        return protectorsService.createProtector(protectors);
    }

    // Update a protector
    @PutMapping("/{id}")
    public ResponseEntity<Protectors> updateProtector(@PathVariable UUID id, @RequestBody Protectors protectorDetails) {
        Protectors updatedProtector = protectorsService.updateProtector(id, protectorDetails);
        return ResponseEntity.ok(updatedProtector);
    }

    // Delete a protector
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProtector(@PathVariable UUID id) {
        protectorsService.deleteProtector(id);
        return ResponseEntity.ok().build();
    }
}
