package co.ucc.esyrent.dto.request;

import jakarta.validation.constraints.Size;

public record RejectRentalApplicationRequest(
        @Size(max = 500) String reason
) {
}
