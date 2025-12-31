package com.legal.pipeline.infrastructure.kafka;

import com.legal.pipeline.domain.DocumentProcessingMessage;
import com.legal.pipeline.domain.observer.DocumentProcessingSubject;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

/**
 * Kafka consumer for handling processing errors in the document processing pipeline.
 * Processes failed messages and notifies observers of errors.
 */
@Service
public class ErrorConsumer {

    private final DocumentProcessingSubject processingSubject;

    public ErrorConsumer(DocumentProcessingSubject processingSubject) {
        this.processingSubject = processingSubject;
    }

    @KafkaListener(topics = "document-error", groupId = "legal-document-processor")
    public void consumeError(DocumentProcessingMessage message) {
        System.err.println("Processing error for document: " + message.getDocumentId());
        System.err.println("Error message: " + message.getErrorMessage());
        System.err.println("Failed at stage: " + message.getCurrentStage());

        try {
            // Mark as failed
            message.setStatus(DocumentProcessingMessage.ProcessingStatus.FAILED);

            // Notify observers of error
            processingSubject.notifyProcessingCompleted(message.getDocumentId(), false);
            processingSubject.notifyProcessingError(message.getDocumentId(), message.getErrorMessage());

            // Log error details
            System.err.println("Document processing failed:");
            System.err.println("  ID: " + message.getDocumentId());
            System.err.println("  Type: " + message.getDocumentType());
            System.err.println("  Stage: " + message.getCurrentStage());
            System.err.println("  Error: " + message.getErrorMessage());

        } catch (Exception e) {
            System.err.println("Error handling failed for document " + message.getDocumentId() + ": " + e.getMessage());
        }
    }
}
