package com.legal.pipeline.domain.strategy;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

/**
 * Concrete implementation of IExtractionStrategy for PDF documents.
 * Uses Apache PDFBox to extract text from PDF files.
 */
@Component
public class PDFExtractionStrategy implements IExtractionStrategy {

    @Override
    public String extractText(String documentPath) throws Exception {
        try (PDDocument document = PDDocument.load(new File(documentPath))) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        } catch (IOException e) {
            throw new Exception("Failed to extract text from PDF: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean supports(String documentType) {
        return "pdf".equalsIgnoreCase(documentType);
    }
}
