package com.example.adoption_Manopata.controller;

import com.example.adoption_Manopata.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/files")
public class Filecontroller {
    private FileStorageService fileStorageService;

    // Inyección de dependencias a través del constructor
    @Autowired
    public Filecontroller(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }


    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "No se ha proporcionado ningún archivo"));
        }
        try {
            String fileName = fileStorageService.storeFile(file);
            Map<String, String> response = new HashMap<>();
            response.put("fileName", fileName);
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            return ResponseEntity.status(500).body(Map.of("message", "Failed to upload file: " + e.getMessage()));
        }
    }
}
