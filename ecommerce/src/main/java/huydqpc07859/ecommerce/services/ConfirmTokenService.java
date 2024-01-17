package huydqpc07859.ecommerce.services;

import huydqpc07859.ecommerce.models.user.ConfirmToken;
import huydqpc07859.ecommerce.repositories.ConfirmTokenRepository;
import huydqpc07859.ecommerce.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ConfirmTokenService {
    private final ConfirmTokenRepository confirmTokenRepository;
    private final UserRepository userRepository;

    public void confirmTokenForEmail(String token) {
        Optional<ConfirmToken> optionalConfirmToken = confirmTokenRepository.findByToken(token);

        if(!optionalConfirmToken.isPresent()){
            throw new RuntimeException("Token is not found");
        }


        ConfirmToken confirmToken = optionalConfirmToken.get();

        if (confirmToken.getConfirmedAt() != null) {
            throw new IllegalStateException("email already confirmed");
        }

        LocalDateTime expiredAt = confirmToken.getExpiresAt();

        if (expiredAt.isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("token expired");
        }
        confirmToken.setConfirmedAt(LocalDateTime.now());
        confirmToken.getUser().setEnable(true);
        confirmTokenRepository.save(confirmToken);


    }

}
