package co.ucc.esyrent.service;

import co.ucc.esyrent.dto.request.UploadFileRequest;
import co.ucc.esyrent.dto.response.FileDownload;
import co.ucc.esyrent.dto.response.FileResponse;
import java.util.List;

public interface FileStorageService {

    FileResponse upload(UploadFileRequest request);

    List<FileResponse> getFilesForProperty(Long propertyId);

    List<FileResponse> getFilesForContract(Long contractId);

    List<FileResponse> getFilesForMaintenance(Long maintenanceRequestId);

    FileDownload loadFileContent(String storagePath);

    FileDownload loadFileContentById(Long attachmentId);
}
