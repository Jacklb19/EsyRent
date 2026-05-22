package co.ucc.esyrent.domain.factory;

import co.ucc.esyrent.domain.entity.Contract;
import co.ucc.esyrent.domain.entity.Property;
import co.ucc.esyrent.domain.entity.User;
import co.ucc.esyrent.domain.valueobject.MoneyAmount;
import co.ucc.esyrent.domain.valueobject.PaymentCutoff;
import java.time.LocalDate;
import org.springframework.stereotype.Component;

@Component
public class StandardContractFactory extends ContractFactory {

    @Override
    public Contract createContract(Property property, User tenant, LocalDate startDate,
                                   Integer durationMonths, MoneyAmount monthlyRent,
                                   PaymentCutoff cutoff, MoneyAmount deposit) {
        validateProperty(property);
        validateTenant(tenant);
        return new Contract(property, tenant, startDate, durationMonths, monthlyRent, cutoff, deposit);
    }
}
