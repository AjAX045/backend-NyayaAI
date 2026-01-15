package com.ajay.nyayaAI.controller;

import com.ajay.nyayaAI.model.Fir;
import com.ajay.nyayaAI.service.FirService;

import java.time.LocalDate;
import java.util.Map;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/firs")
@CrossOrigin(origins = "*")
public class FirController {

    private final FirService firService;

    public FirController(FirService firService) {
        this.firService = firService;
    }

    @PostMapping
    public Fir createFir(@RequestBody Map<String, Object> firData) {
        Fir fir = new Fir();

        // Map FIR form fields
        fir.setComplainantName((String) firData.get("complainantName"));
        fir.setContactNumber((String) firData.get("contactNumber"));
        fir.setAddress((String) firData.get("address"));
        fir.setIncidentDate(LocalDate.parse((String) firData.get("incidentDate")));
        fir.setIncidentTime((String) firData.get("incidentTime"));
        fir.setLocation((String) firData.get("location"));
        fir.setIncidentType((String) firData.get("incidentType"));
        fir.setComplaintText((String) firData.get("complaintText"));

        // Save only accepted/corrected predicted sections
        Object predictedSections = firData.get("predictedSections");
        if (predictedSections != null) {
            // Convert to string (JSON) before saving
            fir.setPredictedSections(predictedSections.toString());
        }

        return firService.saveFir(fir);
    }

}
