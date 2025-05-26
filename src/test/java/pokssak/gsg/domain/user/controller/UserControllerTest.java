package pokssak.gsg.domain.user.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import pokssak.gsg.common.s3.S3Uploader;
import pokssak.gsg.domain.user.dto.UserProfileResponse;
import pokssak.gsg.domain.user.dto.UserRegisterRequest;
import pokssak.gsg.domain.user.dto.UserResponse;
import pokssak.gsg.domain.user.entity.JoinType;
import pokssak.gsg.domain.user.entity.User;
import pokssak.gsg.domain.user.entity.UserKeyword;
import pokssak.gsg.domain.user.service.UserService;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private S3Uploader s3Uploader;

    @InjectMocks
    private UserController userController;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .email("test@example.com")
                .nickName("tester")
                .imageUrl("https://image.com/test.png")
                .joinType(JoinType.LOCAL)
                .userKeywords(List.of())
                .build();
    }

    @Test
    @DisplayName("회원가입 성공")
    void register_success() throws Exception {
        UserRegisterRequest request = UserRegisterRequest.builder()
                .email("test@example.com")
                .password("securePassword")
                .nickName("tester")
                .joinType(JoinType.LOCAL)
                .keywords(List.of())
                .build();

        MockMultipartFile image = new MockMultipartFile(
                "image", "profile.png", "image/png", "image-bytes".getBytes()
        );

        String uploadedImageUrl = "https://s3.com/profile.png";
        UserResponse expectedResponse = UserResponse.builder()
                .nickName("tester")
                .email("test@example.com")
                .imageUrl(uploadedImageUrl)
                .joinType(JoinType.LOCAL)
                .userKeywords(List.of())
                .build();

        when(s3Uploader.upload(image)).thenReturn(uploadedImageUrl);
        when(userService.register(request, uploadedImageUrl)).thenReturn(expectedResponse);

        // when
        ResponseEntity<UserResponse> response = userController.register(request, image);

        // then
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(expectedResponse);
    }

    @Test
    @DisplayName("회원가입 성공 - 이미지 없이")
    void register_success_without_image() throws Exception {
        UserRegisterRequest request = UserRegisterRequest.builder()
                .email("noimage@example.com")
                .password("securePassword")
                .nickName("noimage")
                .joinType(JoinType.LOCAL)
                .keywords(List.of())
                .build();

        String emptyImageUrl = "";  // 이미지가 없는 경우 ""로 처리
        UserResponse expectedResponse = UserResponse.builder()
                .nickName("noimage")
                .email("noimage@example.com")
                .imageUrl(emptyImageUrl)
                .joinType(JoinType.LOCAL)
                .userKeywords(List.of())
                .build();

        // s3Uploader.upload는 호출되지 않아야 하므로 설정하지 않음
        when(userService.register(request, emptyImageUrl)).thenReturn(expectedResponse);

        // when: 이미지 없이 호출
        ResponseEntity<UserResponse> response = userController.register(request, null);

        // then
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(expectedResponse);

        // s3Uploader.upload는 호출되지 않아야 함
        verify(s3Uploader, never()).upload(any());
    }

    @Test
    @DisplayName("회원가입 성공 - 이미지 파일은 있지만 내용이 비어 있음")
    void register_success_with_empty_image() throws Exception {
        UserRegisterRequest request = UserRegisterRequest.builder()
                .email("emptyimage@example.com")
                .password("securePassword")
                .nickName("emptyuser")
                .joinType(JoinType.LOCAL)
                .keywords(List.of())
                .build();

        // 내용이 비어 있는 이미지 파일 생성
        MockMultipartFile emptyImage = new MockMultipartFile(
                "image", "empty.png", "image/png", new byte[0]  // 빈 byte 배열
        );

        String emptyImageUrl = "";  // 내용이 비어 있으므로 업로드하지 않음
        UserResponse expectedResponse = UserResponse.builder()
                .nickName("emptyuser")
                .email("emptyimage@example.com")
                .imageUrl(emptyImageUrl)
                .joinType(JoinType.LOCAL)
                .userKeywords(List.of())
                .build();

        // userService만 호출되며, s3Uploader.upload는 호출되지 않아야 함
        when(userService.register(request, emptyImageUrl)).thenReturn(expectedResponse);

        // when: 내용이 비어 있는 이미지 파일과 함께 회원가입 요청
        ResponseEntity<UserResponse> response = userController.register(request, emptyImage);

        // then
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(expectedResponse);
        verify(s3Uploader, never()).upload(any());
    }



    @Test
    @DisplayName("회원 탈퇴 성공")
    void deleteUser_success() throws Exception {
        ResponseEntity<Void> response = userController.deleteUser(testUser);

        verify(userService).deleteUser(testUser.getId());
        assertThat(response.getStatusCodeValue()).isEqualTo(204);
    }



    @Test
    @DisplayName("회원 복구")
    void restore_user() {
        Long userId = 1L;

        ResponseEntity<Void> response = userController.restoreUser(userId);

        verify(userService).restoreUser(userId);
        assertThat(response.getStatusCodeValue()).isEqualTo(204);
    }

    @Test
    @DisplayName("프로필 조회 성공")
    void get_my_profile() {
        List<UserKeyword> mockKeywords = List.of(
                UserKeyword.builder().id(1L).build(),
                UserKeyword.builder().id(2L).build()
        );

        testUser = User.builder()
                .id(1L)
                .email("test@example.com")
                .nickName("tester")
                .imageUrl("https://img.com/profile.png")
                .joinType(JoinType.LOCAL)
                .userKeywords(mockKeywords)
                .build();

        UserProfileResponse expectedResponse = UserProfileResponse.builder()
                .id(testUser.getId())
                .nickName(testUser.getNickName())
                .email(testUser.getEmail())
                .imageUrl(testUser.getImageUrl())
                .joinType(testUser.getJoinType())
                .keywordIds(List.of(1L, 2L))
                .bookmarkCount(2)
                .reviewCount(1)
                .build();

        when(userService.getProfile(testUser.getId())).thenReturn(expectedResponse);

        ResponseEntity<UserProfileResponse> response = userController.getMyProfile(testUser);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(expectedResponse);
    }

    @Test
    @DisplayName("프로필 수정 성공")
    void updateProfile_success() throws Exception {
        String newNickName = "newName";
        MockMultipartFile newImage = new MockMultipartFile(
                "image", "new.png", "image/png", "new-image".getBytes()
        );

        ResponseEntity<Void> response = userController.updateProfile(testUser, newNickName, newImage);

        verify(userService).updateProfile(testUser.getId(), newNickName, newImage);
        assertThat(response.getStatusCodeValue()).isEqualTo(204);
    }

}
