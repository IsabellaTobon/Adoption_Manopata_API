package com.example.adoption_Manopata.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class BCryptPasswordGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String rawPassword = "password123";  // Password to encrypt
        String encodedPassword = encoder.encode(rawPassword);
        System.out.println(encodedPassword);  // This is the encrypted password
    }
}
