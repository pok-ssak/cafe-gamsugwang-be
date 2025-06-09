package pokssak.gsg.domain.review.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pokssak.gsg.common.exception.CustomException;
import pokssak.gsg.domain.feed.dto.FeedRequest;
import pokssak.gsg.domain.feed.entity.FeedType;
import pokssak.gsg.domain.feed.service.FeedService;
import pokssak.gsg.domain.review.entity.Review;
import pokssak.gsg.domain.review.entity.ReviewLike;
import pokssak.gsg.domain.review.exception.ReviewErrorCode;
import pokssak.gsg.domain.review.repository.ReviewLikeRepository;
import pokssak.gsg.domain.review.repository.ReviewRepository;
import pokssak.gsg.domain.user.exception.UserErrorCode;
import pokssak.gsg.domain.user.repository.UserRepository;

@Slf4j
@RequiredArgsConstructor
@Service
@EnableAsync
public class ReviewLikeService {

    private final ReviewLikeRepository reviewLikeRepository;
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final FeedService feedService;

    private static final String LIKE_COUNT_KEY_PREFIX = "review:like:count:";

    /** 리뷰 좋아요 토글 */
    @Transactional
    public Long toggle(Long userId, Long reviewId) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(UserErrorCode.NOT_FOUND));
        var review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new CustomException(ReviewErrorCode.NOT_FOUND));

        var existingLike = reviewLikeRepository.findByUserIdAndReviewId(user.getId(), reviewId);
//        String likeCountKey = LIKE_COUNT_KEY_PREFIX + reviewId;

        if (existingLike.isPresent()) {
            // 좋아요 취소
            reviewLikeRepository.delete(existingLike.get());
//            redisTemplate.opsForValue().decrement(likeCountKey);
//            review.updateLikeCount(Math.max(0, review.getLikeCount() - 1));
            asyncUpdate(review, -1L);
            log.info("리뷰 좋아요 취소 userId={} reviewId={}", user.getId(), reviewId);
            return review.getLikeCount();
        } else {
            // 좋아요 추가
            var reviewLike = ReviewLike.builder()
                    .user(user)
                    .review(review)
                    .build();
            reviewLikeRepository.save(reviewLike);
//            redisTemplate.opsForValue().increment(likeCountKey);
//            review.updateLikeCount(review.getLikeCount() + 1);
            handleLikeOnReview(reviewId);
            asyncUpdate(review, 1L);
            log.info("리뷰 좋아요 userId={} reviewId={}", user.getId(), reviewId);
            return review.getLikeCount();
        }
    }


    @Async
    @Transactional
    public void asyncUpdate(Review review, Long delta){
        review.updateLikeCount(Math.max(0, review.getLikeCount() + delta)); // -1인 경우
    }

    public void handleLikeOnReview(Long reviewId) {
        var review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new CustomException(ReviewErrorCode.NOT_FOUND));

        Long reviewAuthorId = review.getUser().getId();

        FeedRequest feedRequest = new FeedRequest(
                reviewAuthorId,
                "회원님이 남긴 리뷰에 좋아요가 눌렸습니다.",
                "/api/v1/reviews/" + reviewId,
                FeedType.LIKE
        );

        feedService.createFeed(feedRequest);
    }


}
