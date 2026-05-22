package co.ucc.esyrent.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;

public record RegisterPaymentRequest(
        @NotNull Long contractId,
        @NotNull YearMonth paymentMonth,
        @NotNull @DecimalMin(value = "0.00") BigDecimal amount,
        @NotBlank String currency,
        @NotNull LocalDate paymentDate
) {
}
