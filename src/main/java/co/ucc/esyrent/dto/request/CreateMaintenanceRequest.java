package co.ucc.esyrent.dto.request;

import co.ucc.esyrent.domain.enums.MaintenanceCategory;
import co.ucc.esyrent.domain.enums.UrgencyLevel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateMaintenanceRequest(
        @NotNull Long contractId,
        @NotBlank String description,
        @NotNull MaintenanceCategory category,
        @NotNull UrgencyLevel urgency
) {
}
