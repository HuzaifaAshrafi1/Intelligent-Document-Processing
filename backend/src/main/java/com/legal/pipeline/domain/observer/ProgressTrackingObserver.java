package com.legal.pipeline.domain.observer;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Concrete observer that tracks processing progress for documents.
 * Maintains progress state and provides progress reporting.
 */
@Component
public class ProgressTrackingObserver implements DocumentProcessingObserver {

    private final Map<String, ProcessingProgress> progressMap = new ConcurrentHashMap<>();

    @Override
    public void onProcessingStarted(String documentId) {
        progressMap.put(documentId, new ProcessingProgress());
        System.out.println("Progress tracking started for document: " + documentId);
    }

    @Override
    public void onProcessingProgress(String documentId, String stage, int progress) {
        ProcessingProgress currentProgress = progressMap.get(documentId);
        if (currentProgress != null) {
            currentProgress.updateProgress(stage, progress);
            System.out.println("Document " + documentId + " progress: " + progress + "% (" + stage + ")");
        }
    }

    @Override
    public void onProcessingCompleted(String documentId, boolean success) {
        ProcessingProgress progress = progressMap.remove(documentId);
        if (progress != null) {
            System.out.println("Document " + documentId + " processing completed. " +
                             "Final progress: " + progress.getOverallProgress() + "%");
        }
    }

    @Override
    public void onProcessingError(String documentId, String error) {
        ProcessingProgress progress = progressMap.remove(documentId);
        if (progress != null) {
            System.out.println("Document " + documentId + " processing failed at " +
                             progress.getOverallProgress() + "% progress. Error: " + error);
        }
    }

    /**
     * Gets the current progress for a document.
     *
     * @param documentId the document ID
     * @return the processing progress, or null if not found
     */
    public ProcessingProgress getProgress(String documentId) {
        return progressMap.get(documentId);
    }

    /**
     * Inner class to represent processing progress.
     */
    public static class ProcessingProgress {
        private String currentStage = "Not started";
        private int overallProgress = 0;

        public void updateProgress(String stage, int progress) {
            this.currentStage = stage;
            this.overallProgress = progress;
        }

        public String getCurrentStage() {
            return currentStage;
        }

        public int getOverallProgress() {
            return overallProgress;
        }
    }
}
