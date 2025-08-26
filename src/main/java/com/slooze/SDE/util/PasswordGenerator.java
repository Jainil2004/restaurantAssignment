package com.slooze.SDE.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordGenerator {
    public static void main(String[] args) {
        String raw = "help";
        String hash = new BCryptPasswordEncoder().encode(raw);
        System.out.println("BCrypt hash for " + raw + " = " + hash);
        String hashFromDb = "$2a$10$LO0IjklBKQbCydU.NrlQ0eeiwnkjMk5aKpVylL9itjEa3RmpEpMqW";
        System.out.println(new BCryptPasswordEncoder().matches(raw, hashFromDb));
    }
}

