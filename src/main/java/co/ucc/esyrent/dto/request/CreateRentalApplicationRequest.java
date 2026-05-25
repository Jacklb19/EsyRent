package co.ucc.esyrent.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateRentalApplicationRequest(
        @NotNull Long propertyId,
        @Size(max = 1000) String message
) {
}
