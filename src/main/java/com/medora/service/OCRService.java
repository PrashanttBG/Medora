package com.medora.service;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

@Service
public class OCRService {

    public String extractTextFromMultipart(MultipartFile file) {
        try {
            File tempFile = File.createTempFile("prescription-", ".tmp");
            file.transferTo(tempFile);
            String text = extractTextFromImage(tempFile);
            tempFile.delete();
            return text;
        } catch (IOException e) {
            throw new RuntimeException("Failed to process file: " + e.getMessage(), e);
        }
    }

    public String extractTextFromImage(File imageFile) {
        ITesseract tesseract = new Tesseract();
        tesseract.setDatapath("C:/Program Files/Tesseract-OCR/tessdata");
        tesseract.setLanguage("eng");

        try {
            BufferedImage original = ImageIO.read(imageFile);
            if (original == null) {
                throw new RuntimeException("Unsupported or corrupted image format.");
            }

            BufferedImage preprocessed = preprocessImage(original);

            return tesseract.doOCR(preprocessed);

        } catch (TesseractException | IOException e) {
            e.printStackTrace();
            return "OCR failed: " + e.getMessage();
        }
    }

    private BufferedImage preprocessImage(BufferedImage original) {
        // Resize: scale up to 300 DPI equivalent
        int newWidth = original.getWidth() * 2;
        int newHeight = original.getHeight() * 2;
        Image tmp = original.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
        BufferedImage resized = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = resized.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();

        // Convert to grayscale
        BufferedImage gray = new BufferedImage(resized.getWidth(), resized.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        g2d = gray.createGraphics();
        g2d.drawImage(resized, 0, 0, null);
        g2d.dispose();

        // Binarize: simple threshold
        BufferedImage binarized = new BufferedImage(gray.getWidth(), gray.getHeight(), BufferedImage.TYPE_BYTE_BINARY);
        g2d = binarized.createGraphics();
        g2d.drawImage(gray, 0, 0, null);
        g2d.dispose();

        return binarized;
    }

    public String extractMedicineName(MultipartFile image) {
        ITesseract tesseract = new Tesseract();
        tesseract.setDatapath("C:/Program Files/Tesseract-OCR/tessdata");
        try {
            String text = tesseract.doOCR(ImageIO.read(image.getInputStream()));
            // Simple cleanup
            return text.replaceAll("[^a-zA-Z0-9 \\n]", "").trim();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
