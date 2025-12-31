package com.legal.pipeline.domain.strategy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Factory class for managing and providing extraction strategies.
 * Uses Spring's dependency injection to collect all strategy implementations.
 */
@Component
public class ExtractionStrategyFactory {

    private final Map<String, IExtractionStrategy> strategies;

    @Autowired
    public ExtractionStrategyFactory(List<IExtractionStrategy> strategyList) {
        this.strategies = strategyList.stream()
                .collect(Collectors.toMap(
                        strategy -> getDocumentTypeForStrategy(strategy),
                        Function.identity()
                ));
    }

    /**
     * Gets the appropriate extraction strategy for the given document type.
     *
     * @param documentType the type of the document (e.g., "pdf", "docx")
     * @return the corresponding extraction strategy
     * @throws IllegalArgumentException if no strategy supports the document type
     */
    public IExtractionStrategy getStrategy(String documentType) {
        IExtractionStrategy strategy = strategies.get(documentType.toLowerCase());
        if (strategy == null) {
            throw new IllegalArgumentException("No extraction strategy found for document type: " + documentType);
        }
        return strategy;
    }

    /**
     * Checks if a strategy exists for the given document type.
     *
     * @param documentType the type of the document
     * @return true if supported, false otherwise
     */
    public boolean supportsDocumentType(String documentType) {
        return strategies.containsKey(documentType.toLowerCase());
    }

    private String getDocumentTypeForStrategy(IExtractionStrategy strategy) {
        // This is a simple implementation; in a real scenario, you might use annotations or configuration
        if (strategy instanceof PDFExtractionStrategy) {
            return "pdf";
        } else if (strategy instanceof DocxExtractionStrategy) {
            return "docx";
        }
        throw new IllegalArgumentException("Unknown strategy type: " + strategy.getClass().getSimpleName());
    }
}
