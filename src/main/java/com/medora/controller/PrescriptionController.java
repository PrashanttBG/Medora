package com.medora.controller;

import com.medora.service.GeminiService;
import com.medora.service.OCRService;
import com.medora.service.PrescriptionParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@RestController
@RequestMapping("/api/prescription")
public class PrescriptionController {

    private static final String UPLOAD_DIR = "C:/uploads/prescriptions";

    @Autowired
    private OCRService ocrService;

    @Autowired
    private PrescriptionParser prescriptionParser;

    @Autowired
    private GeminiService geminiService;

    @PostMapping("/analyze")
    public ResponseEntity<String> analyzePrescription(@RequestParam("file") MultipartFile file) {
        try {

            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("File is empty");
            }

            // Save the image to disk
            String filePath = UPLOAD_DIR + file.getOriginalFilename();
            File savedFile = new File(filePath);
            file.transferTo(savedFile);

            // Step 1: Extract text from image
            String extractedText = ocrService.extractTextFromImage(savedFile);

            System.out.println("=== OCR OUTPUT ===");
            System.out.println(extractedText);

//            // Step 2: Parse prescription data (medicines, diagnosis, symptoms)
//            PrescriptionData data = prescriptionParser.parse(extractedText);
//
//            System.out.println("=== PARSED OUTPUT ===");
//            System.out.println("Medicines: " + data.getMedicines());
//            System.out.println("Dosages: " + data.getDosages());
//            System.out.println("Timings: " + data.getTimings());

            // Step 3: Call AI API for summary
//            String summary = geminiService.analyzePrescription(data);
            String summary = "";

            System.out.println("=== AI RESPONSE ===");
            System.out.println(summary);

            // Step 4: Return result
            return ResponseEntity.ok(summary);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error analyzing prescription: " + e.getMessage());
        }
    }
}
