package co.ucc.esyrent.domain.strategy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import co.ucc.esyrent.domain.valueobject.MoneyAmount;
import co.ucc.esyrent.domain.valueobject.PaymentCutoff;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class DailyRateLateFeeStrategyTest {

    private final DailyRateLateFeeStrategy strategy = new DailyRateLateFeeStrategy();

    @Test
    void shouldReturnZeroWhenPaymentIsOnTime() {
        MoneyAmount rent = new MoneyAmount(new BigDecimal("1200000"), "COP");
        PaymentCutoff cutoff = new PaymentCutoff(10);

        MoneyAmount fee = strategy.calculate(rent, cutoff, LocalDate.of(2026, 5, 10));

        assertEquals(new BigDecimal("0.00"), fee.getAmount());
        assertEquals("COP", fee.getCurrency());
        assertFalse(strategy.isApplicable(cutoff, LocalDate.of(2026, 5, 10)));
    }

    @Test
    void shouldCalculateLateFeeUsingDailyRate() {
        MoneyAmount rent = new MoneyAmount(new BigDecimal("1000.00"), "USD");
        PaymentCutoff cutoff = new PaymentCutoff(10);

        MoneyAmount fee = strategy.calculate(rent, cutoff, LocalDate.of(2026, 5, 15));

        assertTrue(strategy.isApplicable(cutoff, LocalDate.of(2026, 5, 15)));
        assertEquals(new BigDecimal("5.00"), fee.getAmount());
        assertEquals("USD", fee.getCurrency());
    }

    @Test
    void shouldComputeLateFeeForLongDelayWithinMonth() {
        MoneyAmount rent = new MoneyAmount(new BigDecimal("1000.00"), "USD");
        PaymentCutoff cutoff = new PaymentCutoff(1);

        MoneyAmount fee = strategy.calculate(rent, cutoff, LocalDate.of(2026, 5, 31));

        assertEquals(new BigDecimal("30.00"), fee.getAmount());
    }
}
