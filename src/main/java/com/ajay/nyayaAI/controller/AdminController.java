package com.ajay.nyayaAI.controller;
//new
import com.ajay.nyayaAI.dto.CreateOfficerRequest;
import com.ajay.nyayaAI.dto.OfficerResponse;
import com.ajay.nyayaAI.service.OfficerService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final OfficerService officerService;

    public AdminController(OfficerService officerService) {
        this.officerService = officerService;
    }

    @PostMapping("/officers")
    public ResponseEntity<OfficerResponse> createOfficer(
            @RequestBody CreateOfficerRequest request) {

        OfficerResponse response =
                officerService.createOfficer(request);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/officers")
    public ResponseEntity<List<OfficerResponse>> getAllOfficers() {

        return ResponseEntity.ok(
                officerService.getAllActiveOfficers()
        );
    }

    @DeleteMapping("/officers/{id}")
    public ResponseEntity<String> deactivateOfficer(
            @PathVariable Long id) {

        officerService.deactivateOfficer(id);
        return ResponseEntity.ok("Officer deactivated successfully");
    }
}
