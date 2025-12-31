package com.legal.pipeline.domain.composite;

import java.util.List;

/**
 * Leaf node in the Composite pattern representing individual documents.
 * Cannot have children but implements the DocumentComponent interface.
 */
public class DocumentLeaf extends DocumentComponent {

    private final long size;
    private final String type;
    // content field? Or is it passed in? Assuming content is unrelated to metadata
    // here, but we need to implement getContent.
    // If leaf represents a file, maybe it doesn't hold content in memory yet?
    // Or maybe we should add a content field.
    private String content = "";

    public DocumentLeaf(String name, long size, String type) {
        super(name);
        this.size = size;
        this.type = type;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public long getSize() {
        return size;
    }

    @Override
    public void add(DocumentComponent component) {
        throw new UnsupportedOperationException("Leaf nodes cannot have children");
    }

    @Override
    public void remove(DocumentComponent component) {
        throw new UnsupportedOperationException("Leaf nodes cannot have children");
    }

    @Override
    public List<DocumentComponent> getChildren() {
        return java.util.Collections.emptyList();
    }

    @Override
    public void display(String indent) {
        System.out.println(indent + "Document: " + name + " (" + type + ", " + size + " bytes)");
    }

    @Override
    public String getContent() {
        // Return placeholder or actual content if we had it.
        // Given existing code didn't have content field, maybe it reads from file?
        // But abstract class forces us to implement it.
        return content; // Return empty string or stored content.
    }

    public String getType() {
        return type;
    }
}
