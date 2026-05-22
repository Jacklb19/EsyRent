package co.ucc.esyrent.domain.strategy;

import co.ucc.esyrent.domain.valueobject.MoneyAmount;
import co.ucc.esyrent.domain.valueobject.PaymentCutoff;
import java.time.LocalDate;

public interface LateFeeStrategy {

    MoneyAmount calculate(MoneyAmount monthlyRent, PaymentCutoff cutoff, LocalDate paymentDate);

    boolean isApplicable(PaymentCutoff cutoff, LocalDate paymentDate);
}
