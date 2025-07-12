package com.medora.controller;

import com.medora.service.GeminiService;
import com.medora.service.OCRService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/medicine")
public class MedicineDecoderController {

    @Autowired
    private GeminiService geminiService;

    @Autowired
    private OCRService ocrService;

    // POST endpoint to decode medicine
    @PostMapping(value = "/decode", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> decodeMedicine(
            @RequestPart(required = false) String medicineName,
            @RequestPart(required = false) String language,
            @RequestPart(required = false) MultipartFile image) {

        if ((medicineName == null || medicineName.isEmpty()) && (image == null || image.isEmpty())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Provide either medicine name or image."));
        }

        // If image is provided, perform OCR to extract medicine name
        if (medicineName == null || medicineName.isEmpty()) {
            medicineName = ocrService.extractMedicineName(image);
            if (medicineName == null || medicineName.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Could not extract medicine name from image."));
            }
        }

        Map<String, String> aiResponse = geminiService.getMedicineDetails(medicineName, language);
        if (aiResponse.isEmpty()) {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to parse AI response."));
        }

        return ResponseEntity.ok(aiResponse);
    }

}
