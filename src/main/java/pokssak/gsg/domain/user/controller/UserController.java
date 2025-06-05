package pokssak.gsg.domain.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pokssak.gsg.common.s3.S3Uploader;
import pokssak.gsg.domain.user.dto.UserProfileResponse;
import pokssak.gsg.domain.user.dto.UserRegisterRequest;
import pokssak.gsg.domain.user.dto.UserResponse;
import pokssak.gsg.domain.user.dto.UserUpdateRequest;
import pokssak.gsg.domain.user.entity.User;
import pokssak.gsg.domain.user.service.UserService;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class UserController {

    private final UserService userService;

    // 회원탈퇴 (soft delete)
    @DeleteMapping("/users")
    public ResponseEntity<Void> deleteUser(@AuthenticationPrincipal User user) {
        userService.deleteUser(user.getId());
        return ResponseEntity.noContent().build();
    }

    // 회원복구
    @PutMapping("/users/{userId}/restore")
    public ResponseEntity<Void> restoreUser(@PathVariable Long userId) {
        userService.restoreUser(userId);
        return ResponseEntity.noContent().build();
    }

    // 프로필 조회
    @GetMapping("/users/profile")
    public ResponseEntity<UserProfileResponse> getMyProfile(@AuthenticationPrincipal User user) {
        UserProfileResponse profile = userService.getProfile(user.getId());
        return ResponseEntity.ok(profile);
    }

    // 프로필 수정
    @PutMapping("/users/profile")
    public ResponseEntity<Void> updateProfile(
            @AuthenticationPrincipal User user,
            @RequestBody UserUpdateRequest userUpdateRequest
    ) {
        userService.updateProfile(user.getId(), userUpdateRequest);
        return ResponseEntity.noContent().build();
    }

    /** 프로필 이미지 */
    @PatchMapping(value = "/users/profile/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> updateProfileImage(
            @AuthenticationPrincipal User user,
            @RequestPart(value = "image", required = true) MultipartFile image
    ) {
        userService.updateProfileImage(user.getId(), image);
        return ResponseEntity.noContent().build();
    }

    /** 유저 리뷰 조회 */
    @GetMapping("/users/my/reviews")
    public ResponseEntity<?> getMyReviews(
            @AuthenticationPrincipal User user
    ) {
        var result = userService.getMyReviews(user.getId());
        return ResponseEntity.ok(result);
    }

    /** 유저 북마크 조회 */
    @GetMapping("/users/my/bookmarks")
    public ResponseEntity<?> getMyBookmarks(
            @AuthenticationPrincipal User user
    ) {
        var result = userService.getMyBookmarks(user.getId());
        return ResponseEntity.ok(result);
    }

}
