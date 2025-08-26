package com.slooze.SDE.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class JwtService {

    private static final String SECRET_KEY = "8f9a3c62d0b74e4b927f1a86b4c53d7f13c5e9f8a4d2b1c6e7f0a9d8b3c5f7e1";

    public String generateToken(String username, String role, String country) {
        long nowMillies = System.currentTimeMillis();
        long expMillies = nowMillies + (1000 * 60 * 60);

        return Jwts.builder()
                .setSubject(username)
                .claim("role", role)
                .claim("country", country)
                .setIssuedAt(new Date(nowMillies))
                .setExpiration(new Date(expMillies))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    public Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build().parseClaimsJws(token).getBody();
    }
}
