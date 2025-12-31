package com.legal.pipeline.infrastructure.kafka;

import com.legal.pipeline.domain.DocumentProcessingMessage;
import com.legal.pipeline.domain.strategy.ExtractionStrategyFactory;
import com.legal.pipeline.domain.strategy.IExtractionStrategy;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 * Kafka consumer for the tokenizer/extraction stage of the document processing
 * pipeline.
 * Extracts text content from documents using appropriate strategies.
 */
@Service
public class TokenizerConsumer {

    private final KafkaTemplate<String, DocumentProcessingMessage> kafkaTemplate;
    private final ExtractionStrategyFactory extractionStrategyFactory;

    public TokenizerConsumer(KafkaTemplate<String, DocumentProcessingMessage> kafkaTemplate,
            ExtractionStrategyFactory extractionStrategyFactory) {
        this.kafkaTemplate = kafkaTemplate;
        this.extractionStrategyFactory = extractionStrategyFactory;
    }

    @KafkaListener(topics = "document-tokenizer", groupId = "legal-document-processor")
    public void consumeTokenizer(DocumentProcessingMessage message) {
        String documentId = message.getDocumentId();
        if (documentId == null) {
            System.err.println("Received message with null document ID in TokenizerConsumer");
            return;
        }

        System.out.println("Tokenizing document: " + documentId);

        try {
            // Get appropriate extraction strategy
            IExtractionStrategy strategy = extractionStrategyFactory.getStrategy(message.getDocumentType());
            if (strategy == null) {
                throw new IllegalArgumentException(
                        "No extraction strategy found for type: " + message.getDocumentType());
            }

            // Extract text content
            String content = strategy.extractText(message.getFilePath());
            message.setContent(content);
            message.setCurrentStage("TOKENIZER");

            // Send to extractor stage
            kafkaTemplate.send("document-extractor", documentId, message);
            System.out.println("Document " + documentId + " tokenized and sent to extractor stage");

        } catch (Exception e) {
            message.setStatus(DocumentProcessingMessage.ProcessingStatus.FAILED);
            message.setErrorMessage("Tokenization failed: " + e.getMessage());
            kafkaTemplate.send("document-error", documentId, message);
            System.err.println("Tokenization failed for document " + documentId + ": " + e.getMessage());
        }
    }
}
