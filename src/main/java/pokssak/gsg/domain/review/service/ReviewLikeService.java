package pokssak.gsg.domain.review.service;

import jakarta.persistence.Cacheable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pokssak.gsg.common.exception.CustomException;
import pokssak.gsg.domain.review.entity.ReviewLike;
import pokssak.gsg.domain.review.exception.ReviewErrorCode;
import pokssak.gsg.domain.review.exception.ReviewLikeErrorCode;
import pokssak.gsg.domain.review.repository.ReviewLikeRepository;
import pokssak.gsg.domain.review.repository.ReviewRepository;
import pokssak.gsg.domain.user.exception.UserErrorCode;
import pokssak.gsg.domain.user.repository.UserRepository;

@Slf4j
@RequiredArgsConstructor
@Service
public class ReviewLikeService {

    private final ReviewLikeRepository reviewLikeRepository;
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;

    /** 리뷰 토글 */
    public void toggle(Long userId, Long reviewId) {
        var reviewLike = reviewLikeRepository.findByUserIdAndReviewId(userId, reviewId);
        if(reviewLike.isPresent()) {
            cancel(reviewLike.get());
        } else {
            like(userId, reviewId);
        }
    }

    /** 리뷰 좋아요 */
    private void like(Long userId, Long reviewId) {
        log.info("리뷰 좋아요 userId={} reviewId={}",userId, reviewId);
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(UserErrorCode.NOT_FOUND));
        var review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new CustomException(ReviewErrorCode.NOT_FOUND));
        var reviewLike = ReviewLike.builder()
                .user(user)
                .review(review)
                .build();

        reviewLikeRepository.save(reviewLike);
    }

    /** 리뷰 좋아요 취소 */
    private void cancel(ReviewLike reviewLike) {
        log.info("리뷰 좋아요 취소 reviewLike={}",reviewLike);
        reviewLikeRepository.delete(reviewLike);
    }

    /** 리뷰 좋아요 수 조회*/
//    @Cacheable(value = "likeCount", key = "#reviewId")
    public Long getLikeCount(Long reviewId) {
        return 0L;
    }
}
