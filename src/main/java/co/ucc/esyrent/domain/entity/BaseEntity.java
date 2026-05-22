package co.ucc.esyrent.domain.entity;

import co.ucc.esyrent.domain.valueobject.AuditInfo;
import jakarta.persistence.Embedded;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private AuditInfo auditInfo;

    public Long getId() {
        return id;
    }

    public AuditInfo getAuditInfo() {
        return auditInfo;
    }

    @PrePersist
    protected void onCreate() {
        auditInfo = AuditInfo.now();
    }

    @PreUpdate
    protected void onUpdate() {
        auditInfo = auditInfo == null ? AuditInfo.now() : auditInfo.touch();
    }
}
