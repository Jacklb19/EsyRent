package co.ucc.esyrent.repository;

import co.ucc.esyrent.domain.entity.Property;
import co.ucc.esyrent.domain.entity.RentalApplication;
import co.ucc.esyrent.domain.entity.User;
import co.ucc.esyrent.domain.enums.RentalApplicationStatus;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RentalApplicationRepository extends JpaRepository<RentalApplication, Long> {

    List<RentalApplication> findByTenantOrderBySubmittedAtDesc(User tenant);

    List<RentalApplication> findByProperty_OwnerOrderBySubmittedAtDesc(User owner);

    boolean existsByPropertyAndTenantAndStatus(Property property, User tenant, RentalApplicationStatus status);

    boolean existsByPropertyAndTenantAndStatusIn(
            Property property,
            User tenant,
            Collection<RentalApplicationStatus> statuses
    );
}
