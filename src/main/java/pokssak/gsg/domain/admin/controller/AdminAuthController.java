package pokssak.gsg.domain.admin.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pokssak.gsg.common.jwt.JwtTokenDto;
import pokssak.gsg.domain.admin.dto.AdminLoginRequest;
import pokssak.gsg.domain.admin.service.AdminAuthService;

@RestController
@RequestMapping("/admin/auth")
@RequiredArgsConstructor
public class AdminAuthController {

    private final AdminAuthService adminAuthService;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody AdminLoginRequest request) {
        JwtTokenDto token = adminAuthService.login(request);
        return ResponseEntity.ok(token.accessToken());
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String bearerToken) {
        String token = bearerToken.replace("Bearer ", "");
        adminAuthService.logout(token);
        return ResponseEntity.noContent().build();
    }
}
