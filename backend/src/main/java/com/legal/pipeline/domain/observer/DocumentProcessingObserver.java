package com.legal.pipeline.domain.observer;

/**
 * Observer pattern interface for tracking document processing progress.
 * Observers can be notified of processing events.
 */
public interface DocumentProcessingObserver {

    /**
     * Called when processing starts for a document.
     *
     * @param documentId the ID of the document being processed
     */
    void onProcessingStarted(String documentId);

    /**
     * Called when processing progresses through a stage.
     *
     * @param documentId the ID of the document
     * @param stage the current processing stage
     * @param progress percentage of completion (0-100)
     */
    void onProcessingProgress(String documentId, String stage, int progress);

    /**
     * Called when processing completes for a document.
     *
     * @param documentId the ID of the document
     * @param success true if processing succeeded, false otherwise
     */
    void onProcessingCompleted(String documentId, boolean success);

    /**
     * Called when an error occurs during processing.
     *
     * @param documentId the ID of the document
     * @param error the error message
     */
    void onProcessingError(String documentId, String error);
}
