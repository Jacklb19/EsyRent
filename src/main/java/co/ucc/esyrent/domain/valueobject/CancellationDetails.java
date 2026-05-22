package co.ucc.esyrent.domain.valueobject;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.time.LocalDate;
import java.util.Objects;

@Embeddable
public class CancellationDetails {

    @Column(name = "cancellation_reason")
    private String reason;

    @Column(name = "cancellation_date")
    private LocalDate date;

    protected CancellationDetails() {
    }

    public CancellationDetails(String reason, LocalDate date) {
        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("Cancellation reason cannot be blank");
        }
        if (date == null) {
            throw new IllegalArgumentException("Cancellation date cannot be null");
        }
        this.reason = reason;
        this.date = date;
    }

    public String getReason() {
        return reason;
    }

    public LocalDate getDate() {
        return date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CancellationDetails that)) {
            return false;
        }
        return Objects.equals(reason, that.reason) && Objects.equals(date, that.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reason, date);
    }
}
