package huydqpc07859.ecommerce.controllers;

import huydqpc07859.ecommerce.models.dto.*;
import huydqpc07859.ecommerce.models.user.User;
import huydqpc07859.ecommerce.services.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private AuthService authService;


    @PostMapping("/register")
    public ResponseEntity<CommonResponse> register(@Valid @RequestBody RegisterRequest req) {
         return ResponseEntity.ok(authService.register(req));
    }

    @PostMapping("/login")
    public ResponseEntity<CommonResponse> login(@Validated @RequestBody LoginRequest req) {
        return ResponseEntity.ok(authService.login(req));
    }

    @PostMapping("/provider/login")
    public ResponseEntity<CommonResponse> providerLogin(@Validated @RequestBody ProviderRequest req){
        return ResponseEntity.ok(authService.providerLogin(req));

    }

    @PostMapping("/logout")
    public ResponseEntity<CommonResponse> logout() {
        return ResponseEntity.ok(authService.logout());
    }

    @GetMapping("/confirm")
    public void confirmToken( @RequestParam("token") String confirmToken,
                                                        HttpServletResponse res) {
        authService.confirmToken(confirmToken, res);
    }

    @GetMapping("/send-forget-password-token/{email}")
    public ResponseEntity<CommonResponse> sendToEmailForgetPasswordToken(@PathVariable String email) {
        return ResponseEntity.ok(authService.sendToEmailForgetPasswordToken(email));
    }

    @GetMapping("/forget-password-token")
    public void confirmEmailForgetPasswordToken(@RequestParam("token") String token, HttpServletResponse res) {
//        return ResponseEntity.ok(authService.confirmForgetPassword(token, res));
        authService.confirmForgetPassword(token, res);
    }

    @PostMapping("/change-password-with-forget-password-token")
    public ResponseEntity<CommonResponse> changePasswordWithForgetPasswordToken
            (@RequestParam("token") String token, @RequestBody NewPassword newPassword) {
        return ResponseEntity
                .ok(authService.changePasswordWithForgetPassword(token, newPassword.getNewPassword()));
    }

    @GetMapping("/redirect")
    public void redirect(HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
        response.setHeader("Location", "http://localhost:3000");
    }

}
