package co.ucc.esyrent.repository;

import co.ucc.esyrent.domain.entity.User;
import co.ucc.esyrent.domain.enums.UserRole;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    List<User> findByRole(UserRole role);

    Optional<User> findByResetToken(String resetToken);
}
