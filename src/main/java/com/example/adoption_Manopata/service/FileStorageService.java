package com.example.adoption_Manopata.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class FileStorageService {
    @Value("${file.upload-dir}")
    private String uploadDir;

    public String storeFile(MultipartFile file) throws IOException {
        // Log para verificar la ruta de almacenamiento y el nombre del archivo
        System.out.println("Almacenando archivo en: " + uploadDir + " con nombre: " + file.getOriginalFilename());

        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path uploadPath = Paths.get(uploadDir);

        // Crear el directorio si no existe
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
            System.out.println("Directorio de subida creado: " + uploadPath.toString());  // Log si el directorio se crea
        }

        // Guardar el archivo en el servidor
        Path filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath);
        System.out.println("Archivo guardado en: " + filePath.toString());  // Log para confirmar que el archivo se ha guardado

        return fileName;
    }
}
