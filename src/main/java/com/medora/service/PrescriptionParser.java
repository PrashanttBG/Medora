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
        data.setDiagnosis(null);  // Since not extracting diagnosis now
        data.setSymptoms(Collections.emptyList()); // Could enhance later
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
                .replaceAll("CA8", "CAP")
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
            if (token.equals("TAB") || token.equals("CAP") || token.equals("SYR") || token.equals("INJ")) {
                if (i + 1 < tokens.length) {
                    String medCandidate = tokens[i + 1].replaceAll("[^A-Za-z0-9]", "");
                    String matched = fuzzyMatch(medCandidate);
                    medicines.add(matched != null ? matched : medCandidate);
                    System.out.println("Detected medicine: " + (matched != null ? matched : medCandidate));
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

    private String capitalizeFirst(String word) {
        if (word == null || word.isEmpty()) return word;
        return word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase();
    }
    private static final List<String> MEDICINE_DICTIONARY = Arrays.asList(
            "Sizodon", "Rivotril", "Ativan", "Sertraline", "Lorazepam", "Clonazepam", "Quetiapine"
    );

    private String fuzzyMatch(String raw) {
        int threshold = 2;
        String best = null;
        int bestDist = Integer.MAX_VALUE;
        for (String med : MEDICINE_DICTIONARY) {
            int dist = levenshteinDistance(raw.toLowerCase(), med.toLowerCase());
            if (dist < bestDist && dist <= threshold) {
                bestDist = dist;
                best = med;
            }
        }
        return best;
    }

    private int levenshteinDistance(String a, String b) {
        int[] costs = new int[b.length() + 1];
        for (int j = 0; j < costs.length; j++) costs[j] = j;
        for (int i = 1; i <= a.length(); i++) {
            costs[0] = i;
            int nw = i - 1;
            for (int j = 1; j <= b.length(); j++) {
                int cj = Math.min(1 + Math.min(costs[j], costs[j - 1]),
                        a.charAt(i - 1) == b.charAt(j - 1) ? nw : nw + 1);
                nw = costs[j];
                costs[j] = cj;
            }
        }
        return costs[b.length()];
    }

}
