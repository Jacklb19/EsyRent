package co.ucc.esyrent.domain.strategy;

import co.ucc.esyrent.domain.valueobject.MoneyAmount;
import co.ucc.esyrent.domain.valueobject.PaymentCutoff;
import java.time.LocalDate;
import java.time.YearMonth;

public interface LateFeeStrategy {

    MoneyAmount calculate(MoneyAmount monthlyRent, PaymentCutoff cutoff, LocalDate paymentDate, YearMonth paymentMonth);

    boolean isApplicable(PaymentCutoff cutoff, LocalDate paymentDate, YearMonth paymentMonth);
}
