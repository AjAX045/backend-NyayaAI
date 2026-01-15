package com.ajay.nyayaAI.controller;

import com.ajay.nyayaAI.dto.SectionAnalysisResponse;
import com.ajay.nyayaAI.service.LawService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/laws")
@CrossOrigin("*")
public class LawController {

    @Autowired
    private LawService lawService;

    /**
     * 🔹 ML-based FIR → Legal Section Prediction
     */
    @PostMapping("/predict")
    public ResponseEntity<?> predictLawSections(@RequestBody Map<String, String> body) {

        String complaint = body.get("complaint");

        if (complaint == null || complaint.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Complaint text is required");
        }

        List<SectionAnalysisResponse> response =
                lawService.predictSections(complaint);

        return ResponseEntity.ok(response);
    }

    /**
     * 🔹 Gemini-based Law Explanation
     */
    @GetMapping("/ask")
    public ResponseEntity<String> askLaw(@RequestParam String query) {

        if (query == null || query.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Query is required");
        }

        String response = lawService.getLawInfo(query);
        return ResponseEntity.ok(response);
    }
}
