package co.ucc.esyrent.domain.valueobject;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.time.LocalDateTime;
import java.util.Objects;

@Embeddable
public class AuditInfo {

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    protected AuditInfo() {
    }

    public AuditInfo(LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static AuditInfo now() {
        LocalDateTime now = LocalDateTime.now();
        return new AuditInfo(now, now);
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public AuditInfo touch() {
        return new AuditInfo(createdAt, LocalDateTime.now());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AuditInfo auditInfo1)) {
            return false;
        }
        return Objects.equals(createdAt, auditInfo1.createdAt)
                && Objects.equals(updatedAt, auditInfo1.updatedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(createdAt, updatedAt);
    }
}
