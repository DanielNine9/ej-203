package huydqpc07859.ecommerce.repositories;

import huydqpc07859.ecommerce.models.user.ConfirmToken;
import huydqpc07859.ecommerce.services.ConfirmTokenService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConfirmTokenRepository extends JpaRepository<ConfirmToken, Long> {
    Optional<ConfirmToken> findByToken(String token);
}
