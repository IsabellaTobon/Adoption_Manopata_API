package com.example.adoption_Manopata.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class BCryptPasswordGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String rawPassword = "password123";  // PASSWORD TO ENCRYPT
        String encodedPassword = encoder.encode(rawPassword);
        System.out.println(encodedPassword);  // THIS IS THE ENCRYPTED PASSWORD
    }
}
