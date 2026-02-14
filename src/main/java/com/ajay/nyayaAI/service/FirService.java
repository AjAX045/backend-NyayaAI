package com.ajay.nyayaAI.service;

import com.ajay.nyayaAI.model.Accused;
import com.ajay.nyayaAI.model.Fir;
import com.ajay.nyayaAI.repository.FirRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FirService {

    private final FirRepository firRepository;

    public FirService(FirRepository firRepository) {
        this.firRepository = firRepository;
    }

    public Fir saveFir(Fir fir) {

        // ===============================
        // Attach FIR reference to accused
        // ===============================
        List<Accused> accusedList = fir.getAccusedList();

        if (accusedList != null && !accusedList.isEmpty()) {
            for (Accused accused : accusedList) {
                accused.setFir(fir);
            }
        }

        // ===============================
        // Additional Safety Defaults
        // ===============================

        if (fir.getStatus() == null || fir.getStatus().isBlank()) {
            fir.setStatus("PENDING");
        }

        // Year will auto set in @PrePersist in entity

        return firRepository.save(fir);
 
    }

    public Fir getFirById(Long id) {
    return firRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("FIR not found"));
}

}

