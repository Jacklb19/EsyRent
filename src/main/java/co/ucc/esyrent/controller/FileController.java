package co.ucc.esyrent.controller;

import co.ucc.esyrent.dto.request.UploadFileRequest;
import co.ucc.esyrent.dto.response.FileResponse;
import co.ucc.esyrent.service.FileStorageService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
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
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER', 'TENANT')")
    public ResponseEntity<FileResponse> upload(@Valid @ModelAttribute UploadFileRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(fileStorageService.upload(request));
    }

    @GetMapping(params = "propertyId")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<FileResponse>> getFilesForProperty(@RequestParam Long propertyId) {
        return ResponseEntity.ok(fileStorageService.getFilesForProperty(propertyId));
    }

    @GetMapping(params = "contractId")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<FileResponse>> getFilesForContract(@RequestParam Long contractId) {
        return ResponseEntity.ok(fileStorageService.getFilesForContract(contractId));
    }

    @GetMapping(params = "maintenanceRequestId")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<FileResponse>> getFilesForMaintenance(@RequestParam Long maintenanceRequestId) {
        return ResponseEntity.ok(fileStorageService.getFilesForMaintenance(maintenanceRequestId));
    }
}
