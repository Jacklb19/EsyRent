package co.ucc.esyrent.domain.strategy;

import co.ucc.esyrent.domain.valueobject.MoneyAmount;
import co.ucc.esyrent.domain.valueobject.PaymentCutoff;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import org.springframework.stereotype.Component;

@Component
public class DailyRateLateFeeStrategy implements LateFeeStrategy {

    private static final BigDecimal DAILY_RATE = new BigDecimal("0.001");
    private static final BigDecimal MAX_RATE = new BigDecimal("0.10");

    @Override
    public MoneyAmount calculate(MoneyAmount monthlyRent, PaymentCutoff cutoff, LocalDate paymentDate, YearMonth paymentMonth) {
        if (!isApplicable(cutoff, paymentDate, paymentMonth)) {
            return new MoneyAmount(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP), monthlyRent.getCurrency());
        }

        long lateDays = computeLateDays(cutoff, paymentDate, paymentMonth);
        BigDecimal rawRate = DAILY_RATE.multiply(BigDecimal.valueOf(lateDays));
        BigDecimal cappedRate = rawRate.min(MAX_RATE);
        MoneyAmount fee = monthlyRent.multiply(cappedRate).normalizeScale();
        return capToMaximum(fee, monthlyRent);
    }

    @Override
    public boolean isApplicable(PaymentCutoff cutoff, LocalDate paymentDate, YearMonth paymentMonth) {
        return cutoff != null && paymentDate != null && cutoff.isCutoffExceeded(paymentDate, paymentMonth);
    }

    private long computeLateDays(PaymentCutoff cutoff, LocalDate paymentDate, YearMonth paymentMonth) {
        LocalDate cutoffDate = cutoff.computeCutoffDateFor(paymentMonth);
        return java.time.temporal.ChronoUnit.DAYS.between(cutoffDate, paymentDate);
    }

    private MoneyAmount capToMaximum(MoneyAmount fee, MoneyAmount monthlyRent) {
        MoneyAmount maxFee = monthlyRent.multiply(MAX_RATE).normalizeScale();
        return fee.isGreaterThan(maxFee) ? maxFee : fee;
    }
}
