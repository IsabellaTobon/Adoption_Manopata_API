package com.example.adoption_Manopata.service;

import com.example.adoption_Manopata.model.Protector;
import com.example.adoption_Manopata.repository.ProtectorsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProtectorsService {

    @Autowired
    private ProtectorsRepository protectorsRepository;

    public List<Protector> getAllProtectors() {
        return protectorsRepository.findAll();
    }

    public Optional<Protector> getProtectorById(Long id) {
        return protectorsRepository.findById(id);
    }

    public Protector createProtector(Protector protector) {
        if (protector.getPhoto() == null || protector.getPhoto().isEmpty()) {
            protector.setPhoto("/images/default-protector.jpg");
        }
        return protectorsRepository.save(protector);
    }

    public Protector updateProtector(Long id, Protector protectorDetails) {
        return protectorsRepository.findById(id)
                .map(protector -> {
                    protector.setName(protectorDetails.getName());
                    protector.setDescription(protectorDetails.getDescription());
                    protector.setPhone(protectorDetails.getPhone());
                    protector.setEmail(protectorDetails.getEmail());
                    protector.setCity(protectorDetails.getCity());
                    protector.setProvince(protectorDetails.getProvince());
                    protector.setAddress(protectorDetails.getAddress());
                    protector.setWebSite(protectorDetails.getWebSite());
                    if (protectorDetails.getPhoto() != null && !protectorDetails.getPhoto().isEmpty()) {
                        protector.setPhoto(protectorDetails.getPhoto());
                    }
                    return protectorsRepository.save(protector);
                }).orElseThrow(() -> new RuntimeException("Protector not found"));
    }

    public void deleteProtector(Long id) {
        protectorsRepository.deleteById(id);
    }
}
