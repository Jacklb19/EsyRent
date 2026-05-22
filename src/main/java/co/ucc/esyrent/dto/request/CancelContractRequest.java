package co.ucc.esyrent.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record CancelContractRequest(
        @NotBlank String reason,
        @NotNull LocalDate date
) {
}
