package pokssak.gsg.domain.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import pokssak.gsg.domain.user.dto.UserProfileResponse;
import pokssak.gsg.domain.user.dto.UserProfileUpdateRequest;
import pokssak.gsg.domain.user.dto.UserRegisterRequest;
import pokssak.gsg.domain.user.dto.UserResponse;
import pokssak.gsg.domain.user.service.UserService;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class UserController {

    private final UserService userService;

    // 회원가입
    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@RequestBody UserRegisterRequest request) {
        UserResponse response = userService.register(request);
        return ResponseEntity.ok(response);
    }

    // 회원탈퇴 (soft delete)
    @DeleteMapping("/users/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    // 회원복구
    @PutMapping("/users/{userId}")
    public ResponseEntity<Void> restoreUser(@PathVariable Long userId) {
        userService.restoreUser(userId);
        return ResponseEntity.noContent().build();
    }

    // 프로필 조회
    @GetMapping("/users/profile")
    public ResponseEntity<UserProfileResponse> getMyProfile(@AuthenticationPrincipal CustomUserDetails userDetails) {
        UserProfileResponse profile = userService.getProfile(userDetails.getUser());
        return ResponseEntity.ok(profile);
    }

    // 프로필 수정
    @PatchMapping("/users/profile")
    public ResponseEntity<Void> updateProfile(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody UserProfileUpdateRequest request) {
        userService.updateProfile(userDetails.getUser(), request);
        return ResponseEntity.noContent().build();
    }


}
