package com.ajay.nyayaAI.service;

import com.ajay.nyayaAI.dto.SectionAnalysisResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class LawService {

    /* ============================
       🔹 COMMON
       ============================ */

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RestTemplate restTemplate = new RestTemplate();

    /* ============================
       🔹 ML SERVICE CONFIG
       ============================ */

    private static final String ML_SERVICE_URL = "http://localhost:8000/predict";

    public List<SectionAnalysisResponse> predictSections(String complaint) {

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("complaint", complaint);
            requestBody.put("top_k", 5);

            HttpEntity<Map<String, Object>> entity =
                    new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    ML_SERVICE_URL,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            List<Map<String, Object>> rawResults =
                    objectMapper.readValue(
                            response.getBody(),
                            new TypeReference<List<Map<String, Object>>>() {}
                    );

            List<SectionAnalysisResponse> results = new ArrayList<>();

            for (Map<String, Object> raw : rawResults) {

                SectionAnalysisResponse dto = new SectionAnalysisResponse();

                dto.setSection(String.valueOf(raw.get("section")));
                dto.setTitle(String.valueOf(raw.get("section_name")));
                dto.setCategory("IPC");
                dto.setDescription(String.valueOf(raw.get("description")));

                double similarity =
                        Double.parseDouble(String.valueOf(raw.get("similarity")));

                dto.setMatchPercentage((int) Math.round(similarity * 100));
                dto.setPunishment("As per IPC");

                results.add(dto);
            }

            return results;

        } catch (Exception e) {
            throw new RuntimeException("Error calling ML service: " + e.getMessage(), e);
        }
    }

    /* ============================
       🔹 GEMINI CONFIG
       ============================ */

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    private static final String GEMINI_URL =
            "https://generativelanguage.googleapis.com/v1/models/gemini-2.5-flash:generateContent";

    public String getLawInfo(String query) {

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            String prompt =
                    "Explain this Indian law or legal term in simple, public-friendly language: " + query;

            Map<String, Object> part = Map.of("text", prompt);
            Map<String, Object> content = Map.of("parts", List.of(part));
            Map<String, Object> requestBody = Map.of("contents", List.of(content));

            HttpEntity<Map<String, Object>> entity =
                    new HttpEntity<>(requestBody, headers);

            String fullUrl = GEMINI_URL + "?key=" + geminiApiKey;

            ResponseEntity<String> response =
                    restTemplate.postForEntity(fullUrl, entity, String.class);

            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode candidates = root.path("candidates");

            if (candidates.isArray() && candidates.size() > 0) {
                JsonNode parts =
                        candidates.get(0).path("content").path("parts");
                if (parts.isArray() && parts.size() > 0) {
                    return parts.get(0).path("text").asText();
                }
            }

            return "No valid response from Gemini";

        } catch (Exception e) {
            return "Error fetching law info: " + e.getMessage();
        }
    }
}
