package pokssak.gsg.domain.review.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import pokssak.gsg.common.exception.CustomException;
import pokssak.gsg.domain.review.entity.Review;
import pokssak.gsg.domain.review.entity.ReviewLike;
import pokssak.gsg.domain.review.exception.ReviewErrorCode;
import pokssak.gsg.domain.review.repository.ReviewLikeRepository;
import pokssak.gsg.domain.review.repository.ReviewRepository;
import pokssak.gsg.domain.user.entity.User;
import pokssak.gsg.domain.user.exception.UserErrorCode;
import pokssak.gsg.domain.user.repository.UserRepository;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReviewLikeServiceTest {

    @InjectMocks
    ReviewLikeService reviewLikeService;

    @Mock
    UserRepository userRepository;
    @Mock
    ReviewRepository reviewRepository;
    @Mock
    ReviewLikeRepository reviewLikeRepository;
    @Mock
    RedisTemplate<String, Object> redisTemplate;
    @Mock
    ValueOperations<String, Object> valueOperations;

    @BeforeEach
    void setUp() {
//        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Nested
    @DisplayName("좋아요 토글")
    class toggle {

        @Test
        @DisplayName("좋아요 성공")
        void toggle_liked_success() {
            // given
            Long userId = 1L;
            Long reviewId = 1L;
            User user = User.builder().id(userId).build();
            Review review = Review.builder().id(reviewId).likeCount(0L).build();

            when(redisTemplate.opsForValue()).thenReturn(valueOperations);
            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));
            when(reviewLikeRepository.findByUserIdAndReviewId(userId, reviewId))
                    .thenReturn(Optional.empty());

            // when
            boolean result = reviewLikeService.toggle(userId, reviewId);

            // then
            assertThat(result).isTrue();
            verify(reviewLikeRepository).save(any(ReviewLike.class));
            verify(valueOperations).increment(eq("review:like:count:" + reviewId));
            assertThat(review.getLikeCount()).isEqualTo(1);
        }

        @Test
        @DisplayName("취소 성공")
        void toggle_cancel_success() {
            // given
            Long userId = 1L;
            Long reviewId = 1L;
            User user = User.builder().id(userId).build();
            Review review = Review.builder().id(reviewId).likeCount(1L).build();
            ReviewLike existingLike = ReviewLike.builder()
                    .user(user)
                    .review(review)
                    .build();

            when(redisTemplate.opsForValue()).thenReturn(valueOperations);
            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));
            when(reviewLikeRepository.findByUserIdAndReviewId(userId, reviewId))
                    .thenReturn(Optional.of(existingLike));

            // when
            boolean result = reviewLikeService.toggle(userId, reviewId);

            // then
            assertThat(result).isFalse();
            verify(reviewLikeRepository).delete(existingLike);
            verify(valueOperations).decrement(eq("review:like:count:" + reviewId));
            assertThat(review.getLikeCount()).isEqualTo(0);
        }

        @Test
        @DisplayName("사용자 조회 예외")
        void toggle_user_not_found_fail() {
            // given
            Long userId = 1L;
            Long reviewId = 1L;

            when(userRepository.findById(userId)).thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> reviewLikeService.toggle(userId, reviewId))
                    .isInstanceOf(CustomException.class)
                    .hasFieldOrPropertyWithValue("errorCode", UserErrorCode.NOT_FOUND);
        }

        @Test
        @DisplayName("리뷰 조회 예외")
        void toggle_review_not_found_fail() {
            // given
            Long userId = 1L;
            Long reviewId = 1L;
            User user = User.builder().id(userId).build();

            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            when(reviewRepository.findById(reviewId)).thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> reviewLikeService.toggle(userId, reviewId))
                    .isInstanceOf(CustomException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ReviewErrorCode.NOT_FOUND);
        }
    }
}