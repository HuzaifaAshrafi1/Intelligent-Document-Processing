package com.legal.pipeline.domain;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Message class for document processing pipeline communication.
 * Contains document metadata and processing state.
 */
public class DocumentProcessingMessage {

    private String documentId;
    private String filePath;
    private String documentType;
    private String content;
    private Map<String, Object> metadata;
    private ProcessingStatus status;
    private String errorMessage;
    private LocalDateTime timestamp;
    private String currentStage;

    public DocumentProcessingMessage() {
        this.timestamp = LocalDateTime.now();
        this.status = ProcessingStatus.PENDING;
    }

    public DocumentProcessingMessage(String documentId, String filePath, String documentType) {
        this();
        this.documentId = documentId;
        this.filePath = filePath;
        this.documentType = documentType;
    }

    // Getters and setters
    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    public ProcessingStatus getStatus() {
        return status;
    }

    public void setStatus(ProcessingStatus status) {
        this.status = status;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getCurrentStage() {
        return currentStage;
    }

    public void setCurrentStage(String currentStage) {
        this.currentStage = currentStage;
    }

    /**
     * Enum for processing status.
     */
    public enum ProcessingStatus {
        PENDING,
        PROCESSING,
        COMPLETED,
        FAILED
    }

    @Override
    public String toString() {
        return "DocumentProcessingMessage{" +
                "documentId='" + documentId + '\'' +
                ", documentType='" + documentType + '\'' +
                ", status=" + status +
                ", currentStage='" + currentStage + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
