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

    private static final String SECRET_KEY = "super-secret-change-me";

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
