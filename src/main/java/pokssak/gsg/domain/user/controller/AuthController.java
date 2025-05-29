package pokssak.gsg.domain.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pokssak.gsg.common.dto.ApiResponse;
import pokssak.gsg.domain.user.dto.ConflictEmailCheckRequestDto;
import pokssak.gsg.domain.user.dto.LoginRequestDto;
import pokssak.gsg.domain.user.dto.OAuthCodeDto;
import pokssak.gsg.domain.user.dto.OAuthSignUpRequestDto;
import pokssak.gsg.domain.user.dto.SignupRequestDto;
import pokssak.gsg.domain.user.entity.JoinType;
import pokssak.gsg.domain.user.entity.User;
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

    @PostMapping("oauth/kakao")
    public ResponseEntity<?> kakaoLogin(@RequestBody OAuthCodeDto codeDto) {
        return ResponseEntity.ok(ApiResponse.ok(authService.oAuthSignUp(codeDto, JoinType.KAKAO)));
    }

    @PostMapping("oauth/naver")
    public ResponseEntity<?> naverLogin(@RequestBody OAuthCodeDto codeDto) {
        return ResponseEntity.ok(ApiResponse.ok(authService.oAuthSignUp(codeDto, JoinType.NAVER)));
    }

    @PostMapping("oauth/google")
    public ResponseEntity<?> googleLogin(@RequestBody OAuthCodeDto codeDto) {
        return ResponseEntity.ok(ApiResponse.ok(authService.oAuthSignUp(codeDto, JoinType.GOOGLE)));
    }

    @PostMapping("oauth/signup")
    public ResponseEntity<?> oauthSignup(@RequestBody OAuthSignUpRequestDto oAuthSignUpRequestDto,
                                         @AuthenticationPrincipal User user) {
        authService.oAuthRegister(oAuthSignUpRequestDto, user);
        return ResponseEntity.ok().build();
    }

    @PostMapping("logout")
    public ResponseEntity<?> localLogout(@RequestBody LoginRequestDto loginRequestDto) {
        return ResponseEntity.ok(ApiResponse.ok(null));
    }
}
