package com.ajay.nyayaAI.controller;

import com.ajay.nyayaAI.dto.PoliceDashboardStatsDto;
import java.util.List;
import java.util.stream.Collectors;
import com.ajay.nyayaAI.dto.RecentFirDto;
import com.ajay.nyayaAI.model.Fir;
import com.ajay.nyayaAI.repository.DashboardRepo;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/police/dashboard")
@CrossOrigin(origins = "http://localhost:3000")
public class PoliceDashboardController {

    private final DashboardRepo firRepository;

    public PoliceDashboardController(DashboardRepo firRepository) {
        this.firRepository = firRepository;
    }

    @GetMapping("/stats")
    public PoliceDashboardStatsDto getDashboardStats() {

        long total = firRepository.count();
        long pending = firRepository.countByStatus("PENDING");
        long solved = firRepository.countByStatus("SOLVED");

        return new PoliceDashboardStatsDto(total, pending, solved);
    }
    
    @GetMapping("/recent-firs")
    public List<RecentFirDto> getRecentFirs() {

        List<Fir> firs = firRepository.findTop5ByOrderByCreatedAtDesc();

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
        return firRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
    }



}
