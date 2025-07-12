package com.medora.service;

import org.springframework.stereotype.Service;
import java.util.regex.*;

@Service
public class PostProcessorService {

    public String cleanSummary(String aiResponse) {
        if (aiResponse == null || aiResponse.isBlank()) {
            return "No summary available.";
        }

        // Try to remove only the generic intro sentence if present
        String withoutIntro = aiResponse.replaceFirst("(?i)^here'?s (a )?(brief )?summary.*?:\\s*", "");

        // Optionally, remove repeated specialist sections if needed
        return withoutIntro.trim();// fallback
    }
}
