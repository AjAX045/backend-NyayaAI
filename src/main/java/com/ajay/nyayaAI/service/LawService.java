package com.ajay.nyayaAI.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class LawService {

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    // Correct Gemini endpoint
    private static final String GEMINI_URL =
            "https://generativelanguage.googleapis.com/v1/models/gemini-1.5-pro:generateContent";

    public String getLawInfo(String query) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // ✅ Use Bearer token for authentication
            headers.setBearerAuth(geminiApiKey);

            // Build the prompt
            String promptText = "Explain this in simple words for public legal awareness: " + query;

            // Build request body
            Map<String, Object> part = Map.of("text", promptText);
            Map<String, Object> contents = Map.of("parts", List.of(part));
            Map<String, Object> requestBody = Map.of("contents", List.of(contents));

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            // ✅ Correct call (no need to append API key to URL)
            ResponseEntity<String> response = restTemplate.postForEntity(GEMINI_URL, entity, String.class);

            // Parse the response
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.getBody());

            JsonNode candidates = root.path("candidates");
            if (candidates.isArray() && candidates.size() > 0) {
                JsonNode firstCandidate = candidates.get(0);
                JsonNode partsNode = firstCandidate.path("content").path("parts");
                if (partsNode.isArray() && partsNode.size() > 0) {
                    return partsNode.get(0).path("text").asText();
                }
            }

            return "No valid response from Gemini";

        } catch (Exception e) {
            return "Error fetching law info: " + e.getMessage();
        }
    }
}
