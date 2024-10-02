package com.example.adoption_Manopata.controller;

import com.example.adoption_Manopata.model.Protector;
import com.example.adoption_Manopata.service.ProtectorsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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
    public ResponseEntity<List<Protector>> getAllProtectors() {
        List<Protector> protectors = protectorsService.getAllProtectors();
        return ResponseEntity.ok(protectors);
    }
    // Obtener provincias
    @GetMapping("/provinces")
    public ResponseEntity<List<String>> getAllProvinces() {
        List<String> provinces = protectorsService.getAllProvinces();
        return ResponseEntity.ok(provinces);
    }

    // Obtener ciudades por provincia
    @GetMapping("/cities")
    public ResponseEntity<List<String>> getCities(@RequestParam String province) {
        List<String> cities = protectorsService.getCitiesByProvince(province);
        return ResponseEntity.ok(cities);
    }

    // Obtain protector by id
    @GetMapping("/{id}")
    public ResponseEntity<Protector> getProtectorById(@PathVariable Long id) {
        Optional<Protector> protectors = protectorsService.getProtectorById(id);
        return protectors.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<Protector> getProtectorByName(@PathVariable String name) { // New endpoint
        Optional<Protector> protector = protectorsService.getProtectorByName(name);
        return protector.map(ResponseEntity::ok)
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
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String loggedInUsername = userDetails.getUsername();

        Protector protector = protectorsService.getProtectorById(id)
                .orElseThrow(() -> new RuntimeException("Protector not found"));

        if (!protector.getEmail().equals(loggedInUsername)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        Protector updatedProtector = protectorsService.updateProtector(id, protectorDetails);
        return ResponseEntity.ok(updatedProtector);
    }

    // Delete a protector
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProtector(@PathVariable Long id) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String loggedInUsername = userDetails.getUsername();

        Protector protector = protectorsService.getProtectorById(id)
                .orElseThrow(() -> new RuntimeException("Protector not found"));

        if (!protector.getEmail().equals(loggedInUsername)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        protectorsService.deleteProtector(id);
        return ResponseEntity.ok().build();
    }
}
