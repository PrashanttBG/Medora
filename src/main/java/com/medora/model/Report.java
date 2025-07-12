package com.medora.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Entity
@Table(name = "reports")
@Data
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileName;
    private String uploadPath;
    private String uploadedBy; // Username
    @Lob
    @Column(columnDefinition = "TEXT")
    private String summary;    // AI Generated Summary=

    @Temporal(TemporalType.TIMESTAMP)
    private Date uploadDate = new Date();

    public Report() {}

    public Report(String fileName, String uploadPath, String uploadedBy, String summary) {
        this.fileName = fileName;
        this.uploadPath = uploadPath;
        this.uploadedBy = uploadedBy;
        this.summary = summary;
    }

    // Getters and Setters
}
