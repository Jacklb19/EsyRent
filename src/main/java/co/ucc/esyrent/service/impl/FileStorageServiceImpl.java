package co.ucc.esyrent.service.impl;

import co.ucc.esyrent.domain.entity.Attachment;
import co.ucc.esyrent.domain.entity.Contract;
import co.ucc.esyrent.domain.entity.MaintenanceRequest;
import co.ucc.esyrent.domain.entity.Property;
import co.ucc.esyrent.domain.enums.AttachmentType;
import co.ucc.esyrent.domain.valueobject.AttachmentMetadata;
import co.ucc.esyrent.dto.request.UploadFileRequest;
import co.ucc.esyrent.dto.response.FileDownload;
import co.ucc.esyrent.dto.response.FileResponse;
import co.ucc.esyrent.exception.BusinessRuleException;
import co.ucc.esyrent.exception.ResourceNotFoundException;
import co.ucc.esyrent.mapper.FileMapper;
import co.ucc.esyrent.repository.AttachmentRepository;
import co.ucc.esyrent.repository.ContractRepository;
import co.ucc.esyrent.repository.MaintenanceRequestRepository;
import co.ucc.esyrent.repository.PropertyRepository;
import co.ucc.esyrent.service.FileStorageService;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional(readOnly = true)
public class FileStorageServiceImpl implements FileStorageService {

    private final AttachmentRepository attachmentRepository;
    private final PropertyRepository propertyRepository;
    private final ContractRepository contractRepository;
    private final MaintenanceRequestRepository maintenanceRequestRepository;
    private final FileMapper fileMapper;
    private final Path storageRoot;

    public FileStorageServiceImpl(AttachmentRepository attachmentRepository, PropertyRepository propertyRepository,
                                  ContractRepository contractRepository,
                                  MaintenanceRequestRepository maintenanceRequestRepository,
                                  FileMapper fileMapper,
                                  @Value("${app.file-storage.location}") String storageLocation) {
        this.attachmentRepository = attachmentRepository;
        this.propertyRepository = propertyRepository;
        this.contractRepository = contractRepository;
        this.maintenanceRequestRepository = maintenanceRequestRepository;
        this.fileMapper = fileMapper;
        this.storageRoot = Paths.get(storageLocation).toAbsolutePath().normalize();
    }

    @Override
    @Transactional
    public FileResponse upload(UploadFileRequest request) {
        validateUploadRequest(request);
        MultipartFile file = request.file();
        String storedPath = storeFile(file);
        Attachment attachment = createAttachment(request, file, storedPath);
        return fileMapper.toResponse(attachmentRepository.save(attachment));
    }

    @Override
    public List<FileResponse> getFilesForProperty(Long propertyId) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property with id " + propertyId + " was not found"));
        return attachmentRepository.findByProperty(property).stream()
                .map(fileMapper::toResponse)
                .toList();
    }

    @Override
    public List<FileResponse> getFilesForContract(Long contractId) {
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new ResourceNotFoundException("Contract with id " + contractId + " was not found"));
        return attachmentRepository.findByContract(contract).stream()
                .map(fileMapper::toResponse)
                .toList();
    }

    @Override
    public List<FileResponse> getFilesForMaintenance(Long maintenanceRequestId) {
        MaintenanceRequest request = maintenanceRequestRepository.findById(maintenanceRequestId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Maintenance request with id " + maintenanceRequestId + " was not found"));
        return attachmentRepository.findByMaintenanceRequest(request).stream()
                .map(fileMapper::toResponse)
                .toList();
    }

    @Override
    public FileDownload loadFileContent(String storagePath) {
        if (storagePath == null || storagePath.isBlank()) {
            throw new BusinessRuleException("Storage path is required");
        }

        if (storagePath.startsWith("http://") || storagePath.startsWith("https://")) {
            throw new BusinessRuleException("Use public URL directly for remote files");
        }

        Attachment attachment = attachmentRepository.findByMetadataStoragePath(storagePath.trim())
                .orElseThrow(() -> new ResourceNotFoundException("File was not found"));

        return buildDownload(attachment);
    }

    @Override
    public FileDownload loadFileContentById(Long attachmentId) {
        Attachment attachment = attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new ResourceNotFoundException("File with id " + attachmentId + " was not found"));
        return buildDownload(attachment);
    }

    private FileDownload buildDownload(Attachment attachment) {
        String storagePath = attachment.getMetadata().getStoragePath();
        if (storagePath != null && (storagePath.startsWith("http://") || storagePath.startsWith("https://"))) {
            throw new BusinessRuleException("Remote file must be loaded from its public URL");
        }

        Path filePath = resolveReadablePath(storagePath);
        Resource resource = new FileSystemResource(filePath);
        if (!resource.exists() || !resource.isReadable()) {
            throw new ResourceNotFoundException("File content is not available on disk");
        }

        return new FileDownload(
                resource,
                attachment.getMetadata().getContentType(),
                attachment.getMetadata().getFileName()
        );
    }

    private Path resolveReadablePath(String storagePath) {
        Path filePath = isAbsoluteStoragePath(storagePath)
                ? Paths.get(storagePath).toAbsolutePath().normalize()
                : storageRoot.resolve(storagePath).normalize();

        if (!filePath.startsWith(storageRoot)) {
            throw new BusinessRuleException("Invalid storage path");
        }
        if (!Files.exists(filePath) || !Files.isRegularFile(filePath)) {
            throw new ResourceNotFoundException("File was not found on storage");
        }
        return filePath;
    }

    private boolean isAbsoluteStoragePath(String storagePath) {
        if (storagePath == null || storagePath.isBlank()) {
            return false;
        }
        return storagePath.contains(":")
                || storagePath.startsWith("/")
                || storagePath.startsWith("\\\\");
    }

    private void validateUploadRequest(UploadFileRequest request) {
        if (request.file().isEmpty()) {
            throw new BusinessRuleException("Uploaded file cannot be empty");
        }

        switch (request.type()) {
            case PROPERTY_IMAGE -> {
                if (request.propertyId() == null || request.contractId() != null || request.maintenanceRequestId() != null) {
                    throw new BusinessRuleException("Property image upload must target only a property");
                }
            }
            case CONTRACT_PDF -> {
                if (request.contractId() == null || request.propertyId() != null || request.maintenanceRequestId() != null) {
                    throw new BusinessRuleException("Contract PDF upload must target only a contract");
                }
            }
            case MAINTENANCE_EVIDENCE -> {
                if (request.maintenanceRequestId() == null || request.propertyId() != null || request.contractId() != null) {
                    throw new BusinessRuleException("Maintenance evidence upload must target only a maintenance request");
                }
            }
        }
    }

    private String storeFile(MultipartFile file) {
        try {
            Files.createDirectories(storageRoot);
            String originalName = file.getOriginalFilename() == null ? "file" : Path.of(file.getOriginalFilename()).getFileName().toString();
            String uniqueName = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                    + "_" + UUID.randomUUID() + "_" + originalName;
            Path targetPath = storageRoot.resolve(uniqueName).normalize();
            if (!targetPath.startsWith(storageRoot)) {
                throw new BusinessRuleException("Invalid storage path");
            }
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
            return uniqueName;
        } catch (IOException exception) {
            throw new BusinessRuleException("File could not be stored: " + exception.getMessage());
        }
    }

    private Attachment createAttachment(UploadFileRequest request, MultipartFile file, String storedPath) {
        AttachmentMetadata metadata = new AttachmentMetadata(
                safeFileName(file),
                file.getContentType() == null ? "application/octet-stream" : file.getContentType(),
                file.getSize(),
                storedPath
        );

        return switch (request.type()) {
            case PROPERTY_IMAGE -> Attachment.forProperty(findPropertyById(request.propertyId()), metadata);
            case CONTRACT_PDF -> Attachment.forContract(findContractById(request.contractId()), metadata);
            case MAINTENANCE_EVIDENCE -> Attachment.forMaintenance(
                    findMaintenanceById(request.maintenanceRequestId()), metadata);
        };
    }

    private String safeFileName(MultipartFile file) {
        String originalName = file.getOriginalFilename();
        if (originalName == null || originalName.isBlank()) {
            return "file-" + UUID.randomUUID();
        }
        return Path.of(originalName).getFileName().toString();
    }

    private Property findPropertyById(Long propertyId) {
        return propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property with id " + propertyId + " was not found"));
    }

    private Contract findContractById(Long contractId) {
        return contractRepository.findById(contractId)
                .orElseThrow(() -> new ResourceNotFoundException("Contract with id " + contractId + " was not found"));
    }

    private MaintenanceRequest findMaintenanceById(Long maintenanceRequestId) {
        return maintenanceRequestRepository.findById(maintenanceRequestId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Maintenance request with id " + maintenanceRequestId + " was not found"));
    }
}
