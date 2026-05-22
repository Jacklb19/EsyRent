package co.ucc.esyrent.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record UpdatePropertyRequest(
        @NotBlank String address,
        @NotNull @DecimalMin(value = "0.01") BigDecimal areaM2,
        @NotNull @DecimalMin(value = "0.00") BigDecimal referenceRentAmount,
        @NotBlank String referenceRentCurrency,
        @NotBlank String description
) {
}
