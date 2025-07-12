package com.medora.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateDocumentRequest {

    private String fileName;
    private String tags;
    private LocalDate reportDate;
}
