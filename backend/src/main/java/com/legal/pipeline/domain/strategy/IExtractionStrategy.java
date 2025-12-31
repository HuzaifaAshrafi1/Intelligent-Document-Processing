package com.legal.pipeline.domain.strategy;

/**
 * Strategy pattern interface for text extraction from documents.
 * Different strategies can be implemented for various document types (PDF, DOCX, etc.).
 */
public interface IExtractionStrategy {

    /**
     * Extracts text content from the given document.
     *
     * @param documentPath the path to the document file
     * @return extracted text content as a String
     * @throws Exception if extraction fails
     */
    String extractText(String documentPath) throws Exception;

    /**
     * Checks if this strategy supports the given document type.
     *
     * @param documentType the type of the document (e.g., "pdf", "docx")
     * @return true if supported, false otherwise
     */
    boolean supports(String documentType);
}
