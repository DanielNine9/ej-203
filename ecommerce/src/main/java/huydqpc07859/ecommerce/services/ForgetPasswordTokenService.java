package huydqpc07859.ecommerce.services;

import huydqpc07859.ecommerce.models.user.ConfirmToken;
import huydqpc07859.ecommerce.models.user.ForgetPasswordToken;
import huydqpc07859.ecommerce.repositories.ConfirmTokenRepository;
import huydqpc07859.ecommerce.repositories.ForgetPasswordTokenRepository;
import huydqpc07859.ecommerce.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ForgetPasswordTokenService {

    private final ForgetPasswordTokenRepository forgetPasswordTokenRepository;
    private final UserRepository userRepository;

    public void confirmForgetPasswordTokenForEmail(String token) {
        Optional<ForgetPasswordToken> optionalConfirmToken = forgetPasswordTokenRepository.findByToken(token);

        if(!optionalConfirmToken.isPresent()){
            throw new RuntimeException("Token is not found");
        }


        ForgetPasswordToken confirmToken = optionalConfirmToken.get();

        if (confirmToken.getConfirmedAt() != null) {
            throw new IllegalStateException("email already confirmed forget password token");
        }

        LocalDateTime expiredAt = confirmToken.getExpiresAt();

        if (expiredAt.isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("token expired");
        }
        confirmToken.setConfirmedAt(LocalDateTime.now());
        forgetPasswordTokenRepository.save(confirmToken);
    }

}
