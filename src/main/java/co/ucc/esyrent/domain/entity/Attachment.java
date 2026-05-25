package co.ucc.esyrent.domain.entity;

import co.ucc.esyrent.domain.enums.AttachmentType;
import co.ucc.esyrent.domain.valueobject.AttachmentMetadata;
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
import java.time.LocalDateTime;

@Entity
@Table(name = "attachments")
public class Attachment extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private AttachmentType type;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "fileName", column = @Column(name = "file_name", nullable = false)),
            @AttributeOverride(name = "contentType", column = @Column(name = "content_type", nullable = false)),
            @AttributeOverride(name = "sizeBytes", column = @Column(name = "size_bytes", nullable = false)),
            @AttributeOverride(name = "storagePath", column = @Column(name = "storage_path", nullable = false))
    })
    private AttachmentMetadata metadata;

    @Column(name = "uploaded_at", nullable = false)
    private LocalDateTime uploadedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_id")
    private Property property;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contract_id")
    private Contract contract;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maintenance_request_id")
    private MaintenanceRequest maintenanceRequest;

    protected Attachment() {
    }

    private Attachment(AttachmentType type, AttachmentMetadata metadata) {
        if (type == null) {
            throw new IllegalArgumentException("Attachment type cannot be null");
        }
        if (metadata == null) {
            throw new IllegalArgumentException("Attachment metadata cannot be null");
        }
        this.type = type;
        this.metadata = metadata;
        this.uploadedAt = LocalDateTime.now();
    }

    public static Attachment forProperty(Property property, AttachmentMetadata metadata) {
        if (property == null) {
            throw new IllegalArgumentException("Property cannot be null");
        }
        Attachment attachment = new Attachment(AttachmentType.PROPERTY_IMAGE, metadata);
        attachment.property = property;
        property.addAttachment(attachment);
        return attachment;
    }

    public static Attachment forContract(Contract contract, AttachmentMetadata metadata) {
        if (contract == null) {
            throw new IllegalArgumentException("Contract cannot be null");
        }
        Attachment attachment = new Attachment(AttachmentType.CONTRACT_PDF, metadata);
        attachment.contract = contract;
        contract.addAttachment(attachment);
        return attachment;
    }

    public static Attachment forMaintenance(MaintenanceRequest request, AttachmentMetadata metadata) {
        if (request == null) {
            throw new IllegalArgumentException("Maintenance request cannot be null");
        }
        Attachment attachment = new Attachment(AttachmentType.MAINTENANCE_EVIDENCE, metadata);
        attachment.maintenanceRequest = request;
        request.addAttachment(attachment);
        return attachment;
    }

    public AttachmentType getType() {
        return type;
    }

    public AttachmentMetadata getMetadata() {
        return metadata;
    }

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }

    public Property getProperty() {
        return property;
    }

    public Contract getContract() {
        return contract;
    }

    public MaintenanceRequest getMaintenanceRequest() {
        return maintenanceRequest;
    }
}
