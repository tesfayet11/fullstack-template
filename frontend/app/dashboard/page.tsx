'use client';

import { useState, type ChangeEvent } from 'react';
import { ProtectedRoute } from '@/components/ProtectedRoute';
import { uploadFile } from '@/lib/api/files';

function DashboardContent() {
  const [uploadResult, setUploadResult] = useState<{ key: string; url: string } | null>(null);
  const [error, setError] = useState('');
  const [isUploading, setIsUploading] = useState(false);

  async function handleFileChange(event: ChangeEvent<HTMLInputElement>) {
    const file = event.target.files?.[0];
    if (!file) {
      return;
    }

    setError('');
    setUploadResult(null);
    setIsUploading(true);

    try {
      const result = await uploadFile(file);
      setUploadResult(result);
    } catch {
      setError('Upload failed. Check that MinIO is running.');
    } finally {
      setIsUploading(false);
      event.target.value = '';
    }
  }

  return (
    <div className="card">
      <h1>Dashboard</h1>
      <p>Upload a file to S3-compatible storage (MinIO in local dev).</p>

      <label className="file-input">
        <span>{isUploading ? 'Uploading...' : 'Choose file'}</span>
        <input type="file" onChange={(event) => void handleFileChange(event)} disabled={isUploading} />
      </label>

      {error && <p className="error">{error}</p>}

      {uploadResult && (
        <div className="upload-result">
          <p>
            <strong>Key:</strong> {uploadResult.key}
          </p>
          <p>
            <strong>URL:</strong>{' '}
            <a href={uploadResult.url} target="_blank" rel="noreferrer">
              Open file
            </a>
          </p>
        </div>
      )}
    </div>
  );
}

export default function DashboardPage() {
  return (
    <ProtectedRoute>
      <DashboardContent />
    </ProtectedRoute>
  );
}
