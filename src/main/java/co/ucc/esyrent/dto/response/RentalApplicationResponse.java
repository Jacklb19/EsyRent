package co.ucc.esyrent.dto.response;

import co.ucc.esyrent.domain.enums.RentalApplicationStatus;
import java.time.LocalDateTime;

public record RentalApplicationResponse(
        Long id,
        Long propertyId,
        String propertyAddress,
        Long ownerId,
        String ownerName,
        Long tenantId,
        String tenantName,
        RentalApplicationStatus status,
        String message,
        LocalDateTime submittedAt,
        LocalDateTime resolvedAt,
        String rejectionReason,
        Long contractId
) {
}
