package pokssak.gsg.domain.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import pokssak.gsg.common.exception.CustomException;
import pokssak.gsg.common.s3.S3Uploader;
import pokssak.gsg.domain.bookmark.dto.BookmarkResponse;
import pokssak.gsg.domain.bookmark.repository.BookmarkRepository;
import pokssak.gsg.domain.bookmark.service.BookmarkService;
import pokssak.gsg.domain.review.dto.ReviewResponse;
import pokssak.gsg.domain.review.repository.ReviewRepository;
import pokssak.gsg.domain.review.service.ReviewService;
import pokssak.gsg.domain.user.dto.UserProfileResponse;
import pokssak.gsg.domain.user.dto.UserUpdateRequest;
import pokssak.gsg.domain.user.entity.User;
import pokssak.gsg.domain.user.exception.UserErrorCode;
import pokssak.gsg.domain.user.repository.UserRepository;

import java.awt.print.Book;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService{

    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private final BookmarkService bookmarkService;
    private final PasswordEncoder encoder;
    private final UserKeywordService userKeywordService;
    private final S3Uploader s3Uploader;

//    // 회원가입
//    public UserResponse register(UserRegisterRequest request, String imageUrl) {
//        log.info("회원가입 요청 - email={}, nickname={}", request.email(), request.nickName());
//
//        if (userRepository.existsByEmail(request.email())) {
//            log.warn("회원가입 실패 - 중복 이메일: {}", request.email());
//            throw new CustomException(UserErrorCode.USER_EMAIL_ALREADY_EXIST);
//        }
//
//        User user = User.builder()
//                .nickName(request.nickName())
//                .email(request.email())
//                .password(encoder.encode(request.password()))
//                .imageUrl(imageUrl)
//                .joinType(request.joinType())
//                .build();
//
//        User savedUser = userRepository.save(user);
//
//        userKeywordService.addUserKeywords(savedUser.getId(), request.keywords());
//
//        log.info("회원가입 성공 - userId={}, email={}", savedUser.getId(), savedUser.getEmail());
//        return UserResponse.from(savedUser);
//    }

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
    public void updateProfile(Long userId, UserUpdateRequest userUpdateRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(UserErrorCode.NOT_FOUND));

        //키워드 수정
        userKeywordService.updateUserKeywords(userId, userUpdateRequest.keywords());

        //닉네임 수정
        user.updateProfile(userUpdateRequest);
    }

    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(UserErrorCode.NOT_FOUND));
    }

    // 프로필 이미지 수정
    @Transactional
    public void updateProfileImage(Long userId, MultipartFile image) {

        log.info("프로필 이미지 수정 userId={}, imageName={}, imageType={}, imageSize={}"
                ,image.getOriginalFilename(), image.getContentType(), image.getSize());

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(UserErrorCode.NOT_FOUND));

        String imageUrl = s3Uploader.upload(image);
        user.updateProfileImage(imageUrl);
    }

    /** 내 리뷰 조회 */
    @Transactional(readOnly = true)
    public List<ReviewResponse> getMyReviews(Long userId) {
        log.info("내 리뷰 조회 userId={}", userId);
        return reviewRepository.findByUserId(userId).stream()
                .map(review -> ReviewResponse.from(review,false)).toList();
    }

    private final BookmarkRepository bookmarkRepository;
    /** 내 북마크 조회 */
    @Transactional(readOnly = true)
    public List<BookmarkResponse> getMyBookmarks(Long userId) {
        log.info("내 북마크 조회 userId={}", userId);
        return bookmarkRepository.findByUserId(userId).stream()
                .map(BookmarkResponse::from).toList();
    }
}
