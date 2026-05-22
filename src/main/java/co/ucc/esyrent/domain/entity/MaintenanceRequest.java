package co.ucc.esyrent.domain.entity;

import co.ucc.esyrent.domain.enums.MaintenanceCategory;
import co.ucc.esyrent.domain.enums.MaintenanceStatus;
import co.ucc.esyrent.domain.enums.UrgencyLevel;
import co.ucc.esyrent.domain.state.maintenance.ClosedState;
import co.ucc.esyrent.domain.state.maintenance.InProgressState;
import co.ucc.esyrent.domain.state.maintenance.MaintenanceState;
import co.ucc.esyrent.domain.state.maintenance.OpenState;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PostLoad;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "maintenance_requests")
public class MaintenanceRequest extends BaseEntity {

    @Column(name = "description", nullable = false, length = 2000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private MaintenanceCategory category;

    @Enumerated(EnumType.STRING)
    @Column(name = "urgency", nullable = false)
    private UrgencyLevel urgency;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private MaintenanceStatus status;

    @Column(name = "opened_at", nullable = false)
    private LocalDateTime openedAt;

    @Column(name = "closed_at")
    private LocalDateTime closedAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "contract_id", nullable = false)
    private Contract contract;

    @OneToMany(mappedBy = "maintenanceRequest", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Attachment> attachments = new ArrayList<>();

    @Transient
    private MaintenanceState state;

    protected MaintenanceRequest() {
    }

    public MaintenanceRequest(Contract contract, String description, MaintenanceCategory category, UrgencyLevel urgency) {
        if (contract == null) {
            throw new IllegalArgumentException("Contract cannot be null");
        }
        this.contract = contract;
        this.description = requireText(description, "Description");
        this.category = requireCategory(category);
        this.urgency = requireUrgency(urgency);
        this.openedAt = LocalDateTime.now();
        setState(new OpenState());
        contract.addMaintenanceRequest(this);
    }

    public String getDescription() {
        return description;
    }

    public MaintenanceCategory getCategory() {
        return category;
    }

    public UrgencyLevel getUrgency() {
        return urgency;
    }

    public MaintenanceStatus getStatus() {
        return status;
    }

    public LocalDateTime getOpenedAt() {
        return openedAt;
    }

    public LocalDateTime getClosedAt() {
        return closedAt;
    }

    public Contract getContract() {
        return contract;
    }

    public List<Attachment> getAttachments() {
        return Collections.unmodifiableList(attachments);
    }

    public void advance() {
        currentState().advance(this);
    }

    public boolean canAdvance() {
        return currentState().canAdvance();
    }

    public void setState(MaintenanceState state) {
        if (state == null) {
            throw new IllegalArgumentException("Maintenance state cannot be null");
        }
        this.state = state;
        this.status = state.getStatus();
        if (this.status == MaintenanceStatus.CLOSED) {
            this.closedAt = LocalDateTime.now();
        }
    }

    void addAttachment(Attachment attachment) {
        if (!attachments.contains(attachment)) {
            attachments.add(attachment);
        }
    }

    @PostLoad
    protected void restoreState() {
        this.state = mapState(status);
    }

    private MaintenanceState currentState() {
        if (state == null) {
            state = mapState(status);
        }
        return state;
    }

    private MaintenanceState mapState(MaintenanceStatus currentStatus) {
        if (currentStatus == null) {
            return new OpenState();
        }
        return switch (currentStatus) {
            case OPEN -> new OpenState();
            case IN_PROGRESS -> new InProgressState();
            case CLOSED -> new ClosedState();
        };
    }

    private String requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " cannot be blank");
        }
        return value.trim();
    }

    private MaintenanceCategory requireCategory(MaintenanceCategory value) {
        if (value == null) {
            throw new IllegalArgumentException("Category cannot be null");
        }
        return value;
    }

    private UrgencyLevel requireUrgency(UrgencyLevel value) {
        if (value == null) {
            throw new IllegalArgumentException("Urgency cannot be null");
        }
        return value;
    }
}
