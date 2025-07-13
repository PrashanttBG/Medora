package com.medora.controller;

import com.medora.service.GeminiService;
import com.medora.service.OCRService;
import com.medora.service.PostProcessorService;
import com.medora.service.PrescriptionParser;
import com.medora.utils.PdfTextExtractor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@RestController
@RequestMapping("/api/prescription")
public class PrescriptionController {

    private static final String UPLOAD_DIR = "C:/uploads/prescriptions/";

    @Autowired
    private OCRService ocrService;

    @Autowired
    private GeminiService geminiService;

    @Autowired
    private PostProcessorService postProcessorService;

    @Autowired
    private PrescriptionParser prescriptionParser;

    // === Upload PDF Prescription ===
    @PostMapping("/analyze")
    public ResponseEntity<String> uploadPrescription(
            @RequestParam("file") MultipartFile file,
            @RequestParam("username") String username) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("File is empty");
            }

            // Extract text from PDF
            String extractedText = PdfTextExtractor.extractText(file);
            System.out.println(extractedText);

            // Save file
            String filePath = UPLOAD_DIR + file.getOriginalFilename();
            file.transferTo(new File(filePath));

            // Call AI
            String aiResponse = geminiService.analyzePrescription(extractedText);
            String summary = postProcessorService.cleanSummary(aiResponse);
            //System.out.println("Summary: " + summary);

            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Upload failed: " + e.getMessage());
        }
    }

    // === Upload Image-based Prescription (uses OCR) ===
    @PostMapping("/image-analyze")
    public ResponseEntity<String> uploadPrescriptionImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam("username") String username) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("File is empty");
            }

            String filePath = UPLOAD_DIR + file.getOriginalFilename();
            File savedFile = new File(filePath);
            file.transferTo(savedFile);

            // Step 1: Extract text from image
            String extractedText = ocrService.extractTextFromImage(savedFile);

//            System.out.println("=== OCR OUTPUT ===");
//            System.out.println(extractedText);

            // AI + Clean Summary
            String aiResponse = geminiService.analyzePrescription(extractedText);
            String summary = postProcessorService.cleanSummary(aiResponse);

            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Upload failed: " + e.getMessage());
        }
    }
}