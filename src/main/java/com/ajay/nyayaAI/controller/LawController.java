package com.ajay.nyayaAI.controller;

import com.ajay.nyayaAI.service.LawService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/laws")
@CrossOrigin("*")
public class LawController {

    @Autowired
    private LawService lawService;

    @GetMapping("/ask")
    public String askLaw(@RequestParam String query) {
        return lawService.getLawInfo(query);
    }
}
