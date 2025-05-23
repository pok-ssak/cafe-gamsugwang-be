package pokssak.gsg.domain.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import pokssak.gsg.common.exception.CustomException;
import pokssak.gsg.common.s3.S3Uploader;
import pokssak.gsg.domain.user.dto.UserProfileResponse;
import pokssak.gsg.domain.user.dto.UserRegisterRequest;
import pokssak.gsg.domain.user.dto.UserResponse;
import pokssak.gsg.domain.user.entity.User;
import pokssak.gsg.domain.user.exception.UserErrorCode;
import pokssak.gsg.domain.user.repository.UserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService{

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final UserKeywordService userKeywordService;
    private final S3Uploader s3Uploader;

    // 회원가입
    public UserResponse register(UserRegisterRequest request) {
        log.info("회원가입 요청 - email={}, nickname={}", request.email(), request.nickName());

        if (userRepository.existsByEmail(request.email())) {
            log.warn("회원가입 실패 - 중복 이메일: {}", request.email());
            throw new CustomException(UserErrorCode.USER_EMAIL_ALREADY_EXIST);
        }

        User user = User.builder()
                .nickName(request.nickName())
                .email(request.email())
                .password(encoder.encode(request.password()))
                .imageUrl(request.imageUrl())
                .joinType(request.joinType())
                .build();

        User savedUser = userRepository.save(user);

        userKeywordService.addUserKeywords(savedUser.getId(), request.keywords());

        log.info("회원가입 성공 - userId={}, email={}", savedUser.getId(), savedUser.getEmail());
        return UserResponse.from(savedUser);
    }

    // 회원탈퇴
    public void deleteUser(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(UserErrorCode.NOT_FOUND));

        userRepository.delete(user);
        log.info("회원탈퇴 성공 - userId={}", user.getId());
    }

    // 회원복구
    @Transactional
    public void restoreUser(Long userId) {
        User user = userRepository.findByIdIncludeDeleted(userId)
                .orElseThrow(() -> new CustomException(UserErrorCode.NOT_FOUND));

        if (!user.isDeleted()) {
            throw new CustomException(UserErrorCode.ALREADY_ACTIVE);
        }

        user.restore();
        log.info("회원복구 성공 - userId={}", user.getId());
    }

    // 프로필 조회
    @Transactional(readOnly = true)
    public UserProfileResponse getProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(UserErrorCode.NOT_FOUND));

        return UserProfileResponse.from(user);
    }

    // 프로필 수정
    @Transactional
    public void updateProfile(Long userId, String nickName, MultipartFile image) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(UserErrorCode.NOT_FOUND));

        String imageUrl = (image != null) ? s3Uploader.upload(image) : user.getImageUrl();
        user.updateProfile(nickName, imageUrl);
    }

}
