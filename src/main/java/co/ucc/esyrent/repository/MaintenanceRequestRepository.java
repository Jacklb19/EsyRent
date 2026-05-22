package co.ucc.esyrent.repository;

import co.ucc.esyrent.domain.entity.Contract;
import co.ucc.esyrent.domain.entity.MaintenanceRequest;
import co.ucc.esyrent.domain.enums.MaintenanceCategory;
import co.ucc.esyrent.domain.enums.MaintenanceStatus;
import co.ucc.esyrent.domain.enums.UrgencyLevel;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MaintenanceRequestRepository extends JpaRepository<MaintenanceRequest, Long> {

    List<MaintenanceRequest> findByContract(Contract contract);

    List<MaintenanceRequest> findByStatus(MaintenanceStatus status);

    List<MaintenanceRequest> findByCategory(MaintenanceCategory category);

    List<MaintenanceRequest> findByUrgency(UrgencyLevel urgency);
}
