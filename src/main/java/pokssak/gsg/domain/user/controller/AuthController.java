package pokssak.gsg.domain.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pokssak.gsg.common.dto.ApiResponse;
import pokssak.gsg.domain.user.dto.ConflictEmailCheckRequestDto;
import pokssak.gsg.domain.user.dto.LoginRequestDto;
import pokssak.gsg.domain.user.dto.SignupRequestDto;
import pokssak.gsg.domain.user.service.AuthService;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("email-validate")
    public ResponseEntity<?> conflictEmailCheck(
        @RequestBody ConflictEmailCheckRequestDto conflictEmailCheckRequestDto) {
        authService.conflictEmailCheck(conflictEmailCheckRequestDto);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    @PostMapping("signup")
    public ResponseEntity<?> localSignup(@RequestBody SignupRequestDto signupRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.ok(authService.localSignup(signupRequestDto)));
    }

    @PostMapping("login")
    public ResponseEntity<?> localLogin(@RequestBody LoginRequestDto loginRequestDto) {
        return ResponseEntity.ok(ApiResponse.ok(authService.localLogin(loginRequestDto)));
    }

    @PostMapping("logout")
    public ResponseEntity<?> localLogout(@RequestBody LoginRequestDto loginRequestDto) {
        return ResponseEntity.ok(ApiResponse.ok(null));
    }
}
