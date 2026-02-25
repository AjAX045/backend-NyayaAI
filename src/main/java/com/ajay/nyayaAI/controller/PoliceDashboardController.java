package com.ajay.nyayaAI.controller;

import com.ajay.nyayaAI.dto.PoliceDashboardStatsDto;
import java.util.List;
import java.util.stream.Collectors;
import com.ajay.nyayaAI.dto.RecentFirDto;
import com.ajay.nyayaAI.model.Fir;
import com.ajay.nyayaAI.repository.DashboardRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import com.ajay.nyayaAI.security.CustomUserDetails;


import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/police/dashboard")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class PoliceDashboardController {

    private final DashboardRepo firRepository;

    public PoliceDashboardController(DashboardRepo firRepository) {
        this.firRepository = firRepository;
    }

    @GetMapping("/stats")
    public PoliceDashboardStatsDto getDashboardStats() {

    	String station = getLoggedInStation();

    	long total = firRepository.countByPoliceStation(station);
    	long pending = firRepository.countByPoliceStationAndStatus(station, "PENDING");
    	long solved = firRepository.countByPoliceStationAndStatus(station, "SOLVED");



        return new PoliceDashboardStatsDto(total, pending, solved);
    }
    
    @GetMapping("/recent-firs")
    public List<RecentFirDto> getRecentFirs() {

    	String station = getLoggedInStation();

    	List<Fir> firs =
    	    firRepository.findTop5ByPoliceStationOrderByCreatedAtDesc(station);


        return firs.stream()
                .map(fir -> new RecentFirDto(
                        fir.getId(),
                        fir.getComplainantName(),
                        fir.getIncidentType(),
                        fir.getStatus(),
                        fir.getCreatedAt()
                ))
                .collect(Collectors.toList());
    }
    
    @PatchMapping("/fir/{id}/status")
    public ResponseEntity<Fir> updateFirStatus(
            @PathVariable Long id,
            @RequestParam String status) {

        Fir fir = firRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("FIR not found"));

        // Update status
        fir.setStatus(status.toUpperCase()); // "PENDING" or "SOLVED"
        Fir updatedFir = firRepository.save(fir);

        return ResponseEntity.ok(updatedFir);
    }
    
    @GetMapping("/all-firs")
    public List<Fir> getAllFirs() {
        // Optional: you can sort by createdAt descending
       // return firRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
    	String station = getLoggedInStation();

    	return firRepository.findByPoliceStation(
    	        station,
    	        Sort.by(Sort.Direction.DESC, "createdAt")
    	);

    }
    
    private String getLoggedInStation() {
        CustomUserDetails userDetails =
            (CustomUserDetails) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        return userDetails.getPoliceStation();
    }




}
