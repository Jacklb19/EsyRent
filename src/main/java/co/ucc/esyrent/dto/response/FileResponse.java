package co.ucc.esyrent.dto.response;

import co.ucc.esyrent.domain.enums.AttachmentType;
import java.time.LocalDateTime;

public record FileResponse(
        Long id,
        AttachmentType type,
        String fileName,
        String contentType,
        Long sizeBytes,
        String storagePath,
        LocalDateTime uploadedAt
) {
}
