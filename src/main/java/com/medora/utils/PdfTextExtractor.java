// src/main/java/com/medora/utils/PdfTextExtractor.java
package com.medora.utils;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.PDFTextStripperByArea;
import org.springframework.web.multipart.MultipartFile;

import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PdfTextExtractor {

    public static class TestResult {
        private String testName;
        private String result;
        private String unit;
        private String referenceRange;

        public TestResult(String testName, String result, String unit, String referenceRange) {
            this.testName = testName;
            this.result = result;
            this.unit = unit;
            this.referenceRange = referenceRange;
        }

        public String getTestName() { return testName; }
        public String getResult() { return result; }
        public String getUnit() { return unit; }
        public String getReferenceRange() { return referenceRange; }

        @Override
        public String toString() {
            return "TestResult{" +
                    "testName='" + testName + '\'' +
                    ", result='" + result + '\'' +
                    ", unit='" + unit + '\'' +
                    ", referenceRange='" + referenceRange + '\'' +
                    '}';
        }
    }

    public static String extractText(MultipartFile file){
        try{
            PDDocument document = PDDocument.load(file.getInputStream());
            PDFTextStripper pdfStripper = new PDFTextStripper();
            String text = pdfStripper.getText(document);
            document.close();
            return text;
        }
        catch(Exception e) {
            e.printStackTrace();
            return "Error reading the PDF";
        }
    }

    public static List<TestResult> extractTestResults(File file) throws IOException {
        List<TestResult> results = new ArrayList<>();

        try (PDDocument document = PDDocument.load(file)) {
            PDFTextStripperByArea stripper = new PDFTextStripperByArea();
            stripper.setSortByPosition(true);

            // Adjust this rectangle to match where the table appears in your PDFs
            Rectangle tableRegion = new Rectangle(50, 300, 500, 500);
            stripper.addRegion("table", tableRegion);

            stripper.extractRegions(document.getPage(0));
            String tableText = stripper.getTextForRegion("table");

            String[] lines = tableText.split("\\r?\\n");
            for (int i = 1; i < lines.length; i++) { // Assuming first line is header
                String[] cols = lines[i].trim().split("\\s{2,}"); // split by 2+ spaces
                if (cols.length >= 4) {
                    results.add(new TestResult(cols[0], cols[1], cols[2], cols[3]));
                }
            }
        }

        return results;
    }
}