package co.ucc.esyrent.domain.entity;

import co.ucc.esyrent.domain.enums.ContractStatus;
import co.ucc.esyrent.domain.state.contract.ActiveState;
import co.ucc.esyrent.domain.state.contract.CancelledState;
import co.ucc.esyrent.domain.state.contract.ContractState;
import co.ucc.esyrent.domain.state.contract.ExpiredState;
import co.ucc.esyrent.domain.state.contract.ExpiringSoonState;
import co.ucc.esyrent.domain.valueobject.CancellationDetails;
import co.ucc.esyrent.domain.valueobject.MoneyAmount;
import co.ucc.esyrent.domain.valueobject.PaymentCutoff;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "contracts")
public class Contract extends BaseEntity {

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "duration_months", nullable = false)
    private Integer durationMonths;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "monthly_rent_amount", precision = 19, scale = 2, nullable = false)),
            @AttributeOverride(name = "currency", column = @Column(name = "monthly_rent_currency", length = 3, nullable = false))
    })
    private MoneyAmount monthlyRent;

    @Embedded
    private PaymentCutoff cutoff;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "deposit_amount", precision = 19, scale = 2, nullable = false)),
            @AttributeOverride(name = "currency", column = @Column(name = "deposit_currency", length = 3, nullable = false))
    })
    private MoneyAmount deposit;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ContractStatus status;

    @Embedded
    private CancellationDetails cancellation;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "property_id", nullable = false)
    private Property property;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tenant_id", nullable = false)
    private User tenant;

    @OneToMany(mappedBy = "contract", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Payment> payments = new ArrayList<>();

    @OneToMany(mappedBy = "contract", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MaintenanceRequest> maintenanceRequests = new ArrayList<>();

    @OneToMany(mappedBy = "contract", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Attachment> attachments = new ArrayList<>();

    @Transient
    private ContractState state;

    protected Contract() {
    }

    public Contract(Property property, User tenant, LocalDate startDate, Integer durationMonths,
                    MoneyAmount monthlyRent, PaymentCutoff cutoff, MoneyAmount deposit) {
        if (property == null || !property.isAvailable()) {
            throw new IllegalArgumentException("Property must exist and be available");
        }
        if (tenant == null || !tenant.isTenant()) {
            throw new IllegalArgumentException("Tenant must be a valid tenant user");
        }
        if (startDate == null) {
            throw new IllegalArgumentException("Start date cannot be null");
        }
        if (durationMonths == null || durationMonths <= 0) {
            throw new IllegalArgumentException("Duration must be greater than zero");
        }
        this.property = property;
        this.tenant = tenant;
        this.startDate = startDate;
        this.durationMonths = durationMonths;
        this.endDate = startDate.plusMonths(durationMonths.longValue());
        this.monthlyRent = requireMoney(monthlyRent, "Monthly rent");
        this.cutoff = requireCutoff(cutoff);
        this.deposit = requireMoney(deposit, "Deposit");
        setState(new ActiveState());
        property.markAsRented();
        property.addContract(this);
        tenant.addContract(this);
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public Integer getDurationMonths() {
        return durationMonths;
    }

    public MoneyAmount getMonthlyRent() {
        return monthlyRent;
    }

    public PaymentCutoff getCutoff() {
        return cutoff;
    }

    public MoneyAmount getDeposit() {
        return deposit;
    }

    public ContractStatus getStatus() {
        return status;
    }

    public CancellationDetails getCancellation() {
        return cancellation;
    }

    public Property getProperty() {
        return property;
    }

    public User getTenant() {
        return tenant;
    }

    public List<Payment> getPayments() {
        return Collections.unmodifiableList(payments);
    }

    public List<MaintenanceRequest> getMaintenanceRequests() {
        return Collections.unmodifiableList(maintenanceRequests);
    }

    public List<Attachment> getAttachments() {
        return Collections.unmodifiableList(attachments);
    }

    public void transitionState(LocalDate today) {
        currentState().onSchedulerTick(this, today == null ? LocalDate.now() : today);
    }

    public void cancel(CancellationDetails details) {
        currentState().cancel(this, details);
        property.markAsAvailable();
    }

    public boolean canRegisterPayment() {
        return currentState().canRegisterPayment();
    }

    public void setState(ContractState state) {
        if (state == null) {
            throw new IllegalArgumentException("Contract state cannot be null");
        }
        this.state = state;
        this.status = state.getStatus();
    }

    void addPayment(Payment payment) {
        if (!payments.contains(payment)) {
            payments.add(payment);
        }
    }

    void addMaintenanceRequest(MaintenanceRequest request) {
        if (!maintenanceRequests.contains(request)) {
            maintenanceRequests.add(request);
        }
    }

    void addAttachment(Attachment attachment) {
        if (!attachments.contains(attachment)) {
            attachments.add(attachment);
        }
    }

    public void applyCancellation(CancellationDetails details) {
        if (details == null) {
            throw new IllegalArgumentException("Cancellation details cannot be null");
        }
        this.cancellation = details;
    }

    @PostLoad
    protected void restoreState() {
        this.state = mapState(status);
    }

    private ContractState currentState() {
        if (state == null) {
            state = mapState(status);
        }
        return state;
    }

    private ContractState mapState(ContractStatus currentStatus) {
        if (currentStatus == null) {
            return new ActiveState();
        }
        return switch (currentStatus) {
            case ACTIVE -> new ActiveState();
            case EXPIRING_SOON -> new ExpiringSoonState();
            case EXPIRED -> new ExpiredState();
            case CANCELLED -> new CancelledState();
        };
    }

    private MoneyAmount requireMoney(MoneyAmount value, String fieldName) {
        if (value == null) {
            throw new IllegalArgumentException(fieldName + " cannot be null");
        }
        return value;
    }

    private PaymentCutoff requireCutoff(PaymentCutoff value) {
        if (value == null) {
            throw new IllegalArgumentException("Payment cutoff cannot be null");
        }
        return value;
    }
}
