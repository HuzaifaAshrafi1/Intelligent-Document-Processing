package com.legal.pipeline.domain.strategy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for PDFExtractionStrategy.
 */
class PDFExtractionStrategyTest {

    private PDFExtractionStrategy strategy;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        strategy = new PDFExtractionStrategy();
    }

    @Test
    void testSupports() {
        assertTrue(strategy.supports("pdf"));
        assertTrue(strategy.supports("PDF"));
        assertFalse(strategy.supports("docx"));
        assertFalse(strategy.supports("txt"));
    }

    @Test
    void testExtractTextWithInvalidFile() {
        Exception exception = assertThrows(Exception.class, () -> {
            strategy.extractText("nonexistent.pdf");
        });
        assertNotNull(exception);
    }

    // Note: Integration test with actual PDF file would require test resources
    // For now, we test the interface contract
}
