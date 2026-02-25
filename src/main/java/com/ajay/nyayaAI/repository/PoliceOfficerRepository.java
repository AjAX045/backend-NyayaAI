package com.ajay.nyayaAI.repository;
//login
import com.ajay.nyayaAI.model.PoliceOfficer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PoliceOfficerRepository 
        extends JpaRepository<PoliceOfficer, Long> {

    Optional<PoliceOfficer> findByUsername(String username);

    List<PoliceOfficer> findByActiveTrue();
}