package com.medora.service;


import com.medora.model.PrescriptionData;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class GPTService {

    private static final String API_URL = "https://api.openai.com/v1/chat/completions";
    private static final String API_KEY = "----";

    public String buildPrompt(PrescriptionData data) {
        StringBuilder sb = new StringBuilder();
        sb.append("Please analyze this prescription and generate:\n");
        sb.append("- A summary of the diagnosis and problem in simple language.\n");
        sb.append("- The medicines prescribed.\n");
        sb.append("- The approximate price of each medicine (you can estimate or suggest).\n");
        sb.append("- Generic or cheaper alternatives if possible.\n\n");

        sb.append("Data:\n");
        sb.append("Diagnosis: ").append(data.getDiagnosis()).append("\n");
        sb.append("Symptoms: ").append(String.join(", ", data.getSymptoms())).append("\n");
        sb.append("Medicines:\n");
        for (String med : data.getMedicines()) {
            sb.append("- ").append(med).append("\n");
        }

        return sb.toString();
    }

    public String callAiApi(String prompt) {

        try {
            // Prepare headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(API_KEY);

            // Build body
            String body = buildRequestBody(prompt);

            // Make HTTP entity
            HttpEntity<String> entity = new HttpEntity<>(body, headers);

            // Call API
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.postForEntity(API_URL, entity, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                return parseAiResponse(response.getBody());
            } else {
                throw new RuntimeException("OpenAI API error: " + response.getStatusCode());
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to call OpenAI API: " + e.getMessage(), e);
        }
    }

    private String parseAiResponse(String body) {
        return body;
    }

    private String buildRequestBody(String prompt) {
        return """
        {
          "model": "gpt-3.5-turbo",
          "messages": [
            {"role": "system", "content": "You are a helpful medical assistant."},
            {"role": "user", "content": %s}
          ]
        }
        """.formatted(jsonEscape(prompt));
    }

    private String jsonEscape(String text) {
        return "\"" + text.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n") + "\"";
    }


//    public String analyzeReport(String extractedText) throws IOException {
//        HttpHeaders headers = new HttpHeaders();
//        headers.setBearerAuth(API_KEY);
//        headers.setContentType(MediaType.APPLICATION_JSON);
//
//        Map<String, Object> message = Map.of(
//                "role", "user",
//                "content", "Extract important test outcomes from this medical report and summarize it in layman terms. Also, suggest which specialist to consult:\n\n" + extractedText
//        );
//
//        Map<String, Object> requestBody = Map.of(
//                "model", "gpt-3.5-turbo",
//                "messages", List.of(message),
//                "temperature", 0.7
//        );
//
//        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
//        RestTemplate restTemplate = new RestTemplate();
//        ResponseEntity<Map> response = restTemplate.postForEntity(API_URL, entity, Map.class);
//
//        List<Map<String, Object>> choices = (List<Map<String, Object>>) response.getBody().get("choices");
//        Map<String, Object> messageContent = (Map<String, Object>) choices.get(0).get("message");
//
//        return (String) messageContent.get("content");
//    }
//
//    public String extractSpecialist(String aiResponse) {
//        Pattern pattern = Pattern.compile("(?i)(?:consult|see|visit) (?:a|an) ([A-Z][a-zA-Z]+)");
//        Matcher matcher = pattern.matcher(aiResponse);
//
//        if (matcher.find()) {
//            return matcher.group(1); // Group 1 contains the specialist name
//        }
//
//        return "General Physician";
//    }
}

