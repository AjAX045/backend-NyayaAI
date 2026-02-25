package com.ajay.nyayaAI.service;
//new
import com.ajay.nyayaAI.dto.CreateOfficerRequest;
import com.ajay.nyayaAI.dto.OfficerResponse;
import com.ajay.nyayaAI.model.PoliceOfficer;
import com.ajay.nyayaAI.repository.PoliceOfficerRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OfficerService {

    private final PoliceOfficerRepository repository;
    private final PasswordEncoder passwordEncoder;

    public OfficerService(PoliceOfficerRepository repository,
                          PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    public OfficerResponse createOfficer(CreateOfficerRequest request) {

        if (repository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }

        PoliceOfficer officer = new PoliceOfficer();
        officer.setUsername(request.getUsername());
        officer.setPassword(passwordEncoder.encode(request.getPassword()));
        officer.setPoliceStation(request.getPoliceStation());
        officer.setRole(request.getRole());
        officer.setActive(true);

        PoliceOfficer savedOfficer = repository.save(officer);

        return mapToResponse(savedOfficer);
    }

    public List<OfficerResponse> getAllActiveOfficers() {
        return repository.findByActiveTrue()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public void deactivateOfficer(Long id) {

        PoliceOfficer officer = repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Officer not found"));

        officer.setActive(false);
        repository.save(officer);
    }

    private OfficerResponse mapToResponse(PoliceOfficer officer) {

        OfficerResponse response = new OfficerResponse();
        response.setId(officer.getId());
        response.setUsername(officer.getUsername());
        response.setPoliceStation(officer.getPoliceStation());
        response.setRole(officer.getRole());

        return response;
    }
}
