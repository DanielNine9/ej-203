package huydqpc07859.ecommerce.services;

import com.nimbusds.jwt.JWT;
import huydqpc07859.ecommerce.models.dto.CommonResponse;
import huydqpc07859.ecommerce.models.dto.LoginRequest;
import huydqpc07859.ecommerce.models.dto.ProviderRequest;
import huydqpc07859.ecommerce.models.user.*;
import huydqpc07859.ecommerce.models.dto.RegisterRequest;
import huydqpc07859.ecommerce.repositories.ConfirmTokenRepository;
import huydqpc07859.ecommerce.repositories.ForgetPasswordTokenRepository;
import huydqpc07859.ecommerce.repositories.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Provider;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final ConfirmTokenService confirmTokenService;
    private final MailService mailService;
    private final ConfirmTokenRepository confirmTokenRepository;
    private final ForgetPasswordTokenService forgetPasswordTokenService;
    private final ForgetPasswordTokenRepository forgetPasswordTokenRepository;
    private final String clientURL = "http://localhost:3000";


    public CommonResponse register(RegisterRequest req) {
        if(userRepository.findByEmail(req.getEmail()).isPresent()){
            throw new RuntimeException("Email is already taken");
        }

        User user = User.builder()
                .email(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword()))
                .role(Role.BUYER)
                .build();

        UserInfo userInfo = UserInfo.builder()
                .fullName(req.getFullName())
                .address(req.getAddress())
                .phoneNumber(req.getPhoneNumber())
                .build();


        user.setUserInfo(userInfo);
        userRepository.save(user);
        String uuid = UUID.randomUUID().toString();
        var token = ConfirmToken.builder().token(uuid)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .user(user)
                .build();
        confirmTokenRepository.save(token);

        String link = "http://localhost:9090/api/auth/confirm?token=" + token.getToken();
        mailService.send(
                req.getEmail(),
                buildEmail(req.getFullName(), link));

        return new CommonResponse("Register successfully");
    }

    public CommonResponse login(LoginRequest req)
    {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword())
            );

            User user = (User) authentication.getPrincipal();

            Map<String, Object> claims = new HashMap<>();
            claims.put("email", user.getUsername());
            claims.put("roles", user.getAuthorities());
            String token = jwtService.generateJWT(claims, user);

            return new CommonResponse(token);

        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Invalid username or password", e);
        }
    }

    public CommonResponse logout() {
        return new CommonResponse("Logout successfully");
    }

    public void confirmToken(String token, HttpServletResponse res){
        confirmTokenService.confirmTokenForEmail(token);
        res.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
        res.setHeader("Location", clientURL + "/login");
    }

    private String buildEmail(String name, String link) {
        return "<div style=\"font-family:Helvetica,Arial,sans-serif;font-size:16px;margin:0;color:#0b0c0c\">\n" +
                "\n" +
                "<span style=\"display:none;font-size:1px;color:#fff;max-height:0\"></span>\n" +
                "\n" +
                "  <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;min-width:100%;width:100%!important\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">\n" +
                "    <tbody><tr>\n" +
                "      <td width=\"100%\" height=\"53\" bgcolor=\"#0b0c0c\">\n" +
                "        \n" +
                "        <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;max-width:580px\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" align=\"center\">\n" +
                "          <tbody><tr>\n" +
                "            <td width=\"70\" bgcolor=\"#0b0c0c\" valign=\"middle\">\n" +
                "                <table role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n" +
                "                  <tbody><tr>\n" +
                "                    <td style=\"padding-left:10px\">\n" +
                "                  \n" +
                "                    </td>\n" +
                "                    <td style=\"font-size:28px;line-height:1.315789474;Margin-top:4px;padding-left:10px\">\n" +
                "                      <span style=\"font-family:Helvetica,Arial,sans-serif;font-weight:700;color:#ffffff;text-decoration:none;vertical-align:top;display:inline-block\">Confirm your email</span>\n" +
                "                    </td>\n" +
                "                  </tr>\n" +
                "                </tbody></table>\n" +
                "              </a>\n" +
                "            </td>\n" +
                "          </tr>\n" +
                "        </tbody></table>\n" +
                "        \n" +
                "      </td>\n" +
                "    </tr>\n" +
                "  </tbody></table>\n" +
                "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">\n" +
                "    <tbody><tr>\n" +
                "      <td width=\"10\" height=\"10\" valign=\"middle\"></td>\n" +
                "      <td>\n" +
                "        \n" +
                "                <table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n" +
                "                  <tbody><tr>\n" +
                "                    <td bgcolor=\"#1D70B8\" width=\"100%\" height=\"10\"></td>\n" +
                "                  </tr>\n" +
                "                </tbody></table>\n" +
                "        \n" +
                "      </td>\n" +
                "      <td width=\"10\" valign=\"middle\" height=\"10\"></td>\n" +
                "    </tr>\n" +
                "  </tbody></table>\n" +
                "\n" +
                "\n" +
                "\n" +
                "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">\n" +
                "    <tbody><tr>\n" +
                "      <td height=\"30\"><br></td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "      <td width=\"10\" valign=\"middle\"><br></td>\n" +
                "      <td style=\"font-family:Helvetica,Arial,sans-serif;font-size:19px;line-height:1.315789474;max-width:560px\">\n" +
                "        \n" +
                "            <p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\">Hi " + name + ",</p><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> Thank you for registering. Please click on the below link to activate your account: </p><blockquote style=\"Margin:0 0 20px 0;border-left:10px solid #b1b4b6;padding:15px 0 0.1px 15px;font-size:19px;line-height:25px\"><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> <a href=\"" + link + "\">Activate Now</a> </p></blockquote>\n Link will expire in 15 minutes. <p>See you soon</p>" +
                "        \n" +
                "      </td>\n" +
                "      <td width=\"10\" valign=\"middle\"><br></td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "      <td height=\"30\"><br></td>\n" +
                "    </tr>\n" +
                "  </tbody></table><div class=\"yj6qo\"></div><div class=\"adL\">\n" +
                "\n" +
                "</div></div>";
    }

    private String buildEmailForForgetPassword(String name, String link) {
        return "<div style=\"font-family:Helvetica, Arial, sans-serif; font-size:16px; margin:0; color:#0b0c0c\">\n" +
                "  <table role=\"presentation\" width=\"100%\" style=\"border-collapse: collapse; min-width:100%; width:100%!important\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">\n" +
                "    <tbody>\n" +
                "      <tr>\n" +
                "        <td width=\"100%\" height=\"53\" bgcolor=\"#0b0c0c\">\n" +
                "          <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;max-width:580px\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" align=\"center\">\n" +
                "            <tr>\n" +
                "              <td width=\"70\" bgcolor=\"#0b0c0c\" valign=\"middle\">\n" +
                "                <table role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n" +
                "                  <tr>\n" +
                "                    <td style=\"padding-left:10px\">\n" +
                "                    </td>\n" +
                "                    <td style=\"font-size:28px; line-height:1.315789474; Margin-top:4px; padding-left:10px\">\n" +
                "                      <span style=\"font-family:Helvetica, Arial, sans-serif; font-weight:700; color:#ffffff; text-decoration:none; vertical-align:top; display:inline-block\">Password Reset</span>\n" +
                "                    </td>\n" +
                "                  </tr>\n" +
                "                </table>\n" +
                "              </td>\n" +
                "            </tr>\n" +
                "          </table>\n" +
                "        </td>\n" +
                "      </tr>\n" +
                "    </tbody>\n" +
                "  </table>\n" +
                "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">\n" +
                "    <tbody>\n" +
                "      <tr>\n" +
                "        <td width=\"10\" height=\"10\" valign=\"middle\"></td>\n" +
                "        <td>\n" +
                "          <table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n" +
                "            <tr>\n" +
                "              <td bgcolor=\"#1D70B8\" width=\"100%\" height=\"10\"></td>\n" +
                "            </tr>\n" +
                "          </table>\n" +
                "        </td>\n" +
                "        <td width=\"10\" valign=\"middle\" height=\"10\"></td>\n" +
                "      </tr>\n" +
                "    </tbody>\n" +
                "  </table>\n" +
                "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">\n" +
                "    <tbody>\n" +
                "      <tr>\n" +
                "        <td height=\"30\"><br></td>\n" +
                "      </tr>\n" +
                "      <tr>\n" +
                "        <td width=\"10\" valign=\"middle\"><br></td>\n" +
                "        <td style=\"font-family:Helvetica, Arial, sans-serif; font-size:19px; line-height:1.315789474; max-width:560px\">\n" +
                "          <p style=\"Margin:0 0 20px 0; font-size:19px; line-height:25px; color:#0b0c0c\">\n" +
                "            Hi " + name + ",\n" +
                "          </p>\n" +
                "          <p style=\"Margin:0 0 20px 0; font-size:19px; line-height:25px; color:#0b0c0c\">\n" +
                "            You requested a password reset. Click the link below to reset your password:\n" +
                "          </p>\n" +
                "          <blockquote style=\"Margin:0 0 20px 0; border-left:10px solid #b1b4b6; padding:15px 0 0.1px 15px; font-size:19px; line-height:25px\">\n" +
                "            <p style=\"Margin:0 0 20px 0; font-size:19px; line-height:25px; color:#0b0c0c\">\n" +
                "              <a href=\"" + link + "\">Reset Password</a>\n" +
                "            </p>\n" +
                "          </blockquote>\n" +
                "          Link will expire in 15 minutes.\n" +
                "          <p>See you soon</p>\n" +
                "        </td>\n" +
                "        <td width=\"10\" valign=\"middle\"><br></td>\n" +
                "      </tr>\n" +
                "      <tr>\n" +
                "        <td height=\"30\"><br></td>\n" +
                "      </tr>\n" +
                "    </tbody>\n" +
                "  </table>\n" +
                "</div>";
    }


    public void confirmForgetPassword(String token, HttpServletResponse res) {
        forgetPasswordTokenService.confirmForgetPasswordTokenForEmail(token);
        ForgetPasswordToken forgetToken =
                forgetPasswordTokenRepository.findByToken(token)
                        .orElseThrow(() -> new RuntimeException("The forget password token is not found"));

        res.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
        res.setHeader("Location", clientURL +"/change-password-wtk?token=" + forgetToken.getToken()+"&email="+forgetToken.getUser().getUsername());
//        return new CommonResponse("Confirm forget password token successfully");
    }

    public CommonResponse sendToEmailForgetPasswordToken(String email) {
        System.out.println(email);
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("Email not found"));

        String uuid = UUID.randomUUID().toString();
        var token = ForgetPasswordToken.builder().token(uuid)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .user(user)
                .build();
        forgetPasswordTokenRepository.save(token);

        String link = "http://localhost:9090/api/auth/forget-password-token?token=" + token.getToken();
        mailService.send(
                user.getEmail(),
                buildEmailForForgetPassword(user.getUserInfo().getFullName(), link));

        return new CommonResponse("Waiting for you confirmed the token");
    }

    public CommonResponse changePasswordWithForgetPassword(String token, String newPassword){
        ForgetPasswordToken forgetToken =
                forgetPasswordTokenRepository.findByToken(token)
                        .orElseThrow(() -> new RuntimeException("The forget password token is not found"));

        if(forgetToken.getConfirmedAt() == null){
            throw new RuntimeException("You have not confirmed by email");
        }

        forgetToken.getUser().setPassword(passwordEncoder.encode(newPassword));
        forgetPasswordTokenRepository.save(forgetToken);

        return new CommonResponse("Change the password successfully");
    }

    public CommonResponse providerLogin(ProviderRequest req) {
         Optional<User> userOptional = userRepository.findByEmail(req.getEmail());
         if(userOptional.isPresent()){
             LoginRequest loginRequest = LoginRequest.builder()
                     .email(req.getEmail())
                     .password(req.getId()).build();
             return login(loginRequest);
         }else {
             UserInfo userInfo = UserInfo.builder()
                     .fullName(req.getFullName())
                     .build();

             User user = User.builder()
                     .email(req.getEmail())
                     .password(req.getId())
                     .enable(true)
                     .role(Role.BUYER)
                     .userInfo(userInfo)
                     .provider(req.getProvider())
                     .build();

             userRepository.save(user);
             LoginRequest loginRequest = LoginRequest.builder()
                     .email(user.getEmail())
                     .password(user.getPassword())
                     .build();

             return login(loginRequest);
         }

    }
}
