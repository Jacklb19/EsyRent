package co.ucc.esyrent.domain.entity;

import co.ucc.esyrent.domain.enums.PaymentStatus;
import co.ucc.esyrent.domain.strategy.LateFeeStrategy;
import co.ucc.esyrent.domain.valueobject.MoneyAmount;
import co.ucc.esyrent.domain.valueobject.PaymentCutoff;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.YearMonth;

@Entity
@Table(name = "payments")
public class Payment extends BaseEntity {

    @Column(name = "payment_month", nullable = false)
    private YearMonth paymentMonth;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "amount", precision = 19, scale = 2, nullable = false)),
            @AttributeOverride(name = "currency", column = @Column(name = "currency", length = 3, nullable = false))
    })
    private MoneyAmount amount;

    @Column(name = "payment_date", nullable = false)
    private LocalDate paymentDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PaymentStatus status;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "late_fee_amount", precision = 19, scale = 2)),
            @AttributeOverride(name = "currency", column = @Column(name = "late_fee_currency", length = 3))
    })
    private MoneyAmount lateFee;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "contract_id", nullable = false)
    private Contract contract;

    protected Payment() {
    }

    public Payment(Contract contract, YearMonth paymentMonth, MoneyAmount amount, LocalDate paymentDate) {
        if (contract == null || !contract.canRegisterPayment()) {
            throw new IllegalArgumentException("Contract must allow payment registration");
        }
        if (paymentMonth == null) {
            throw new IllegalArgumentException("Payment month cannot be null");
        }
        if (paymentDate == null) {
            throw new IllegalArgumentException("Payment date cannot be null");
        }
        this.contract = contract;
        this.paymentMonth = paymentMonth;
        this.amount = requireMoney(amount);
        this.paymentDate = paymentDate;
        this.status = PaymentStatus.ON_TIME;
        contract.addPayment(this);
    }

    public YearMonth getPaymentMonth() {
        return paymentMonth;
    }

    public MoneyAmount getAmount() {
        return amount;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public Contract getContract() {
        return contract;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public MoneyAmount getLateFee() {
        return lateFee;
    }

     public void computeAndApplyLateFee(PaymentCutoff cutoff, MoneyAmount monthlyRent, LateFeeStrategy strategy, YearMonth paymentMonth) {
         if (strategy == null) {
             throw new IllegalArgumentException("Late fee strategy cannot be null");
         }
         if (strategy.isApplicable(cutoff, paymentDate, paymentMonth)) {
             lateFee = strategy.calculate(monthlyRent, cutoff, paymentDate, paymentMonth);
             status = PaymentStatus.LATE;
             return;
         }
         lateFee = null;
         status = PaymentStatus.ON_TIME;
     }

    public boolean isLate() {
        return status == PaymentStatus.LATE;
    }

    private MoneyAmount requireMoney(MoneyAmount value) {
        if (value == null) {
            throw new IllegalArgumentException("Payment amount cannot be null");
        }
        return value;
    }
}
