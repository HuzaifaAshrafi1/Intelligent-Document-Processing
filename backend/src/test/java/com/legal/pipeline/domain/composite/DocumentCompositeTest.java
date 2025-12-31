package com.legal.pipeline.domain.composite;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Composite pattern implementations
 */
class DocumentCompositeTest {

    private DocumentLeaf leaf1;
    private DocumentLeaf leaf2;
    private DocumentComposite composite;

    @BeforeEach
    void setUp() {
        leaf1 = new DocumentLeaf("contract.pdf", 1024L, "pdf");
        leaf2 = new DocumentLeaf("agreement.docx", 2048L, "docx");
        composite = new DocumentComposite("Legal Documents");
    }

    @Test
    void testDocumentLeafCreation() {
        assertEquals("contract.pdf", leaf1.getName());
        assertEquals(1024L, leaf1.getSize());
        assertEquals("pdf", leaf1.getType());
    }

    @Test
    void testDocumentCompositeOperations() {
        // Test adding children
        composite.add(leaf1);
        composite.add(leaf2);

        assertEquals(2, composite.getChildren().size());
        assertEquals(3072L, composite.getSize()); // 1024 + 2048

        // Test removing children
        composite.remove(leaf1);
        assertEquals(1, composite.getChildren().size());
        assertEquals(2048L, composite.getSize());
    }

    @Test
    void testLeafOperationsThrowExceptions() {
        // Test that leaf operations throw exceptions
        assertThrows(UnsupportedOperationException.class, () -> leaf1.add(leaf2));
        assertThrows(UnsupportedOperationException.class, () -> leaf1.remove(leaf2));
        assertTrue(leaf1.getChildren().isEmpty());
    }

    @Test
    void testNestedComposites() {
        DocumentComposite subComposite = new DocumentComposite("Contracts");
        subComposite.add(leaf1);
        composite.add(subComposite);
        composite.add(leaf2);

        assertEquals(2, composite.getChildren().size());
        assertEquals(3072L, composite.getSize()); // 1024 + 2048
    }
}
