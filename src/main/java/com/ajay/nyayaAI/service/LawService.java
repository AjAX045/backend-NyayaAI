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
       🔹 COMMON CONFIG
       ============================ */

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RestTemplate restTemplate = new RestTemplate();

    /* ============================
       🔹 ML SERVICE CONFIG  (⚠️ UNCHANGED)
       ============================ */

    private static final String ML_SERVICE_URL = "http://localhost:8000/predict";

    public List<SectionAnalysisResponse> predictSections(String complaint) {

        if (complaint == null || complaint.isBlank()) {
            throw new IllegalArgumentException("Complaint text cannot be empty");
        }

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

            if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
                throw new RuntimeException("Invalid response from ML service");
            }

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

                String fullDescription = String.valueOf(raw.get("description"));
                dto.setDescription(summarizeDescription(fullDescription));

                double similarity =
                        Double.parseDouble(String.valueOf(raw.get("similarity")));
                dto.setMatchPercentage((int) Math.round(similarity * 100));

                dto.setPunishment("As per BNS");

                results.add(dto);
            }

            return results;

        } catch (Exception e) {
            throw new RuntimeException("Error calling ML service", e);
        }
    }

    /* ============================
       🔹 DESCRIPTION SUMMARIZER
       ============================ */

    private String summarizeDescription(String description) {

        if (description == null || description.isBlank()) {
            return "";
        }

        // Normalize whitespace
        description = description.replaceAll("\\s+", " ").trim();

        // Split by sentence boundary
        String[] sentences = description.split("[.;]");

        String summary = sentences[0];

        // Limit word count
        int maxWords = 40;
        String[] words = summary.split(" ");

        if (words.length > maxWords) {
            summary = String.join(" ",
                    Arrays.copyOfRange(words, 0, maxWords)) + "...";
        }

        return summary;
    }

    /* ============================
       🔹 GEMINI CONFIG (✅ FIXED)
       ============================ */

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    // ✅ Correct Gemini 2.5 Flash endpoint
    private static final String GEMINI_URL =
            "https://generativelanguage.googleapis.com/v1/models/gemini-2.5-flash:generateContent";

    public String getLawInfo(String query) {

        if (query == null || query.isBlank()) {
            return "Query cannot be empty";
        }

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // API key in URL (not Authorization header)
            String fullUrl = GEMINI_URL + "?key=" + geminiApiKey;

            String promptText =
                    "Explain this Indian law or legal term in simple, public-friendly language: " + query;

            // Gemini request body
            Map<String, Object> part = Map.of("text", promptText);
            Map<String, Object> content = Map.of("parts", List.of(part));
            Map<String, Object> requestBody = Map.of("contents", List.of(content));

            HttpEntity<Map<String, Object>> entity =
                    new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> response =
                    restTemplate.postForEntity(fullUrl, entity, String.class);

            if (response.getBody() == null) {
                return "No response from Gemini";
            }

            // Parse response JSON
            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode candidates = root.path("candidates");

            if (candidates.isArray() && candidates.size() > 0) {
                JsonNode partsNode =
                        candidates.get(0).path("content").path("parts");
                if (partsNode.isArray() && partsNode.size() > 0) {
                    return partsNode.get(0).path("text").asText();
                }
            }

            return "No valid response from Gemini";

        } catch (Exception e) {

            String msg = e.getMessage() == null ? "" : e.getMessage();

            // Auth / API key issues
            if (msg.contains("401") || msg.contains("403")) {
                return "Service is temporarily unavailable due to authentication issues.";
            }

            // Timeout / network
            if (msg.toLowerCase().contains("timeout")) {
                return "The request took too long. Please try again.";
            }

            // Generic fallback
            return "Something went wrong while fetching legal information.";
        }
    }
}
