package co.ucc.esyrent.dto.request;

import java.time.LocalDate;
import java.time.YearMonth;

public record ReportFilter(
        Long ownerId,
        Long propertyId,
        Long tenantId,
        LocalDate startDate,
        LocalDate endDate,
        YearMonth month
) {
}
