import React, { useState, useCallback } from 'react';
import axios from 'axios';

interface ProcessingStatus {
  documentId: string;
  status: 'PENDING' | 'PROCESSING' | 'COMPLETED' | 'FAILED';
  progress: number;
  stage: string;
  error?: string;
}

const DocumentUpload: React.FC = () => {
  const [selectedFile, setSelectedFile] = useState<File | null>(null);
  const [processingStatus, setProcessingStatus] = useState<ProcessingStatus | null>(null);
  const [isUploading, setIsUploading] = useState(false);

  const handleFileSelect = useCallback((event: React.ChangeEvent<HTMLInputElement>) => {
    const file = event.target.files?.[0];
    if (file) {
      setSelectedFile(file);
    }
  }, []);

  const handleUpload = useCallback(async () => {
    if (!selectedFile) return;

    setIsUploading(true);
    const formData = new FormData();
    formData.append('file', selectedFile);

    try {
      const response = await axios.post('/api/documents/upload', formData, {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
        onUploadProgress: (progressEvent) => {
          const progress = progressEvent.total
            ? Math.round((progressEvent.loaded * 100) / progressEvent.total)
            : 0;
          setProcessingStatus(prev => prev ? { ...prev, progress } : {
            documentId: '',
            status: 'PENDING',
            progress,
            stage: 'Uploading'
          });
        },
      });

      const documentId = response.data.documentId;
      setProcessingStatus({
        documentId,
        status: 'PROCESSING',
        progress: 0,
        stage: 'Processing started'
      });

      // Start polling for status updates
      pollProcessingStatus(documentId);

    } catch (error) {
      console.error('Upload failed:', error);
      setProcessingStatus({
        documentId: '',
        status: 'FAILED',
        progress: 0,
        stage: 'Upload failed',
        error: 'Failed to upload document'
      });
    } finally {
      setIsUploading(false);
    }
  }, [selectedFile]);

  const pollProcessingStatus = useCallback((documentId: string) => {
    const poll = async () => {
      try {
        const response = await axios.get(`/api/documents/${documentId}/status`);
        const status: ProcessingStatus = response.data;

        setProcessingStatus(status);

        if (status.status === 'COMPLETED' || status.status === 'FAILED') {
          return; // Stop polling
        }

        // Continue polling
        setTimeout(poll, 2000);
      } catch (error) {
        console.error('Status check failed:', error);
      }
    };

    poll();
  }, []);

  return (
    <div className="document-upload">
      <h2>Document Processing Pipeline</h2>

      <div className="upload-section">
        <input
          type="file"
          accept=".pdf,.docx,.doc,.txt"
          onChange={handleFileSelect}
          disabled={isUploading}
        />
        <button
          onClick={handleUpload}
          disabled={!selectedFile || isUploading}
        >
          {isUploading ? 'Uploading...' : 'Process Document'}
        </button>
      </div>

      {processingStatus && (
        <div className="status-section">
          <h3>Processing Status</h3>
          <div className="status-info">
            <p><strong>Document ID:</strong> {processingStatus.documentId}</p>
            <p><strong>Status:</strong> {processingStatus.status}</p>
            <p><strong>Stage:</strong> {processingStatus.stage}</p>
            <p><strong>Progress:</strong> {processingStatus.progress}%</p>
            {processingStatus.error && (
              <p className="error"><strong>Error:</strong> {processingStatus.error}</p>
            )}
          </div>

          <div className="progress-bar">
            <div
              className="progress-fill"
              style={{ width: `${processingStatus.progress}%` }}
            />
          </div>
        </div>
      )}
    </div>
  );
};

export default DocumentUpload;
