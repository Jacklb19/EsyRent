package co.ucc.esyrent.dto.response;

import co.ucc.esyrent.domain.enums.PaymentStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;

public record PaymentResponse(
        Long id,
        Long contractId,
        YearMonth paymentMonth,
        BigDecimal amount,
        String currency,
        LocalDate paymentDate,
        PaymentStatus status,
        BigDecimal lateFeeAmount,
        String lateFeeCurrency
) {
}
