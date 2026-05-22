package co.ucc.esyrent.dto.request;

import co.ucc.esyrent.domain.enums.AttachmentType;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

public record UploadFileRequest(
        Long propertyId,
        Long contractId,
        Long maintenanceRequestId,
        @NotNull AttachmentType type,
        @NotNull MultipartFile file
) {
}
