package com.legal.pipeline.domain.chain;

import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Concrete validation handler for document format validation.
 * Checks file existence, size limits, and supported formats.
 */
@Component
public class FormatValidationHandler extends AbstractValidationHandler {

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    private static final String[] SUPPORTED_EXTENSIONS = {"pdf", "docx", "doc", "txt"};

    @Override
    protected boolean performValidation(String documentId, String content) {
        try {
            Path filePath = Paths.get(documentId); // Assuming documentId is the file path

            // Check if file exists
            if (!Files.exists(filePath)) {
                System.err.println("Format validation failed for document " + documentId + ": File does not exist");
                return false;
            }

            // Check file size
            long fileSize = Files.size(filePath);
            if (fileSize > MAX_FILE_SIZE) {
                System.err.println("Format validation failed for document " + documentId +
                                 ": File size exceeds limit (" + fileSize + " bytes)");
                return false;
            }

            // Check file extension
            String fileName = filePath.getFileName().toString();
            String extension = getFileExtension(fileName);
            if (!isSupportedExtension(extension)) {
                System.err.println("Format validation failed for document " + documentId +
                                 ": Unsupported file extension: " + extension);
                return false;
            }

            System.out.println("Format validation passed for document " + documentId);
            return true;

        } catch (Exception e) {
            System.err.println("Format validation failed for document " + documentId + ": " + e.getMessage());
            return false;
        }
    }

    private String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        return lastDotIndex > 0 ? fileName.substring(lastDotIndex + 1).toLowerCase() : "";
    }

    private boolean isSupportedExtension(String extension) {
        for (String supported : SUPPORTED_EXTENSIONS) {
            if (supported.equals(extension)) {
                return true;
            }
        }
        return false;
    }
}
