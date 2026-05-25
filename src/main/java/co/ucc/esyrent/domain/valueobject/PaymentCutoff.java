package co.ucc.esyrent.domain.valueobject;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Objects;

@Embeddable
public class PaymentCutoff {

    @Column(name = "cutoff_day", nullable = false)
    private Integer day;

    protected PaymentCutoff() {
    }

    public PaymentCutoff(Integer day) {
        if (day == null || day < 1 || day > 31) {
            throw new IllegalArgumentException("Cutoff day must be between 1 and 31");
        }
        this.day = day;
    }

    public Integer getDay() {
        return day;
    }

    public boolean isCutoffExceeded(LocalDate paymentDate, YearMonth paymentMonth) {
        return paymentDate.isAfter(computeCutoffDateFor(paymentMonth));
    }

    public long computeLateDays(LocalDate paymentDate, YearMonth paymentMonth) {
        LocalDate cutoffDate = computeCutoffDateFor(paymentMonth);
        return java.time.temporal.ChronoUnit.DAYS.between(cutoffDate, paymentDate);
    }

    public LocalDate computeCutoffDateFor(YearMonth yearMonth) {
        int safeDay = Math.min(day, yearMonth.lengthOfMonth());
        return yearMonth.atDay(safeDay);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PaymentCutoff that)) {
            return false;
        }
        return Objects.equals(day, that.day);
    }

    @Override
    public int hashCode() {
        return Objects.hash(day);
    }
}
