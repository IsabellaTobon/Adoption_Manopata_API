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

        // VERIFICAR SI EL ENCABEZADO AUTHORIZATION ESTÁ PRESENTE Y SI COMIENZA CON "BEARER "
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            chain.doFilter(request, response);  // CONTINUE FILTER WITHOUT AUTHENTICATING
            return;
        }

        // EXTRACT JWT TOKEN FROM HEADER
        String jwt = authorizationHeader.substring(7);
        String username = jwtUtil.extractNickname(jwt);

        // VALIDATE IF THE USER IS NOT ALREADY AUTHENTICATED IN THE SECURITY CONTEXT
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            UserDetails userDetails;
            try {
                // LOAD USER DETAILS FROM DATABASE
                userDetails = this.userDetailsService.loadUserByUsername(username);
            } catch (UsernameNotFoundException e) {
                chain.doFilter(request, response);  // CONTINUE FILTER WITHOUT AUTHENTICATING
                return;
            }

            // VALIDATE THE JWT AND USER DETAILS
            if (jwtUtil.validateToken(jwt, userDetails)) {
                System.out.println("JWT validado correctamente para el usuario: " + username);

                // CREATE TOKEN AUTHENTICATION
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());

                // ASOCIAR LOS DETALLES DE LA SOLICITUD HTTP
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // CONFIGURE THE SECURITY CONTEXT FOR THE AUTHENTICATED USER
                SecurityContextHolder.getContext().setAuthentication(authToken);
            } else {
                System.out.println("JWT no válido o UserDetails es nulo para el usuario: " + username);
            }
        } else {
            System.out.println("Usuario ya autenticado o no encontrado");
        }

        chain.doFilter(request, response);
    }
}
