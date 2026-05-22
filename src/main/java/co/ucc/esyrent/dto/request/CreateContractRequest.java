package co.ucc.esyrent.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateContractRequest(
        @NotNull Long propertyId,
        @NotNull Long tenantId,
        @NotNull @FutureOrPresent LocalDate startDate,
        @NotNull @Min(1) Integer durationMonths,
        @NotNull @DecimalMin(value = "0.00") BigDecimal monthlyRentAmount,
        @NotBlank String monthlyRentCurrency,
        @NotNull @Min(1) @Max(31) Integer cutoffDay,
        @NotNull @DecimalMin(value = "0.00") BigDecimal depositAmount,
        @NotBlank String depositCurrency
) {
}
