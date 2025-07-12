package com.medora.service;
import com.medora.model.PrescriptionData;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

import java.util.*;


@Service
public class GeminiService {


    private String API_KEY;
    private String API_URL;

    public GeminiService() {
        this.API_KEY = System.getenv("GEMINI_API_KEY");
        this.API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=" + API_KEY;
    }

    public String buildPrescriptionPrompt(PrescriptionData data) {

        System.out.println("Gemini API URL: " + API_URL);
        System.out.println("Gemini API KEY: " + API_KEY);


        StringBuilder sb = new StringBuilder();
        sb.append("Please analyze this prescription and generate:\n");
        sb.append("- The likely diagnosis or health issue based on these medicines.\n");
        sb.append("- A simple summary for the patient.\n");
        sb.append("- List of medicines prescribed.\n");
        sb.append("- Approximate price of each medicine (estimate or suggest).\n");
        sb.append("- Cheaper or generic alternatives if possible.\n\n");

        sb.append("Medicines:\n");
        for (String med : data.getMedicines()) {
            sb.append("- ").append(med).append("\n");
        }
        return sb.toString();
    }
    public String analyzePrescription(PrescriptionData data) throws IOException {
        String prompt = buildPrescriptionPrompt(data);

        // Create content structure
        Map<String, Object> part = Map.of("text", prompt);
        Map<String, Object> content = Map.of("parts", List.of(part));
        Map<String, Object> requestBody = Map.of("contents", List.of(content));

        // Prepare headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Map> response = restTemplate.postForEntity(API_URL, request, Map.class);

        try {
            List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.getBody().get("candidates");
            Map<String, Object> firstCandidate = candidates.get(0);
            Map<String, Object> contentMap = (Map<String, Object>) firstCandidate.get("content");
            List<Map<String, Object>> parts = (List<Map<String, Object>>) contentMap.get("parts");
            return (String) parts.get(0).get("text");
        } catch (Exception e) {
            return "Error parsing Gemini response: " + e.getMessage();
        }
    }

    public Map<String, String> getMedicineDetails(String medicineName, String language) {
        RestTemplate restTemplate = new RestTemplate();

        String prompt = "I want detailed information about the medicine \"" + medicineName + "\" in the following structured format. Respond strictly using the provided headings and make sure to use clear section markers. Keep the explanation concise and easy to display in a mobile app.\n\n" +
                "Provide the answer exactly like this:\n\n" +
                "**Description:** \n\n" +
                "**Dosage:** \n\n" +
                "**Side Effects:** \n\n" +
                "**Storage Instructions:** \n\n" +
                "**Precautions:** \n\n" +
                "**Drug Interactions:** \n\n" +
                "**Approximate Price:** \n\n" +
                "**Alternate Medicines (with price):** \n- Alternate 1 (₹price)\n- Alternate 2 (₹price)\n\n" +
                "**Additional Notes:** \n\n" +
                "Do not write anything outside this structure. Respond in bullet points where possible. \n\n" +
                "Please respond in " + language + ".";

        Map<String, Object> requestBody = new HashMap<>();
        List<Map<String, Object>> contents = new ArrayList<>();
        Map<String, Object> contentEntry = new HashMap<>();
        List<Map<String, String>> parts = new ArrayList<>();

        parts.add(Map.of("text", prompt));
        contentEntry.put("parts", parts);
        contents.add(contentEntry);
        requestBody.put("contents", contents);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(API_URL, request, Map.class);

            //System.out.println("✅ Full API Raw Response: " + response.getBody());

            // Step 1: Check if 'candidates' exists
            List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.getBody().get("candidates");
            if (candidates == null || candidates.isEmpty()) {
                return Map.of("error", "No candidates in Gemini API response.");
            }

            // Step 2: Get 'content' from the first candidate
            Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
            if (content == null) {
                return Map.of("error", "Content missing in Gemini API response.");
            }

            // Step 3: Get 'parts' list
            List<Map<String, Object>> partsList = (List<Map<String, Object>>) content.get("parts");
            if (partsList == null || partsList.isEmpty()) {
                return Map.of("error", "Parts list is missing in Gemini API response.");
            }

            // Step 4: Get 'text'
            String aiReply = (String) partsList.get(0).get("text");
            if (aiReply == null || aiReply.isEmpty()) {
                return Map.of("error", "AI response text is empty.");
            }


            Map<String, String> parsedSections = parseSections(aiReply);

            if (parsedSections.isEmpty()) {
                return Map.of("error", "Failed to parse AI response.");
            }

            return parsedSections;

        } catch (Exception e) {
            e.printStackTrace();
            return Map.of("error", "Exception occurred: " + e.getMessage());
        }
    }

    private Map<String, String> parseSections(String aiResponse) {


        Map<String, String> sections = new LinkedHashMap<>();

        // Clean response
        aiResponse = aiResponse.trim();

        // Split based on the section headers
        String[] parts = aiResponse.split("\\*\\*");

        for (int i = 1; i < parts.length; i += 2) {  // Start from index 1 because split will lead to empty string at index 0
            String title = parts[i].replace(":", "").trim();
            String content = parts[i + 1].trim();

            content = content.replaceAll("\\s*\\*\\s*", "\n* ");

            sections.put(title, content);
        }

        return sections;
    }

    public String analyzeReport(String extractedText) throws IOException {

        String prompt = "Extract important test outcomes from this medical report and summarize it in layman terms. "
                + "Also, suggest which specialist to consult:\n\n" + extractedText;

        // Create the content structure as expected by Gemini
        Map<String, Object> part = Map.of("text", prompt);
        Map<String, Object> content = Map.of("parts", List.of(part));
        Map<String, Object> requestBody = Map.of("contents", List.of(content));

        // Prepare headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Map> response = restTemplate.postForEntity(API_URL, request, Map.class);

        try {
            List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.getBody().get("candidates");
            Map<String, Object> firstCandidate = candidates.get(0);
            Map<String, Object> contentMap = (Map<String, Object>) firstCandidate.get("content");
            List<Map<String, Object>> parts = (List<Map<String, Object>>) contentMap.get("parts");
            return (String) parts.get(0).get("text");
        } catch (Exception e) {
            return "Error parsing Gemini response: " + e.getMessage();
        }
    }
}