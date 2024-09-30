package com.example.adoption_Manopata.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        final String authorizationHeader = request.getHeader("Authorization");

        // Verificar si el encabezado Authorization está presente y si comienza con "Bearer "
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            System.out.println("No se encontró el header Authorization o no empieza con Bearer");
            chain.doFilter(request, response);  // Continuar el filtro sin autenticar
            return;
        }

        // Extraer el token JWT del encabezado
        String jwt = authorizationHeader.substring(7);
        String username = jwtUtil.extractNickname(jwt);
        System.out.println("JWT recibido: " + jwt);
        System.out.println("Usuario extraído del JWT: " + username);

        // Validar si el usuario no está ya autenticado en el contexto de seguridad
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            UserDetails userDetails;
            try {
                // Cargar los detalles del usuario desde la base de datos
                userDetails = this.userDetailsService.loadUserByUsername(username);
            } catch (UsernameNotFoundException e) {
                System.out.println("Usuario no encontrado: " + username);
                chain.doFilter(request, response);  // Continuar el filtro sin autenticar
                return;
            }

            // Validar el JWT y los detalles del usuario
            if (jwtUtil.validateToken(jwt, userDetails)) {
                System.out.println("JWT validado correctamente para el usuario: " + username);

                // Crear el token de autenticación
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());

                // Asociar los detalles de la solicitud HTTP
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Configurar el contexto de seguridad para el usuario autenticado
                SecurityContextHolder.getContext().setAuthentication(authToken);
            } else {
                System.out.println("JWT no válido o UserDetails es nulo para el usuario: " + username);
            }
        } else {
            System.out.println("Usuario ya autenticado o no encontrado");
        }

        // Continuar con el siguiente filtro en la cadena
        chain.doFilter(request, response);
    }
}
