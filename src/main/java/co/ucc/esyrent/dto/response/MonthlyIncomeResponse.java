package co.ucc.esyrent.dto.response;

import java.math.BigDecimal;
import java.time.YearMonth;

public record MonthlyIncomeResponse(
        YearMonth month,
        BigDecimal totalIncome,
        BigDecimal totalLateFees,
        String currency
) {
}
