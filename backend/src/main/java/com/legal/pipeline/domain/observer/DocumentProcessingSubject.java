package com.legal.pipeline.domain.observer;

/**
 * Subject interface for the Observer pattern in document processing.
 * Manages observers and notifies them of processing events.
 */
public interface DocumentProcessingSubject {

    /**
     * Adds an observer to the list of observers.
     *
     * @param observer the observer to add
     */
    void addObserver(DocumentProcessingObserver observer);

    /**
     * Removes an observer from the list of observers.
     *
     * @param observer the observer to remove
     */
    void removeObserver(DocumentProcessingObserver observer);

    /**
     * Notifies all observers of processing start.
     *
     * @param documentId the ID of the document
     */
    void notifyProcessingStarted(String documentId);

    /**
     * Notifies all observers of processing progress.
     *
     * @param documentId the ID of the document
     * @param stage the current stage
     * @param progress the progress percentage
     */
    void notifyProcessingProgress(String documentId, String stage, int progress);

    /**
     * Notifies all observers of processing completion.
     *
     * @param documentId the ID of the document
     * @param success true if successful
     */
    void notifyProcessingCompleted(String documentId, boolean success);

    /**
     * Notifies all observers of processing error.
     *
     * @param documentId the ID of the document
     * @param error the error message
     */
    void notifyProcessingError(String documentId, String error);
}
