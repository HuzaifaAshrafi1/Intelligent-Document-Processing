package com.legal.pipeline.infrastructure.kafka;

import com.legal.pipeline.domain.DocumentProcessingMessage;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Kafka consumer for the categorizer stage of the document processing pipeline.
 * Categorizes documents based on content analysis.
 */
@Service
public class CategorizerConsumer {

    private final KafkaTemplate<String, DocumentProcessingMessage> kafkaTemplate;

    public CategorizerConsumer(KafkaTemplate<String, DocumentProcessingMessage> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(topics = "document-categorizer", groupId = "legal-document-processor")
    public void consumeCategorizer(DocumentProcessingMessage message) {
        String documentId = message.getDocumentId();
        if (documentId == null) {
            System.err.println("Received message with null document ID in CategorizerConsumer");
            return;
        }

        System.out.println("Categorizing document: " + documentId);

        try {
            // Simple categorization based on content keywords
            String category = categorizeDocument(message.getContent());
            Map<String, Object> metadata = message.getMetadata();
            if (metadata == null) {
                metadata = new HashMap<>();
            }
            metadata.put("category", category);
            message.setMetadata(metadata);
            message.setCurrentStage("CATEGORIZER");

            // Send to output stage
            kafkaTemplate.send("document-output", documentId, message);
            System.out.println("Document " + documentId + " categorized as '" + category +
                    "' and sent to output stage");

        } catch (Exception e) {
            message.setStatus(DocumentProcessingMessage.ProcessingStatus.FAILED);
            message.setErrorMessage("Categorization failed: " + e.getMessage());
            kafkaTemplate.send("document-error", documentId, message);
            System.err.println("Categorization failed for document " + documentId + ": " + e.getMessage());
        }
    }

    private String categorizeDocument(String content) {
        if (content == null || content.trim().isEmpty()) {
            return "UNKNOWN";
        }

        String lowerContent = content.toLowerCase();

        // Contract-related keywords
        if (lowerContent.contains("contract") || lowerContent.contains("agreement") ||
                lowerContent.contains("terms") || lowerContent.contains("conditions")) {
            return "CONTRACT";
        }

        // Legal document keywords
        if (lowerContent.contains("court") || lowerContent.contains("judgment") ||
                lowerContent.contains("lawsuit") || lowerContent.contains("litigation")) {
            return "LEGAL_DOCUMENT";
        }

        // Corporate keywords
        if (lowerContent.contains("company") || lowerContent.contains("corporation") ||
                lowerContent.contains("board") || lowerContent.contains("shareholder")) {
            return "CORPORATE";
        }

        // Default category
        return "GENERAL";
    }
}
