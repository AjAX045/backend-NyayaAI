package com.ajay.nyayaAI.service;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class GeminiService {

    private final Client client;

    public GeminiService(@Value("${gemini.api.key}") String apiKey) {
        this.client = Client.builder()
                            .apiKey(apiKey)
                            .build();
    }

    public String getGeminiResponse(String prompt) {
        try {
            // 1. Wrap user prompt with an improved instruction
            String improvedPrompt ="""
            		You are a legal assistant AI specialized in explaining BNS law sections. 
            		Follow these strict instructions:

            		1. Use **only the exact sections provided in the user's input**.
            		2. Provide output in this order:
            		   - Chapter name
            		   - Section numbers
            		   - Simplified summary in plain English
            		   - Short legal interpretation
            		   - Examples or penalties **only if they are explicitly mentioned in the text**— skip if nothing is provided.
            		3. Do NOT include any other sections, chapters, or information.
            		4. Do NOT add any starting sentences like 'Here's a summary of...' or other filler text.
            		5. Keep the output concise, structured, and easy to read.

            		User Query & Reference:
            		""" + prompt;


            // 2. Call Gemini with the improved prompt
            GenerateContentResponse response =
                client.models.generateContent("gemini-2.5-flash", improvedPrompt, null);

            // 3. Optional: minor post-processing for clean formatting
            return response.text().trim();

        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }

}
