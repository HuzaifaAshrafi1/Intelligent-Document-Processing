package com.legal.pipeline.domain.composite;

/**
 * Composite pattern interface for document structure representation.
 * Allows treating individual documents and document collections uniformly.
 */
public abstract class DocumentComponent {

    protected String name;

    public DocumentComponent(String name) {
        this.name = name;
    }

    /**
     * Gets the name of the component.
     *
     * @return the name of the document or collection
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the size of the component in bytes.
     *
     * @return the size in bytes
     */
    public abstract long getSize();

    /**
     * Adds a child component (for composite nodes).
     *
     * @param component the child component to add
     */
    public abstract void add(DocumentComponent component);

    /**
     * Removes a child component (for composite nodes).
     *
     * @param component the child component to remove
     */
    public abstract void remove(DocumentComponent component);

    /**
     * Gets child components (for composite nodes).
     *
     * @return list of child components
     */
    public abstract java.util.List<DocumentComponent> getChildren();

    /**
     * Displays the component structure.
     *
     * @param indent indentation for display
     */
    public abstract void display(String indent);

    /**
     * Gets the content of the component.
     *
     * @return content string
     */
    /**
     * Gets a child component at the specified index.
     *
     * @param index the index of the child component
     * @return the child component
     * @throws UnsupportedOperationException if the component does not support
     *                                       children
     */
    public DocumentComponent getChild(int index) {
        throw new UnsupportedOperationException();
    }

    public abstract String getContent();
}
