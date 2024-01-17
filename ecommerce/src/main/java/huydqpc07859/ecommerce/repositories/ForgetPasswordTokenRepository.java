package huydqpc07859.ecommerce.repositories;

import huydqpc07859.ecommerce.models.user.ConfirmToken;
import huydqpc07859.ecommerce.models.user.ForgetPasswordToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface ForgetPasswordTokenRepository extends JpaRepository<ForgetPasswordToken, Long> {
    Optional<ForgetPasswordToken> findByToken(String token);
}
