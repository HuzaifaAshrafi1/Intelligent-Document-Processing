package com.legal.pipeline.domain.strategy;

import org.springframework.stereotype.Component;

@Component
public class DocxExtractionStrategy implements IExtractionStrategy {

    @Override
    public String extractText(String documentPath) throws Exception {
        // Placeholder implementation as Apache POI is not in dependencies
        return "DOCX extraction not yet implemented.";
    }

    @Override
    public boolean supports(String documentType) {
        return "docx".equalsIgnoreCase(documentType) || "doc".equalsIgnoreCase(documentType);
    }
}
