package com.legal.pipeline.domain.observer;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Concrete implementation of DocumentProcessingSubject.
 * Manages a list of observers and notifies them of processing events.
 */
@Component
public class DocumentProcessingSubjectImpl implements DocumentProcessingSubject {

    private final List<DocumentProcessingObserver> observers = new ArrayList<>();

    @Override
    public void addObserver(DocumentProcessingObserver observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(DocumentProcessingObserver observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyProcessingStarted(String documentId) {
        for (DocumentProcessingObserver observer : observers) {
            try {
                observer.onProcessingStarted(documentId);
            } catch (Exception e) {
                System.err.println("Error notifying observer of processing start: " + e.getMessage());
            }
        }
    }

    @Override
    public void notifyProcessingProgress(String documentId, String stage, int progress) {
        for (DocumentProcessingObserver observer : observers) {
            try {
                observer.onProcessingProgress(documentId, stage, progress);
            } catch (Exception e) {
                System.err.println("Error notifying observer of processing progress: " + e.getMessage());
            }
        }
    }

    @Override
    public void notifyProcessingCompleted(String documentId, boolean success) {
        for (DocumentProcessingObserver observer : observers) {
            try {
                observer.onProcessingCompleted(documentId, success);
            } catch (Exception e) {
                System.err.println("Error notifying observer of processing completion: " + e.getMessage());
            }
        }
    }

    @Override
    public void notifyProcessingError(String documentId, String error) {
        for (DocumentProcessingObserver observer : observers) {
            try {
                observer.onProcessingError(documentId, error);
            } catch (Exception e) {
                System.err.println("Error notifying observer of processing error: " + e.getMessage());
            }
        }
    }
}
