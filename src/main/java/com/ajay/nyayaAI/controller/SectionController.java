package com.ajay.nyayaAI.controller;



import com.ajay.nyayaAI.service.SectionService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sections")
@CrossOrigin(origins = "*")
public class SectionController {

    private final SectionService sectionService;

    public SectionController(SectionService sectionService) {
        this.sectionService = sectionService;
    }

    @GetMapping("/search")
    public String searchSection(@RequestParam String query) throws Exception {
        return sectionService.searchSection(query);
    }
}
