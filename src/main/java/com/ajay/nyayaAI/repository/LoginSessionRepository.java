package com.ajay.nyayaAI.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ajay.nyayaAI.model.LoginSession;

public interface LoginSessionRepository extends JpaRepository<LoginSession, Long> {

    Optional<LoginSession> findByToken(String token);
}
