package pokssak.gsg.domain.review.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import pokssak.gsg.common.entity.BaseEntity;
import pokssak.gsg.common.exception.CustomException;
import pokssak.gsg.domain.cafe.entity.Cafe;
import pokssak.gsg.domain.review.entity.Review;
import pokssak.gsg.domain.review.exception.ReviewErrorCode;
import pokssak.gsg.domain.review.repository.ReviewRepository;
import pokssak.gsg.domain.user.entity.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewValidationServiceTest {

    @Mock
    ReviewRepository reviewRepository;

    @InjectMocks
    ReviewValidationService reviewValidationService;


    @Nested
    @DisplayName("일일 경과 검증")
    class validateTodayLimit {


        User user = User.builder().id(111L).build();
        Cafe cafe = Cafe.builder().id(222L).build();

        @Test
        @DisplayName("리뷰가 없을 때 검증 성공")
        void validateTodayLimit_no_review_success() {
            // given

            // when & then
            when(reviewRepository.findTopByUserIdAndCafeIdOrderByModifiedAtDesc(eq(user.getId()), eq(cafe.getId())))
                    .thenReturn(Optional.empty());

            assertThatCode(() -> reviewValidationService.validateTodayLimit(user.getId(), cafe.getId()))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("리뷰가 있을 때 검증 성공")
        void validateTodayLimit_success() {
            // given
            var review = spy(Review.builder()
                    .cafe(cafe)
                    .build());
            doReturn(LocalDateTime.now().minusDays(2)).when(review).getModifiedAt();

            var reviewedUser = User.builder()
                            .id(333L)
                            .reviews(List.of(review))
                            .build();
            // when
            when(reviewRepository.findTopByUserIdAndCafeIdOrderByModifiedAtDesc(eq(reviewedUser.getId()), eq(cafe.getId())))
                    .thenReturn(Optional.of(review));

            // then
            assertThatCode(() -> reviewValidationService.validateTodayLimit(reviewedUser.getId(), cafe.getId()))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("일일 경과 검증 실패")
        void validateTodayLimit_fail() {
            // given
            var review = spy(Review.builder()
                    .cafe(cafe)
                    .build());
            doReturn(LocalDateTime.now().minusDays(0)).when(review).getModifiedAt();

            var reviewedUser = User.builder()
                    .id(333L)
                    .reviews(List.of(review))
                    .build();
            // when
            when(reviewRepository.findTopByUserIdAndCafeIdOrderByModifiedAtDesc(eq(reviewedUser.getId()), eq(cafe.getId())))
                    .thenReturn(Optional.of(review));

            // then
            assertThatThrownBy(() -> reviewValidationService.validateTodayLimit(reviewedUser.getId(), cafe.getId()))
                    .isInstanceOf(CustomException.class)
                    .extracting("errorCode")
                    .isEqualTo(ReviewErrorCode.ALREADY_POST_TODAY);
        }
    }
}