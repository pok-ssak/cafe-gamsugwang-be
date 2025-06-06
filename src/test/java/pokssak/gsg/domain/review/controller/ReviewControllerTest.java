package pokssak.gsg.domain.review.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import pokssak.gsg.domain.cafe.entity.Cafe;
import pokssak.gsg.domain.review.dto.ReviewCreateRequest;
import pokssak.gsg.domain.review.dto.ReviewResponse;
import pokssak.gsg.domain.review.service.ReviewService;
import pokssak.gsg.domain.user.entity.User;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReviewControllerTest {

    @Mock
    ReviewService reviewService;

    @InjectMocks
    ReviewController reviewController;

    Cafe cafe = Cafe.builder().id(111L).build();
    User user = User.builder().id(222L).build();

    @Nested
    @DisplayName("모든 리뷰 조회")
    class getReviews {

        @Test
        @DisplayName("조회 성공")
        void getReviews_success() {
            // given
            var mocked = new PageImpl<ReviewResponse>(List.of());

            // when
            when(reviewService.getReviews(any(User.class), any(Pageable.class))).thenReturn(mocked);
            var response = reviewController.getReviews(user, Pageable.unpaged());

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody().data()).isEqualTo(mocked);
        }
    }

    @Nested
    @DisplayName("리뷰 상세조회")
    class getReview {

        @Test
        @DisplayName("상세조회 성공")
        void getReview_success() {
            // given
            var mocked = ReviewResponse.builder().build();

            // when
            when(reviewService.getReviewById(any(User.class), eq(cafe.getId()))).thenReturn(mocked);
            var response = reviewController.getReview(user, cafe.getId());

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody().data()).isEqualTo(mocked);
        }
    }

    @Nested
    @DisplayName("리뷰 생성")
    class crateReview {
        @Test
        @DisplayName("생성 성공")
        void crateReview() {
            // given
            var createRequest = ReviewCreateRequest.builder().build();
            var mocked = 111L;

            // when
            when(reviewService.createReview(eq(user.getId()), eq(createRequest))).thenReturn(mocked);
            var response = reviewController.createReview(user, createRequest);

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            assertThat(response.getHeaders().get("Location").get(0))
                    .isEqualTo("/api/v1/reviews/" + mocked);
        }
    }

    @Nested
    @DisplayName("리뷰 삭제")
    class deleteReview {
        @Test
        @DisplayName("삭제 성공")
        void deleteReview_success() {
            // given

            // when
            var response = reviewController.deleteReview(cafe.getId());

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody().data()).isNull();
        }
    }
}