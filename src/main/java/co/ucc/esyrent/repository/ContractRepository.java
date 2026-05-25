package co.ucc.esyrent.repository;

import co.ucc.esyrent.domain.entity.Contract;
import co.ucc.esyrent.domain.entity.Property;
import co.ucc.esyrent.domain.entity.User;
import co.ucc.esyrent.domain.enums.ContractStatus;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContractRepository extends JpaRepository<Contract, Long> {

    List<Contract> findByTenant(User tenant);

    List<Contract> findByProperty(Property property);

    List<Contract> findByStatus(ContractStatus status);

    List<Contract> findByStatusIn(List<ContractStatus> statuses);

    List<Contract> findByEndDateBetween(LocalDate startDate, LocalDate endDate);

    boolean existsByPropertyAndStatusIn(Property property, List<ContractStatus> statuses);

    boolean existsByPropertyAndTenantAndStatusIn(
            Property property,
            User tenant,
            List<ContractStatus> statuses
    );
}
