package com.ajay.nyayaAI.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;
import java.io.InputStream;
import java.util.*;

@Service
public class SectionService {
    private List<Map<String, String>> sections;

    private final GeminiService geminiService;

    public SectionService(GeminiService geminiService) {
        this.geminiService = geminiService;
    }

    @PostConstruct
    public void loadSections() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        InputStream is = getClass().getResourceAsStream("/data/bns_sections.json");
        sections = mapper.readValue(is, new TypeReference<List<Map<String, String>>>() {});
    }

    public String searchSection(String query) throws Exception {
        query = query.toLowerCase();

        // Try exact match by section number or title
        for (Map<String, String> section : sections) {
            if (section.get("section").equalsIgnoreCase(query) ||
                section.get("cleaned_title").toLowerCase().contains(query)) {

                String lawText = "Section " + section.get("section") + ": " + section.get("title") + "\n" + section.get("description");
                String prompt = "Summarize the following BNS law in simple terms for this query: " + query + "\n\n" + lawText;

                return geminiService.getGeminiResponse(prompt);
            }
        }

        // If not found, return fallback
        return "No matching section found for your query.";
    }
}
