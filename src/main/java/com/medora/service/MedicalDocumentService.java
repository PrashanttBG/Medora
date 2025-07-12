package com.medora.service;

import com.medora.model.MedicalDocument;
import com.medora.model.User;
import com.medora.repository.MedicalDocumentRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class MedicalDocumentService {

    private final MedicalDocumentRepository documentRepository;

    public MedicalDocumentService(MedicalDocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    public List<MedicalDocument> getDocumentsForUser(User user) {
        return documentRepository.findByUserId(user.getId());
    }

    public MedicalDocument uploadDocument(User user, MultipartFile file, LocalDate reportDate, String tags) throws IOException {

        String baseDir = System.getProperty("user.home") + "/medora_uploads/";
        String uploadDir = baseDir + user.getId();

        File dir = new File(uploadDir);
        if (!dir.exists()){
            dir.mkdirs();
        }

        String filePath = uploadDir + "/" + file.getOriginalFilename();
        file.transferTo(new File(filePath));

        MedicalDocument doc = new MedicalDocument();
        doc.setFileName(file.getOriginalFilename());
        doc.setFilePath(filePath);
        doc.setFileType(file.getContentType());
        doc.setSize(file.getSize());
        doc.setReportDate(reportDate);
        doc.setTags(tags);
        doc.setUser(user);

        return documentRepository.save(doc);
    }

    public Optional<MedicalDocument> getDocumentForUser(Long id, User user) {
        return documentRepository.findById(id)
                .filter(doc -> doc.getUser().getId() == user.getId());
    }

    public void deleteDocument(MedicalDocument doc) {
        // Delete file from storage
        new File(doc.getFilePath()).delete();
        documentRepository.delete(doc);
    }
    public MedicalDocument save(MedicalDocument doc) {
        return documentRepository.save(doc);
    }
}

