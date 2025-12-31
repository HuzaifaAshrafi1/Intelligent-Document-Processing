package com.legal.pipeline.domain.composite;

import java.util.ArrayList;
import java.util.List;

/**
 * Composite implementation for document structure.
 */
public class DocumentComposite extends DocumentComponent {
    private List<DocumentComponent> children = new ArrayList<>();

    public DocumentComposite(String name) {
        super(name);
    }

    @Override
    public void add(DocumentComponent component) {
        children.add(component);
    }

    @Override
    public void remove(DocumentComponent component) {
        children.remove(component);
    }

    @Override
    public DocumentComponent getChild(int index) {
        if (index >= 0 && index < children.size()) {
            return children.get(index);
        }
        throw new IndexOutOfBoundsException("Invalid index: " + index);
    }

    @Override
    public List<DocumentComponent> getChildren() {
        return children;
    }

    @Override
    public void display(String indent) {
        System.out.println(indent + "Composite: " + name);
        for (DocumentComponent component : children) {
            component.display(indent + "  ");
        }
    }

    public void display() {
        display("");
    }

    @Override
    public long getSize() {
        long totalSize = 0;
        for (DocumentComponent component : children) {
            totalSize += component.getSize();
        }
        return totalSize;
    }

    @Override
    public String getContent() {
        StringBuilder content = new StringBuilder();
        for (DocumentComponent component : children) {
            content.append(component.getContent()).append("\n");
        }
        return content.toString().trim();
    }
}
