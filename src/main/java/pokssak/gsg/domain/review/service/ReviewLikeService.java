package pokssak.gsg.domain.review.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pokssak.gsg.common.exception.CustomException;
import pokssak.gsg.domain.review.entity.ReviewLike;
import pokssak.gsg.domain.review.exception.ReviewErrorCode;
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
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String LIKE_COUNT_KEY_PREFIX = "review:like:count:";

    /** 리뷰 좋아요 토글 */
    @Transactional
    public Long toggle(Long userId, Long reviewId) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(UserErrorCode.NOT_FOUND));
        var review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new CustomException(ReviewErrorCode.NOT_FOUND));

        var existingLike = reviewLikeRepository.findByUserIdAndReviewId(user.getId(), reviewId);
        String likeCountKey = LIKE_COUNT_KEY_PREFIX + reviewId;

        if (existingLike.isPresent()) {
            // 좋아요 취소
            reviewLikeRepository.delete(existingLike.get());
            redisTemplate.opsForValue().decrement(likeCountKey);
            review.updateLikeCount(Math.max(0, review.getLikeCount() - 1));
            log.info("리뷰 좋아요 취소 userId={} reviewId={}", user.getId(), reviewId);
            return review.getLikeCount();
        } else {
            // 좋아요 추가
            var reviewLike = ReviewLike.builder()
                    .user(user)
                    .review(review)
                    .build();
            reviewLikeRepository.save(reviewLike);
            redisTemplate.opsForValue().increment(likeCountKey);
            review.updateLikeCount(review.getLikeCount() + 1);
            log.info("리뷰 좋아요 userId={} reviewId={}", user.getId(), reviewId);
            return review.getLikeCount();
        }
    }
}
