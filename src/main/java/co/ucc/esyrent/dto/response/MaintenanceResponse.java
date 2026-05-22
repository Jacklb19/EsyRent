package co.ucc.esyrent.dto.response;

import co.ucc.esyrent.domain.enums.MaintenanceCategory;
import co.ucc.esyrent.domain.enums.MaintenanceStatus;
import co.ucc.esyrent.domain.enums.UrgencyLevel;
import java.time.LocalDateTime;

public record MaintenanceResponse(
        Long id,
        Long contractId,
        String description,
        MaintenanceCategory category,
        UrgencyLevel urgency,
        MaintenanceStatus status,
        LocalDateTime openedAt,
        LocalDateTime closedAt
) {
}
