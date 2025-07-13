package com.medora.controller;


import com.medora.dto.UpdateDocumentRequest;
import com.medora.model.MedicalDocument;
import com.medora.model.User;
import com.medora.security.CurrentUser;
import com.medora.security.UserPrincipal;
import com.medora.service.MedicalDocumentService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/documents")
public class MedicalDocumentController {

    private final MedicalDocumentService documentService;

    public MedicalDocumentController(MedicalDocumentService documentService) {
        this.documentService = documentService;
    }

    @GetMapping("/all")
    public List<MedicalDocument> getDocuments(@CurrentUser UserPrincipal userPrincipal) {
        return documentService.getDocumentsForUser(userPrincipal.getUser());
    }


    @PostMapping("/upload")
    public ResponseEntity<MedicalDocument> uploadDocument(
            @CurrentUser UserPrincipal userPrincipal,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "reportDate", required = false) String reportDate,
            @RequestParam(value = "tags", required = false) String tags
    ) throws IOException {
        User user = userPrincipal.getUser();
//        System.out.println(user);
        LocalDate date = (reportDate != null) ? LocalDate.parse(reportDate) : null;
        MedicalDocument doc = documentService.uploadDocument(user, file, date, tags);
        return ResponseEntity.ok(doc);
    }
    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> downloadDocument(
            @PathVariable Long id,
            @CurrentUser UserPrincipal userPrincipal) throws IOException {

        User user = userPrincipal.getUser();
        MedicalDocument doc = documentService.getDocumentForUser(id, user)
                .orElseThrow(() -> new RuntimeException("Not found"));

        File file = new File(doc.getFilePath());
        byte[] content = Files.readAllBytes(file.toPath());

        String mimeType = Files.probeContentType(file.toPath());
        if (mimeType == null) {
            mimeType = "application/pdf"; // force fallback if detection fails
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + doc.getFileName() + "\"")
                .contentType(MediaType.parseMediaType(mimeType))
                .body(content);
    }



    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDocument(@PathVariable Long id, @CurrentUser UserPrincipal userPrincipal) {
        User user = userPrincipal.getUser();
        MedicalDocument doc = documentService.getDocumentForUser(id, user)
                .orElseThrow(() -> new RuntimeException("Not found"));
        documentService.deleteDocument(doc);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<MedicalDocument> updateDocument(
            @PathVariable Long id,
            @CurrentUser UserPrincipal userPrincipal,
            @RequestBody UpdateDocumentRequest updateRequest
    ) {
        User user = userPrincipal.getUser();
        MedicalDocument doc = documentService.getDocumentForUser(id, user)
                .orElseThrow(() -> new RuntimeException("Document not found"));

        if (updateRequest.getFileName() != null) {
            doc.setFileName(updateRequest.getFileName());
            // Optionally rename actual file on disk
            File oldFile = new File(doc.getFilePath());
            String newPath = oldFile.getParent() + "/" + updateRequest.getFileName();
            boolean renamed = oldFile.renameTo(new File(newPath));
            if (renamed) {
                doc.setFilePath(newPath);
            }
        }

        if (updateRequest.getTags() != null) {
            doc.setTags(updateRequest.getTags());
        }

        if (updateRequest.getReportDate() != null) {
            doc.setReportDate(updateRequest.getReportDate());
        }

        return ResponseEntity.ok(documentService.save(doc));
    }

}

