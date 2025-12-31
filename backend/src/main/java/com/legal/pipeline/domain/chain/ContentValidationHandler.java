package com.legal.pipeline.domain.chain;

import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

/**
 * Concrete validation handler for checking document content quality.
 * Validates that the content contains legal terminology and proper structure.
 */
@Component
public class ContentValidationHandler extends AbstractValidationHandler {

    private static final Pattern LEGAL_TERMS_PATTERN = Pattern.compile(
            "(?i)(contract|agreement|party|clause|terms|conditions|law|legal|court|judgment)"
    );

    @Override
    protected boolean performValidation(String documentId, String content) {
        if (!LEGAL_TERMS_PATTERN.matcher(content).find()) {
            System.err.println("Content validation failed for document " + documentId + ": No legal terminology found");
            return false;
        }

        // Check for basic structure (paragraphs, sentences)
        long sentenceCount = content.chars().filter(ch -> ch == '.' || ch == '!' || ch == '?').count();
        if (sentenceCount < 2) {
            System.err.println("Content validation failed for document " + documentId + ": Insufficient sentence structure");
            return false;
        }

        System.out.println("Content validation passed for document " + documentId);
        return true;
    }
}
