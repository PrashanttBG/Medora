package com.medora.controller;

import com.medora.model.Report;
import com.medora.service.*;
import com.medora.utils.PdfTextExtractor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/report")
public class ReportController {

    private static final String UPLOAD_DIR = "C:/uploads/";

    @Autowired
    private ReportService reportService;

    @Autowired
    private GeminiService geminiService;

    @Autowired
    private PostProcessorService postProcessorService;

    @Autowired
    private OCRService ocrService;

    // Upload Report
    @PostMapping("/upload")
    public ResponseEntity<String> uploadReport(@RequestParam("file") MultipartFile file,
                                               @RequestParam("username") String username) {
        try {

            // Simulate AI Summary and Doctor Recommendation
            String extractedText = PdfTextExtractor.extractText(file);

            // Save file to server
            String filePath = UPLOAD_DIR + file.getOriginalFilename();
            file.transferTo(new File(filePath));

            String fullResponse = geminiService.analyzeReport(extractedText);

            String summary = postProcessorService.cleanSummary(fullResponse);

            System.out.println("Summary: " + summary);

            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("File is empty");
            }


            Report report = new Report(file.getOriginalFilename(), filePath, username, summary);
            reportService.saveReport(report);
            return ResponseEntity.ok(summary);

        }
        catch (Exception e) {
            e.printStackTrace(); // See exact problem
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Internal error: " + e.getMessage());
        }
    }

    @PostMapping("/image-analyze")
    public ResponseEntity<String> uploadImageReport(
            @RequestParam("file") MultipartFile file,
            @RequestParam("username") String username) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("File is empty");
            }

            // Save the image to disk
            String filePath = UPLOAD_DIR + file.getOriginalFilename();
            File savedFile = new File(filePath);
            file.transferTo(savedFile);

            // Extract text using OCR
            String extractedText = ocrService.extractTextFromImage(savedFile); // OCR

            // Analyze with Gemini
            String fullResponse = geminiService.analyzeReport(extractedText);
            String summary = postProcessorService.cleanSummary(fullResponse);

            // Debug
            System.out.println("OCR Extracted Text: \n" + extractedText);
            System.out.println("Summary: " + summary);

            // Save to DB
            Report report = new Report(file.getOriginalFilename(), filePath, username, summary);
            reportService.saveReport(report);

            return ResponseEntity.ok(summary);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error processing image report: " + e.getMessage());
        }
    }


    // Get All Reports by Username
    @GetMapping("/user/{username}")
    public ResponseEntity<List<Report>> getReportsByUser(@PathVariable String username) {
        List<Report> reports = reportService.getReportsByUser(username);
        return ResponseEntity.ok(reports);
    }

    // Download Report by ID
    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> downloadReport(@PathVariable Long id) {
        Optional<Report> reportOpt = reportService.getReportById(id);

        if (reportOpt.isPresent()) {
            Report report = reportOpt.get();
            File file = new File(report.getUploadPath());
            Resource resource = new FileSystemResource(file);

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + file.getName());

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(file.length())
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Delete Report by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteReport(@PathVariable Long id) {
        Optional<Report> reportOpt = reportService.getReportById(id);

        if (reportOpt.isPresent()) {
            Report report = reportOpt.get();
            File file = new File(report.getUploadPath());

            // Delete file from server
            if (file.exists()) {
                file.delete();
            }

            // Delete from database
            reportService.deleteReport(id);

            return ResponseEntity.ok("Report deleted successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Report not found.");
        }
    }
}
