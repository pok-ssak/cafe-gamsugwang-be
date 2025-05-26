package pokssak.gsg.domain.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.web.multipart.MultipartFile;
import pokssak.gsg.common.exception.CustomException;
import pokssak.gsg.common.s3.S3Uploader;
import pokssak.gsg.common.vo.Keyword;
import pokssak.gsg.domain.user.dto.UserProfileResponse;
import pokssak.gsg.domain.user.dto.UserRegisterRequest;
import pokssak.gsg.domain.user.dto.UserResponse;
import pokssak.gsg.domain.user.entity.User;
import pokssak.gsg.domain.user.entity.JoinType;
import pokssak.gsg.domain.user.exception.UserErrorCode;
import pokssak.gsg.domain.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private UserKeywordService userKeywordService;
    @Mock private S3Uploader s3Uploader;

    @InjectMocks
    private UserService userService;

    private UserRegisterRequest registerRequest;
    private User user;

    @BeforeEach
    void setUp() {
        List<Keyword> keywords = List.of(
                Keyword.builder().word("Java").count(1L).build(),
                Keyword.builder().word("Spring").count(1L).build()
        );

        registerRequest = UserRegisterRequest.builder()
                .nickName("testuser")
                .email("test@example.com")
                .password("plaintext")
                .joinType(JoinType.LOCAL)
                .keywords(keywords)
                .build();

        user = User.builder()
                .id(1L)
                .nickName("testuser")
                .email("test@example.com")
                .password("plaintext")
                .imageUrl("http://image.com/profile.jpg")
                .joinType(JoinType.LOCAL)
                .userKeywords(new ArrayList<>())
                .build();
    }

    @Test
    @DisplayName("회원가입 성공")
    void register_success() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        String imageUrl = "http://image.com/profile.jpg";

        User savedUser = User.builder()
                .id(1L)  // id 직접 세팅
                .nickName(registerRequest.nickName())
                .email(registerRequest.email())
                .password("encodedPassword")
                .imageUrl(imageUrl)
                .joinType(registerRequest.joinType())
                .build();

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        UserResponse response = userService.register(registerRequest, imageUrl);

        assertThat(response.nickName()).isEqualTo("testuser");
        assertThat(response.email()).isEqualTo("test@example.com");
        verify(userKeywordService).addUserKeywords(eq(1L), eq(registerRequest.keywords()));
    }

    @Test
    @DisplayName("회원가입 실패 - 이메일 중복")
    void register_fail_duplicateEmail() {
        String imageUrl = "http://image.com/profile.jpg";
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        CustomException exception = assertThrows(CustomException.class, () -> {
            userService.register(registerRequest, imageUrl);
        });

        assertThat(exception.getErrorCode()).isEqualTo(UserErrorCode.USER_EMAIL_ALREADY_EXIST);
    }

    @Test
    @DisplayName("회원탈퇴 성공")
    void deleteUser_success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.deleteUser(1L);

        verify(userRepository).delete(user);
    }

    @Test
    @DisplayName("회원탈퇴 실패 - 사용자 없음")
    void deleteUser_fail_notFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        CustomException exception = assertThrows(CustomException.class, () -> {
            userService.deleteUser(1L);
        });

        assertThat(exception.getErrorCode()).isEqualTo(UserErrorCode.NOT_FOUND);
    }

    @Test
    @DisplayName("회원복구 성공")
    void restoreUser_success() {
        // given: 이미 soft delete된 사용자
        User deletedUser = User.builder()
                .id(1L)
                .email("deleted@example.com")
                .nickName("deletedUser")
                .password("encodedPassword")
                .imageUrl("http://image.com/deleted.jpg")
                .joinType(JoinType.LOCAL)
                .isDeleted(true)  // ← 여기 중요: 이미 삭제된 상태로 세팅
                .build();

        when(userRepository.findByIdIncludeDeleted(1L)).thenReturn(Optional.of(deletedUser));

        // when
        userService.restoreUser(1L);

        // then
        assertThat(deletedUser.isDeleted()).isFalse();
        verify(userRepository).findByIdIncludeDeleted(1L);
    }


    @Test
    @DisplayName("회원복구 실패 - 이미 활성 상태")
    void restoreUser_fail_alreadyActive() {
        when(userRepository.findByIdIncludeDeleted(1L)).thenReturn(Optional.of(user));

        CustomException exception = assertThrows(CustomException.class, () -> {
            userService.restoreUser(1L);
        });

        assertThat(exception.getErrorCode()).isEqualTo(UserErrorCode.ALREADY_ACTIVE);
    }

    @Test
    @DisplayName("프로필 조회 성공")
    void getProfile_success() {
        // given
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // when
        UserProfileResponse response = userService.getProfile(1L);

        // then
        assertThat(response.email()).isEqualTo("test@example.com");
        assertThat(response.nickName()).isEqualTo("testuser");
        assertThat(response.imageUrl()).isEqualTo("http://image.com/profile.jpg");
        verify(userRepository).findById(1L);
    }

    @Test
    @DisplayName("프로필 조회 실패 - 사용자 없음")
    void getProfile_fail_notFound() {
        // given
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // when
        CustomException exception = assertThrows(CustomException.class, () -> {
            userService.getProfile(1L);
        });

        // then
        assertThat(exception.getErrorCode()).isEqualTo(UserErrorCode.NOT_FOUND);
        verify(userRepository).findById(1L);
    }

    @Test
    @DisplayName("프로필 수정 성공")
    void updateProfile_success() {
        // given
        MultipartFile mockImage = mock(MultipartFile.class);
        String newNickName = "updatedUser";
        String newImageUrl = "http://image.com/updated.jpg";

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(s3Uploader.upload(mockImage)).thenReturn(newImageUrl);

        // when
        userService.updateProfile(1L, newNickName, mockImage);

        // then
        assertThat(user.getNickName()).isEqualTo(newNickName);
        assertThat(user.getImageUrl()).isEqualTo(newImageUrl);
        verify(userRepository).findById(1L);
        verify(s3Uploader).upload(mockImage);
    }

    @Test
    @DisplayName("프로필 수정 성공 - 이미지 업로드 없이 닉네임만 수정")
    void updateProfile_success_withoutImage() {
        // given
        String newNickName = "nicknameOnlyUpdated";

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // when
        userService.updateProfile(1L, newNickName, null);

        // then
        assertThat(user.getNickName()).isEqualTo(newNickName);
        assertThat(user.getImageUrl()).isEqualTo("http://image.com/profile.jpg"); // 기존 유지
        verify(userRepository).findById(1L);
    }

    @Test
    @DisplayName("프로필 수정 실패 - 사용자 없음")
    void updateProfile_fail_notFound() {
        // given
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // when
        CustomException exception = assertThrows(CustomException.class, () -> {
            userService.updateProfile(1L, "newNick", null);
        });

        // then
        assertThat(exception.getErrorCode()).isEqualTo(UserErrorCode.NOT_FOUND);
        verify(userRepository).findById(1L);
    }

}
