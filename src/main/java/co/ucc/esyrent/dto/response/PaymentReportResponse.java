package co.ucc.esyrent.dto.response;

import java.math.BigDecimal;
import java.time.YearMonth;

public record PaymentReportResponse(
        Long contractId,
        Long propertyId,
        String propertyAddress,
        Long tenantId,
        String tenantName,
        YearMonth paymentMonth,
        BigDecimal paidAmount,
        BigDecimal lateFeeAmount,
        String currency
) {
}
