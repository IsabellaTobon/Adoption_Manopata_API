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
                        // Permitir acceso público a las rutas de posts y datos de provincias, ciudades y razas
                        .requestMatchers("/api/post", "/api/post/**").permitAll()  // Todos pueden ver posts
                        .requestMatchers("/api/post/provinces", "/api/post/cities", "/api/post/breeds").permitAll()  // Provincias, ciudades y razas públicas
                        .requestMatchers("/auth/**").permitAll()  // Acceso público a la autenticación
                        .requestMatchers("/uploads/**").permitAll()  // Permitir acceso público a las imágenes
                        .requestMatchers("/images/**").permitAll()  // Permitir acceso a imágenes

                        // Seguridad para los comentarios
                        .requestMatchers("/api/comments").permitAll()  // Permitir obtener comentarios (GET)
                        .requestMatchers("/api/comments/create").authenticated()  // Requerir autenticación para crear comentarios

                        // Rutas que requieren autenticación
                        .requestMatchers("/api/post/create", "/api/post/**/update", "/api/post/**/delete").authenticated()
                        .requestMatchers("/api/admin/**").hasAuthority("ADMIN")  // Solo admin puede acceder a rutas admin
                        .anyRequest().authenticated()  // Cualquier otra ruta requiere autenticación
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );

        // Solo habilitar el filtro JWT para rutas autenticadas
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
