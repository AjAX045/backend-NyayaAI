package com.ajay.nyayaAI.controller;

import com.ajay.nyayaAI.model.Accused;
import com.ajay.nyayaAI.model.Fir;
import com.ajay.nyayaAI.service.FirService;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;


@RestController
@RequestMapping("/api/firs")
@CrossOrigin(origins = "*")
public class FirController {

    private final FirService firService;

    public FirController(FirService firService) {
        this.firService = firService;
    }

    @PostMapping
    public Fir createFir(@RequestBody Fir fir) {

        // Attach FIR reference to each accused
        if (fir.getAccusedList() != null) {
            for (Accused accused : fir.getAccusedList()) {
                accused.setFir(fir);
            }
        }

        return firService.saveFir(fir);
    }

@GetMapping("/{id}/pdf")
public ResponseEntity<byte[]> generatePdf(@PathVariable Long id) {

    Fir fir = firService.getFirById(id);

    RestTemplate restTemplate = new RestTemplate();

    String pythonServiceUrl = "http://127.0.0.1:8000/generate-fir-pdf" ;

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    HttpEntity<Fir> request = new HttpEntity<>(fir, headers);

    ResponseEntity<byte[]> response =
            restTemplate.postForEntity(pythonServiceUrl, request, byte[].class);

    return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=FIR_" + fir.getId() + ".pdf")
            .contentType(MediaType.APPLICATION_PDF)
            .body(response.getBody());
}
}