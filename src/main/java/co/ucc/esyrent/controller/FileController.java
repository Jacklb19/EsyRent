package co.ucc.esyrent.controller;

import co.ucc.esyrent.dto.request.UploadFileRequest;
import co.ucc.esyrent.dto.response.FileDownload;
import co.ucc.esyrent.dto.response.FileResponse;
import co.ucc.esyrent.service.FileStorageService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/files")
public class FileController {

    private final FileStorageService fileStorageService;

    public FileController(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("@securityAccessService.canUploadFile(#request, authentication)")
    public ResponseEntity<FileResponse> upload(@Valid @ModelAttribute UploadFileRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(fileStorageService.upload(request));
    }

    @GetMapping(params = "propertyId")
    @PreAuthorize("@securityAccessService.canAccessFilesForProperty(#propertyId, authentication)")
    public ResponseEntity<List<FileResponse>> getFilesForProperty(@RequestParam Long propertyId) {
        return ResponseEntity.ok(fileStorageService.getFilesForProperty(propertyId));
    }

    @GetMapping(params = "contractId")
    @PreAuthorize("@securityAccessService.canAccessFilesForContract(#contractId, authentication)")
    public ResponseEntity<List<FileResponse>> getFilesForContract(@RequestParam Long contractId) {
        return ResponseEntity.ok(fileStorageService.getFilesForContract(contractId));
    }

    @GetMapping(params = "maintenanceRequestId")
    @PreAuthorize("@securityAccessService.canAccessFilesForMaintenance(#maintenanceRequestId, authentication)")
    public ResponseEntity<List<FileResponse>> getFilesForMaintenance(@RequestParam Long maintenanceRequestId) {
        return ResponseEntity.ok(fileStorageService.getFilesForMaintenance(maintenanceRequestId));
    }

    @GetMapping("/{attachmentId}/content")
    @PreAuthorize("@securityAccessService.canAccessFileById(#attachmentId, authentication)")
    public ResponseEntity<org.springframework.core.io.Resource> getFileContentById(@PathVariable Long attachmentId) {
        return toFileResponse(fileStorageService.loadFileContentById(attachmentId));
    }

    @GetMapping("/content")
    @PreAuthorize("@securityAccessService.canAccessFileContent(#path, authentication)")
    public ResponseEntity<org.springframework.core.io.Resource> getFileContent(@RequestParam String path) {
        return toFileResponse(fileStorageService.loadFileContent(path));
    }

    private ResponseEntity<org.springframework.core.io.Resource> toFileResponse(FileDownload download) {
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(download.contentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" + download.fileName() + "\"")
                .body(download.resource());
    }
}
