package com.medora.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class PrescriptionData {
    private String diagnosis;
    private List<String> symptoms;
    private List<String> medicines;
    private List<String> dosages;
    private List<String> timings;

    public PrescriptionData() {
        this.symptoms = new ArrayList<>();
        this.medicines = new ArrayList<>();
        this.dosages = new ArrayList<>();
        this.timings = new ArrayList<>();
    }
}
