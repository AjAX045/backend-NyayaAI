package com.ajay.nyayaAI.service;


import com.ajay.nyayaAI.model.Fir;
import com.ajay.nyayaAI.repository.FirRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
@Service

public class FirService {

    private final FirRepository firRepository;

    public FirService(FirRepository firRepository) {
        this.firRepository = firRepository;
    }

    public Fir saveFir(Fir fir) {
        return firRepository.save(fir);
    }
}
