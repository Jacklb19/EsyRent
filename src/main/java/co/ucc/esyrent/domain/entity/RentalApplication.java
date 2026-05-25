package co.ucc.esyrent.domain.entity;

import co.ucc.esyrent.domain.enums.RentalApplicationStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "rental_applications")
public class RentalApplication extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "property_id", nullable = false)
    private Property property;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tenant_id", nullable = false)
    private User tenant;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private RentalApplicationStatus status;

    @Column(name = "message", length = 1000)
    private String message;

    @Column(name = "submitted_at", nullable = false)
    private LocalDateTime submittedAt;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    @Column(name = "rejection_reason", length = 500)
    private String rejectionReason;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contract_id")
    private Contract contract;

    protected RentalApplication() {
    }

    public RentalApplication(Property property, User tenant, String message) {
        if (property == null) {
            throw new IllegalArgumentException("Property cannot be null");
        }
        if (tenant == null) {
            throw new IllegalArgumentException("Tenant cannot be null");
        }
        if (!property.isAvailable()) {
            throw new IllegalArgumentException("Property is not available for rental applications");
        }
        this.property = property;
        this.tenant = tenant;
        this.message = normalizeMessage(message);
        this.status = RentalApplicationStatus.PENDING;
        this.submittedAt = LocalDateTime.now();
    }

    public void approve(Contract contract) {
        if (status != RentalApplicationStatus.PENDING) {
            throw new IllegalStateException("Only pending applications can be approved");
        }
        if (contract == null) {
            throw new IllegalArgumentException("Contract cannot be null");
        }
        this.contract = contract;
        this.status = RentalApplicationStatus.APPROVED;
        this.resolvedAt = LocalDateTime.now();
        this.rejectionReason = null;
    }

    public void reject(String reason) {
        if (status != RentalApplicationStatus.PENDING) {
            throw new IllegalStateException("Only pending applications can be rejected");
        }
        this.status = RentalApplicationStatus.REJECTED;
        this.resolvedAt = LocalDateTime.now();
        this.rejectionReason = normalizeReason(reason);
        this.contract = null;
    }

    public void cancel() {
        if (status != RentalApplicationStatus.PENDING) {
            throw new IllegalStateException("Only pending applications can be cancelled");
        }
        this.status = RentalApplicationStatus.CANCELLED;
        this.resolvedAt = LocalDateTime.now();
    }

    public Property getProperty() {
        return property;
    }

    public User getTenant() {
        return tenant;
    }

    public RentalApplicationStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public LocalDateTime getResolvedAt() {
        return resolvedAt;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public Contract getContract() {
        return contract;
    }

    private String normalizeMessage(String message) {
        if (message == null || message.isBlank()) {
            return null;
        }
        return message.trim();
    }

    private String normalizeReason(String reason) {
        if (reason == null || reason.isBlank()) {
            return null;
        }
        return reason.trim();
    }
}
