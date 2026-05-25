package co.ucc.esyrent.repository;

import co.ucc.esyrent.domain.entity.Attachment;
import co.ucc.esyrent.domain.entity.Contract;
import co.ucc.esyrent.domain.entity.MaintenanceRequest;
import co.ucc.esyrent.domain.entity.Property;
import co.ucc.esyrent.domain.enums.AttachmentType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttachmentRepository extends JpaRepository<Attachment, Long> {

    Optional<Attachment> findByMetadataStoragePath(String storagePath);

    List<Attachment> findByType(AttachmentType type);

    List<Attachment> findByProperty(Property property);

    List<Attachment> findByContract(Contract contract);

    List<Attachment> findByMaintenanceRequest(MaintenanceRequest maintenanceRequest);
}
