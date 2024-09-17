package com.example.adoption_Manopata.security;

import com.example.adoption_Manopata.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;


import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class JwtUtil {

    // Base64 encoded secret key for signing the token
    private final String SECRET_KEY_BASE64 = "qsnwZKi/aBxrrh1ATH3UoZx62I9Lkx7JgPxaCXuqSC8=";


    // Method to decode the key and obtain the signature key
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(Base64.getDecoder().decode(SECRET_KEY_BASE64));
    }

    // Method to generate a JWT token
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = Map.of("roles", userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));

        return Jwts.builder()
                .claims(claims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // Token expires in 10 hours
                .signWith(getSigningKey())
                .compact();
    }

    // Method to generate a JWT token with email
    public String generateTokenWithEmail(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // Token válido por 1 hora
                .signWith(getSigningKey())
                .compact();
    }

    // Method to generate a JWT token for password reset
    public String generatePasswordResetToken(User user) {
        Map<String, Object> claims = Map.of("email", user.getEmail());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject("password_reset")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // Token expira en 1 hora
                .signWith(getSigningKey())
                .compact();
    }

    // Method to extract username from JWT token
    public String extractNickname(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Method to resolve the token from the request
    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    // Method to extract any type of claim from the JWT token
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // Method to extract all claims from the JWT token
    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(getSigningKey()) // Set the signing key
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Method to validate the JWT token
    public boolean validateToken(String token, UserDetails userDetails) {
        final String nickname = extractNickname(token);
        return (nickname.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    // Method to check if the token has expired
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // Method to extract the expiration date from the JWT token
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

}
