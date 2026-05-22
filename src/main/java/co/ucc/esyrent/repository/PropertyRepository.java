package co.ucc.esyrent.repository;

import co.ucc.esyrent.domain.entity.Property;
import co.ucc.esyrent.domain.entity.User;
import co.ucc.esyrent.domain.enums.PropertyStatus;
import co.ucc.esyrent.domain.enums.PropertyType;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PropertyRepository extends JpaRepository<Property, Long> {

    List<Property> findByOwner(User owner);

    List<Property> findByStatus(PropertyStatus status);

    List<Property> findByType(PropertyType type);

    List<Property> findByOwnerAndStatus(User owner, PropertyStatus status);
}
