import { apiClient } from './client';

export interface FileUploadResponse {
  key: string;
  url: string;
}

export async function uploadFile(file: File) {
  const formData = new FormData();
  formData.append('file', file);

  const { data } = await apiClient.post<FileUploadResponse>('/files/upload', formData, {
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  });

  return data;
}

export async function getFileUrl(key: string) {
  const { data } = await apiClient.get<{ url: string }>(`/files/${encodeURIComponent(key)}`);
  return data.url;
}
