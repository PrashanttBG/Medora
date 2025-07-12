package com.medora.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "medical_documents")
public class MedicalDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileName;
    private String filePath;
    private String fileType;
    private Long size;

    private LocalDate reportDate;
    private LocalDateTime uploadDate = LocalDateTime.now();

    private String tags;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

}

