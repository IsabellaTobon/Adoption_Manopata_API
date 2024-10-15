package com.example.adoption_Manopata.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final UserDetailsService userDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(UserDetailsService userDetailsService, JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.userDetailsService = userDetailsService;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        // ALLOW PUBLIC ACCESS TO POST ROUTES AND DATA FOR PROVINCES, CITIES AND RACES
                        .requestMatchers("/api/post", "/api/post/**").permitAll()  // Everyone can see posts
                        .requestMatchers("/api/post/provinces", "/api/post/cities", "/api/post/breeds").permitAll()  // PROVINCES, CITIES AND BREEDS PUBLIC
                        .requestMatchers("/auth/**").permitAll()  // PUBLIC AUTHENTICATION
                        .requestMatchers("/uploads/**").permitAll()  // ALLOW PUBLIC ACCESS TO UPLOADED IMAGES
                        .requestMatchers("/images/**").permitAll()  // ALLOW ACCESS TO IMAGES
                        .requestMatchers("/protectors/**").permitAll() // PROTECTORS PUBLIC

                        // Seguridad para los comentarios
                        .requestMatchers("/api/comments").permitAll()  // ALLOW OBTAIN COMMENTS
                        .requestMatchers("/api/comments/create").authenticated()  // REQUIRE AUTHENTICATION TO CREATE COMMENTS

                        // Seguridad para los mensajes
                        .requestMatchers("/api/messages/**").authenticated()  // REQUIRES AUTHENTICATION TO SEND MESSAGES
                        // Rutas relacionadas con el usuario
                        .requestMatchers("/api/user/profile/**").authenticated()  // ONLY AUTHENTICATED USERS CAN VIEW AND EDIT YOUR PROFILE
                        .requestMatchers("/api/user/{id}/profile-image").authenticated()  // REQUIRES AUTHENTICATION TO UPDATE PROFILE IMAGE
                        .requestMatchers("/api/user/delete-account").authenticated()  // REQUIRES AUTHENTICATION TO DELETE ACCOUNT
                        .requestMatchers("/api/user/**").authenticated()  // OTHER USER OPERATIONS REQUIRE AUTHENTICATION

                        // Rutas que requieren autenticación
                        .requestMatchers("/api/post/create", "/api/post/**/update", "/api/post/**/delete").authenticated()
                        .requestMatchers("/api/admin/**").hasAuthority("ADMIN")  // ONLY ADMIN CAN ACCESS ADMIN ROUTES
                        .anyRequest().authenticated()  // ANY OTHER ROUTE REQUIRES AUTHENTICATION
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );

        // ONLY ENABLE JWT FILTER FOR AUTHENTICATED ROUTES
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }
}
