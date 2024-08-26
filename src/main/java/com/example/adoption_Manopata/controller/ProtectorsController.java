package com.example.adoption_Manopata.controller;

import com.example.adoption_Manopata.model.Protector;
import com.example.adoption_Manopata.service.ProtectorsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/protectors")
public class ProtectorsController {

    @Autowired
    private ProtectorsService protectorsService;

    // Obtain all protectors
    @GetMapping
    public List<Protector> getAllProtectors() {
        return protectorsService.getAllProtectors();
    }

    // Obtain protector by id
    @GetMapping("/{id}")
    public ResponseEntity<Protector> getProtectorById(@PathVariable Long id) {
        Optional<Protector> protectors = protectorsService.getProtectorById(id);
        return protectors.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Create a new protector
    @PostMapping
    public Protector createProtector(@RequestBody Protector protector) {
        return protectorsService.createProtector(protector);
    }

    // Update a protector
    @PutMapping("/{id}")
    public ResponseEntity<Protector> updateProtector(@PathVariable Long id, @RequestBody Protector protectorDetails) {
        Protector updatedProtector = protectorsService.updateProtector(id, protectorDetails);
        return ResponseEntity.ok(updatedProtector);
    }

    // Delete a protector
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProtector(@PathVariable Long id) {
        protectorsService.deleteProtector(id);
        return ResponseEntity.ok().build();
    }
}
