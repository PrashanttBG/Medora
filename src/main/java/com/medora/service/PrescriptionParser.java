package com.medora.service;


import com.medora.model.PrescriptionData;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.*;

@Service
public class PrescriptionParser {

    public PrescriptionData parse(String text) {
        String temp = debugOCRAndParse(text);
        String cleaned = cleanOCRText(text);

        PrescriptionData data = new PrescriptionData();
        data.setDiagnosis(null);
        data.setSymptoms(Collections.emptyList());
        data.setMedicines(extractMedicines(cleaned));
        data.setDosages(extractDosages(cleaned));
        data.setTimings(extractTimings(cleaned));

        return data;
    }

    public String debugOCRAndParse(String ocrText) {
        System.out.println("=== RAW OCR TEXT ===");
        System.out.println(ocrText);

        String cleaned = cleanOCRText(ocrText);
        System.out.println("\n=== CLEANED OCR TEXT ===");
        System.out.println(cleaned);

        // Now apply your loose test regex
        Pattern pattern = Pattern.compile("([A-Za-z]{3,})"); // any 3+ letter word
        Matcher matcher = pattern.matcher(cleaned);

        List<String> matches = new ArrayList<>();
        while (matcher.find()) {
            String word = matcher.group(1);
            matches.add(word);
            System.out.println("Matched: " + word); // see what regex catches
        }

        if (matches.isEmpty()) {
            System.out.println("⚠ No matches found in cleaned OCR text.");
        } else {
            System.out.println("\n✅ Matches: " + matches);
        }

        return "Done debug.";
    }



    private String cleanOCRText(String text) {
        return text
                .replaceAll("T@B", "TAB")
                .replaceAll("TA8", "TAB")
                .replaceAll("TAD", "TAB")
                .replaceAll("C@P", "CAP")
                .replaceAll("TaB", "TAB")
                .replaceAll("Tab", "TAB")
                .replaceAll("Cap", "CAP")
                .replaceAll("CAp", "CAP")
                .replaceAll("[^A-Za-z0-9\\s:\\-\\.]", "")  // Remove weird chars
                .replaceAll("\\s+", " ")                   // Normalize spaces
                .toUpperCase()                             // Uniform casing
                .trim();
    }


    private List<String> extractMedicines(String text) {
        List<String> medicines = new ArrayList<>();
        String[] tokens = text.split("\\s+");

        for (int i = 0; i < tokens.length; i++) {
            String token = tokens[i].toUpperCase();
            if (token.equals("TAB") || token.equals("CAP") || token.equals("SYR") || token.equals("INJ") || token.equals("Tab") || token.equals("Inj")|| token.equals("Syp")) {
                if (i + 1 < tokens.length) {
                    String medCandidate = tokens[i + 1].replaceAll("[^A-Za-z0-9]", "");

                    medicines.add(medCandidate);
                    System.out.println("Detected medicine: " + (medCandidate));
                }
            }
        }
        return medicines;
    }




    private List<String> extractDosages(String text) {
        List<String> dosages = new ArrayList<>();
        Pattern pattern = Pattern.compile("(\\d{2,4}\\s*(mg|ml))", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(text);

        while (matcher.find()) {
            dosages.add(matcher.group(1));
        }
        return dosages;
    }

    private List<String> extractTimings(String text) {
        List<String> timings = new ArrayList<>();
        Pattern pattern = Pattern.compile("(\\d-\\d-\\d)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(text);

        while (matcher.find()) {
            timings.add(matcher.group(1));
        }
        return timings;
    }
}
