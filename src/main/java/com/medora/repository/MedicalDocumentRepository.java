package com.medora.repository;

import com.medora.model.MedicalDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MedicalDocumentRepository extends JpaRepository<MedicalDocument, Long> {
    List<MedicalDocument> findByUserId(Long userId);
}
