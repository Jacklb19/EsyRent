package co.ucc.esyrent.domain.entity;

import co.ucc.esyrent.domain.enums.PropertyStatus;
import co.ucc.esyrent.domain.enums.PropertyType;
import co.ucc.esyrent.domain.valueobject.MoneyAmount;
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
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "properties")
public class Property extends BaseEntity {

    @Column(name = "address", nullable = false)
    private String address;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private PropertyType type;

    @Column(name = "area_m2", nullable = false, precision = 10, scale = 2)
    private BigDecimal areaM2;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "reference_rent_amount", precision = 19, scale = 2, nullable = false)),
            @AttributeOverride(name = "currency", column = @Column(name = "reference_rent_currency", length = 3, nullable = false))
    })
    private MoneyAmount referenceRent;

    @Column(name = "description", nullable = false, length = 2000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PropertyStatus status;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL)
    private List<Contract> contracts = new ArrayList<>();

    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Attachment> attachments = new ArrayList<>();

    protected Property() {
    }

    public Property(User owner, String address, PropertyType type, BigDecimal areaM2,
                    MoneyAmount referenceRent, String description) {
        if (owner == null || !owner.isOwner()) {
            throw new IllegalArgumentException("Property owner must be a valid owner user");
        }
        this.owner = owner;
        this.address = requireText(address, "Address");
        this.type = requireType(type);
        this.areaM2 = requireArea(areaM2);
        this.referenceRent = requireMoney(referenceRent);
        this.description = requireText(description, "Description");
        this.status = PropertyStatus.AVAILABLE;
        owner.addProperty(this);
    }

    public String getAddress() {
        return address;
    }

    public PropertyType getType() {
        return type;
    }

    public BigDecimal getAreaM2() {
        return areaM2;
    }

    public MoneyAmount getReferenceRent() {
        return referenceRent;
    }

    public String getDescription() {
        return description;
    }

    public PropertyStatus getStatus() {
        return status;
    }

    public User getOwner() {
        return owner;
    }

    public List<Contract> getContracts() {
        return Collections.unmodifiableList(contracts);
    }

    public List<Attachment> getAttachments() {
        return Collections.unmodifiableList(attachments);
    }

    public void updateDetails(String address, BigDecimal areaM2, MoneyAmount referenceRent, String description) {
        this.address = requireText(address, "Address");
        this.areaM2 = requireArea(areaM2);
        this.referenceRent = requireMoney(referenceRent);
        this.description = requireText(description, "Description");
    }

    public void markAsRented() {
        status = PropertyStatus.RENTED;
    }

    public void markAsAvailable() {
        status = PropertyStatus.AVAILABLE;
    }

    public boolean isAvailable() {
        return status == PropertyStatus.AVAILABLE;
    }

    void addContract(Contract contract) {
        if (!contracts.contains(contract)) {
            contracts.add(contract);
        }
    }

    void addAttachment(Attachment attachment) {
        if (!attachments.contains(attachment)) {
            attachments.add(attachment);
        }
    }

    private String requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " cannot be blank");
        }
        return value.trim();
    }

    private PropertyType requireType(PropertyType propertyType) {
        if (propertyType == null) {
            throw new IllegalArgumentException("Property type cannot be null");
        }
        return propertyType;
    }

    private BigDecimal requireArea(BigDecimal value) {
        if (value == null || value.signum() <= 0) {
            throw new IllegalArgumentException("Area must be greater than zero");
        }
        return value;
    }

    private MoneyAmount requireMoney(MoneyAmount moneyAmount) {
        if (moneyAmount == null) {
            throw new IllegalArgumentException("Reference rent cannot be null");
        }
        return moneyAmount;
    }
}
