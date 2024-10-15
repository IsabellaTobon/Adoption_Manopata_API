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

    // BASE64 ENCODED SECRET KEY FOR SIGNING THE TOKEN
    private final String SECRET_KEY_BASE64 = "qsnwZKi/aBxrrh1ATH3UoZx62I9Lkx7JgPxaCXuqSC8=";


    // METHOD TO DECODE THE KEY AND OBTAIN THE SIGNATURE KEY
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(Base64.getDecoder().decode(SECRET_KEY_BASE64));
    }

    // METHOD TO GENERATE A JWT TOKEN
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

    // METHOD TO GENERATE A JWT TOKEN WITH EMAIL
    public String generateTokenWithEmail(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // Token válido por 1 hora
                .signWith(getSigningKey())
                .compact();
    }

    // METHOD TO GENERATE A JWT TOKEN FOR PASSWORD RESET
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

    // METHOD TO EXTRACT USERNAME FROM JWT TOKEN
    public String extractNickname(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // METHOD TO RESOLVE THE TOKEN FROM THE REQUEST
    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    // METHOD TO EXTRACT ANY TYPE OF CLAIM FROM THE JWT TOKEN
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // METHOD TO EXTRACT ALL CLAIMS FROM THE JWT TOKEN
    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(getSigningKey()) // SET THE SIGNING KEY
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // METHOD TO VALIDATE THE JWT TOKEN
    public boolean validateToken(String token, UserDetails userDetails) {
        final String nickname = extractNickname(token);
        return (nickname.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    // METHOD TO CHECK IF THE TOKEN HAS EXPIRED
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // METHOD TO EXTRACT THE EXPIRATION DATE FROM THE JWT TOKEN
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

}
