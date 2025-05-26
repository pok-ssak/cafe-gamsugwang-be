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
import pokssak.gsg.domain.user.entity.User;
import pokssak.gsg.domain.user.service.UserService;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class UserController {

    private final UserService userService;
    private final S3Uploader s3Uploader;

//    // 회원가입
//    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public ResponseEntity<UserResponse> register(
//            @RequestPart("request") UserRegisterRequest request,
//            @RequestPart(value = "image", required = false) MultipartFile image
//    ) {
//        String imageUrl = (image != null && !image.isEmpty()) ? s3Uploader.upload(image) : "";
//
//        UserResponse response = userService.register(request, imageUrl);
//        return ResponseEntity.ok(response);
//    }


    // 회원탈퇴 (soft delete)
    @DeleteMapping("/users")
    public ResponseEntity<Void> deleteUser(@AuthenticationPrincipal User user) {
        userService.deleteUser(user.getId());
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
    public ResponseEntity<UserProfileResponse> getMyProfile(@AuthenticationPrincipal User user) {
        UserProfileResponse profile = userService.getProfile(user.getId());
        return ResponseEntity.ok(profile);
    }

    // 프로필 수정
    @PutMapping(value = "/users/profile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> updateProfile(
            @AuthenticationPrincipal User user,
            @RequestPart("nickName") String nickName,
            @RequestPart(value = "image", required = false) MultipartFile image) {

        userService.updateProfile(user.getId(), nickName, image);
        return ResponseEntity.noContent().build();
    }


}
