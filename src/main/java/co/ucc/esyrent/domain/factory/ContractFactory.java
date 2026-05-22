package co.ucc.esyrent.domain.factory;

import co.ucc.esyrent.domain.entity.Contract;
import co.ucc.esyrent.domain.entity.Property;
import co.ucc.esyrent.domain.entity.User;
import co.ucc.esyrent.domain.valueobject.MoneyAmount;
import co.ucc.esyrent.domain.valueobject.PaymentCutoff;
import java.time.LocalDate;

public abstract class ContractFactory {

    public abstract Contract createContract(Property property, User tenant, LocalDate startDate,
                                            Integer durationMonths, MoneyAmount monthlyRent,
                                            PaymentCutoff cutoff, MoneyAmount deposit);

    protected void validateProperty(Property property) {
        if (property == null) {
            throw new IllegalArgumentException("Property cannot be null");
        }
        if (!property.isAvailable()) {
            throw new IllegalArgumentException("Property is not available for contract creation");
        }
    }

    protected void validateTenant(User tenant) {
        if (tenant == null) {
            throw new IllegalArgumentException("Tenant cannot be null");
        }
        if (!tenant.isTenant()) {
            throw new IllegalArgumentException("User must have tenant role");
        }
    }
}
