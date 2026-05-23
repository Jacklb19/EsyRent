package co.ucc.esyrent.repository;

import co.ucc.esyrent.domain.entity.RefreshToken;
import co.ucc.esyrent.domain.entity.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    List<RefreshToken> findByUserAndRevokedFalse(User user);

    void deleteByUser(User user);
}
