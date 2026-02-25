package com.ajay.nyayaAI.service;

import org.springframework.stereotype.Service;
import com.ajay.nyayaAI.repository.LoginSessionRepository;

@Service
public class AuthService {

    private final LoginSessionRepository loginSessionRepository;

    public AuthService(LoginSessionRepository repo) {
        this.loginSessionRepository = repo;
    }

    public void validateToken(String token) {
        if (token == null || token.isBlank()) {
            throw new RuntimeException("Unauthorized");
        }

        loginSessionRepository
                .findByToken(token)
                .orElseThrow(() -> new RuntimeException("Unauthorized"));
    }
}
