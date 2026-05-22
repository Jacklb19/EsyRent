package co.ucc.esyrent.dto.response;

import co.ucc.esyrent.domain.enums.ContractStatus;
import java.math.BigDecimal;
import java.time.LocalDate;

public record ContractResponse(
        Long id,
        Long propertyId,
        Long tenantId,
        String tenantName,
        LocalDate startDate,
        LocalDate endDate,
        Integer durationMonths,
        BigDecimal monthlyRentAmount,
        String monthlyRentCurrency,
        Integer cutoffDay,
        BigDecimal depositAmount,
        String depositCurrency,
        ContractStatus status,
        String cancellationReason,
        LocalDate cancellationDate
) {
}
