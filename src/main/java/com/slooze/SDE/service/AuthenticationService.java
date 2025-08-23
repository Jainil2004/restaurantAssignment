package com.slooze.SDE.service;

import com.slooze.SDE.model.User;
import com.slooze.SDE.repository.UserRepository;
import io.swagger.v3.oas.annotations.servers.Server;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public String login(String username, String password) {
        User user = userRepository.findUserByName(username).orElseThrow(() -> new RuntimeException("user not found"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("invalid credentials");
        }

        return jwtService.generateToken(
                user.getName(), user.getRole().name(), user.getCountry().name()
        );

    }

}
