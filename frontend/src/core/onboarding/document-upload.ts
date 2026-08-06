import { env } from "@/config/env";
import { useSessionStore } from "@/core/auth/session-store";

export type DocumentUpload = {
  onboardingId: string;
  fileUri: string;
  documentType: "IDENTITY" | "ADDRESS" | "SELFIE";
  fileName: string;
  mimeType: string;
};

export function uploadDocument(
  input: DocumentUpload,
  onProgress?: (progress: number) => void,
): Promise<void> {
  return uploadWithRetry(input, onProgress, 0);
}

async function uploadWithRetry(
  input: DocumentUpload,
  onProgress: ((progress: number) => void) | undefined,
  attempt: number,
): Promise<void> {
  try {
    await upload(input, onProgress);
  } catch (error) {
    if (attempt >= 2) throw error;
    await new Promise<void>((resolve) => setTimeout(resolve, 250 * (attempt + 1)));
    return uploadWithRetry(input, onProgress, attempt + 1);
  }
}

function upload(input: DocumentUpload, onProgress?: (progress: number) => void): Promise<void> {
  const token = useSessionStore.getState().session?.accessToken;
  return new Promise((resolve, reject) => {
    const request = new XMLHttpRequest();
    request.open(
      "POST",
      `${env.bffBaseUrl}/mobile/v1/onboardings/${encodeURIComponent(input.onboardingId)}/documents`,
    );
    request.setRequestHeader("Accept", "application/json");
    if (token) request.setRequestHeader("Authorization", `Bearer ${token}`);
    request.upload.onprogress = (event) => {
      if (event.lengthComputable) onProgress?.(event.loaded / event.total);
    };
    request.onload = () => {
      if (request.status >= 200 && request.status < 300) resolve();
      else reject(new Error(`Document upload failed with status ${request.status}`));
    };
    request.onerror = () => reject(new Error("Document upload network error"));
    request.onabort = () => reject(new Error("Document upload cancelled"));
    const form = new FormData();
    form.append("documentType", input.documentType);
    form.append("file", {
      uri: input.fileUri,
      name: input.fileName,
      type: input.mimeType,
    } as unknown as Blob);
    request.send(form);
  });
}
