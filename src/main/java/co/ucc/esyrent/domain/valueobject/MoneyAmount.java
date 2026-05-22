package co.ucc.esyrent.domain.valueobject;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.math.BigDecimal;
import java.util.Objects;

@Embeddable
public class MoneyAmount {

    @Column(name = "amount", precision = 19, scale = 2, nullable = false)
    private BigDecimal amount;

    @Column(name = "currency", nullable = false, length = 3)
    private String currency;

    protected MoneyAmount() {
    }

    public MoneyAmount(BigDecimal amount, String currency) {
        if (amount == null) {
            throw new IllegalArgumentException("Amount cannot be null");
        }
        if (currency == null || currency.isBlank()) {
            throw new IllegalArgumentException("Currency cannot be blank");
        }
        this.amount = amount;
        this.currency = currency.trim().toUpperCase();
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public MoneyAmount add(MoneyAmount other) {
        validateCurrency(other);
        return new MoneyAmount(amount.add(other.amount), currency);
    }

    public MoneyAmount subtract(MoneyAmount other) {
        validateCurrency(other);
        return new MoneyAmount(amount.subtract(other.amount), currency);
    }

    public MoneyAmount multiply(BigDecimal factor) {
        if (factor == null) {
            throw new IllegalArgumentException("Factor cannot be null");
        }
        return new MoneyAmount(amount.multiply(factor), currency);
    }

    public boolean isGreaterThan(MoneyAmount other) {
        validateCurrency(other);
        return amount.compareTo(other.amount) > 0;
    }

    public MoneyAmount normalizeScale() {
        return new MoneyAmount(amount.setScale(2, java.math.RoundingMode.HALF_UP), currency);
    }

    private void validateCurrency(MoneyAmount other) {
        if (other == null) {
            throw new IllegalArgumentException("MoneyAmount cannot be null");
        }
        if (!currency.equals(other.currency)) {
            throw new IllegalArgumentException("Currency mismatch");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MoneyAmount that)) {
            return false;
        }
        return Objects.equals(amount, that.amount) && Objects.equals(currency, that.currency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount, currency);
    }
}
