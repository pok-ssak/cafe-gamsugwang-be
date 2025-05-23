package pokssak.gsg.domain.review.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import lombok.RequiredArgsConstructor;
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
    private static final String USER_LIKED_SET_PREFIX = "user:liked:review:";

    /** 리뷰 토글, Redis 카운터 반영 + 좋아요 여부 Redis Set으로 관리 */
    public boolean toggle(Long userId, Long reviewId) {
        String userLikedSetKey = USER_LIKED_SET_PREFIX + userId;
        SetOperations<String, Object> setOps = redisTemplate.opsForSet();
//        String reviewIdStr = reviewId.toString();

//        boolean likedBefore = setOps.isMember(userLikedSetKey, reviewIdStr);
        boolean likedBefore = setOps.isMember(userLikedSetKey, reviewId);
        String likeCountKey = LIKE_COUNT_KEY_PREFIX + reviewId;

        if (likedBefore) {
            // 좋아요 취소
//            setOps.remove(userLikedSetKey, reviewIdStr);
            setOps.remove(userLikedSetKey, reviewId);
            redisTemplate.opsForValue().decrement(likeCountKey);
            deleteReviewLike(userId, reviewId);
            log.info("리뷰 좋아요 취소 userId={} reviewId={}", userId, reviewId);
            return false;
        } else {
            // 좋아요 추가
//            setOps.add(userLikedSetKey, reviewIdStr);
            setOps.add(userLikedSetKey, reviewId);
            redisTemplate.opsForValue().increment(likeCountKey);
            createReviewLike(userId, reviewId);
            log.info("리뷰 좋아요 userId={} reviewId={}", userId, reviewId);
            return true;
        }
    }
    @Transactional
    private void createReviewLike(Long userId, Long reviewId) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(UserErrorCode.NOT_FOUND));
        var review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new CustomException(ReviewErrorCode.NOT_FOUND));
        var reviewLike = ReviewLike.builder()
                .user(user)
                .review(review)
                .build();

        reviewLikeRepository.save(reviewLike);
        review.updateLikeCount(review.getLikeCount() + 1);
    }

    private void deleteReviewLike(Long userId, Long reviewId) {
        var reviewLikeOpt = reviewLikeRepository.findByUserIdAndReviewId(userId, reviewId);
        if (reviewLikeOpt.isPresent()) {
            var reviewLike = reviewLikeOpt.get();
            reviewLikeRepository.delete(reviewLike);

            var review = reviewLike.getReview();
            review.updateLikeCount(Math.max(0, review.getLikeCount() - 1));
        }
    }
}
