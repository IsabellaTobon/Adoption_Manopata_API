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

    // GET ALL PROTECTORS
    public List<Protector> getAllProtectors() {
        return protectorsRepository.findAll();
    }

    // OBTAIN ALL PROVINCES
    public List<String> getAllProvinces() {
        return protectorsRepository.findAllProvinces();
    }

    // GET CITIES BY PROVINCE
    public List<String> getCitiesByProvince(String province) {
        return protectorsRepository.findCitiesByProvince(province);
    }

    // SEARCH PROTECTOR BY ID
    public Optional<Protector> getProtectorById(Long id) {
        return protectorsRepository.findById(id);
    }

    // SEARCH PROTECTOR BY NAME
    public Optional<Protector> getProtectorByName(String name) {
        return protectorsRepository.findByName(name);
    }

    // CREATE NEW PROTECTOR
    public Protector createProtector(Protector protector) {
        if (protector.getPhoto() == null || protector.getPhoto().isEmpty()) {
            protector.setPhoto("/images/default-protector.jpg");
        }
        return protectorsRepository.save(protector);
    }

    // UPDATE PROTECTOR
    public Protector updateProtector(Long id, Protector protectorDetails) {
        return protectorsRepository.findById(id)
                .map(protector -> {
                    protector.setName(protectorDetails.getName());
                    protector.setDescription(protectorDetails.getDescription());
                    protector.setPhone(protectorDetails.getPhone());
                    protector.setEmail(protectorDetails.getEmail());
                    protector.setCity(protectorDetails.getCity());
                    protector.setProvince(protectorDetails.getProvince());
                    protector.setWeb_site(protectorDetails.getWeb_site());
                    if (protectorDetails.getPhoto() != null && !protectorDetails.getPhoto().isEmpty()) {
                        protector.setPhoto(protectorDetails.getPhoto());
                    }
                    return protectorsRepository.save(protector);
                }).orElseThrow(() -> new RuntimeException("Protector not found"));
    }

    // DELETE PROTECTOR
    public void deleteProtector(Long id) {
        protectorsRepository.deleteById(id);
    }
}
