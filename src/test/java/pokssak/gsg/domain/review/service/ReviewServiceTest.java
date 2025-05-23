package pokssak.gsg.domain.review.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import pokssak.gsg.common.exception.CustomException;
import pokssak.gsg.domain.cafe.entity.Cafe;
import pokssak.gsg.domain.cafe.service.CafeService;
import pokssak.gsg.domain.review.dto.ReviewCreateRequest;
import pokssak.gsg.domain.review.entity.Review;
import pokssak.gsg.domain.review.exception.ReviewErrorCode;
import pokssak.gsg.domain.review.repository.ReviewRepository;
import pokssak.gsg.domain.user.entity.User;
import pokssak.gsg.domain.user.service.UserService;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    ReviewRepository reviewRepository;

    @Mock
    ReviewValidationService reviewValidationService;

    @Mock
    UserService userService;

    @Mock
    CafeService cafeService;

    @InjectMocks
    ReviewService reviewService;

    User user = User.builder().id(111L).build();
    Cafe cafe = Cafe.builder().id(222L).build();

    @Nested
    @DisplayName("리뷰 전체 조회")
    class getAllReviews {

        Pageable pageable = Pageable.unpaged();
        Review review = Review.builder().user(user).cafe(cafe).build();

        @Test
        @DisplayName("모두 조회 성공")
        void getReviews_success() {
            // given
            var mockedPages = new PageImpl(List.of(review));

            // when
            when(reviewRepository.findAll(any(Pageable.class))).thenReturn(mockedPages);
            var result = reviewService.getReviews(user, pageable);

            // then
            assertThat(result.getSize()).isEqualTo(1);
            assertThat(result.getContent().get(0).reviewId()).isEqualTo(review.getId());
        }

        @Test
        @DisplayName("카페별 조회 성공")
        void getReviews_withCafeId_fail() {
            // given
            var mockedPages = new PageImpl(List.of(review));

            // when
            when(reviewRepository.findByCafeId(eq(cafe.getId()), any(Pageable.class))).thenReturn(mockedPages);
            var response = reviewService.getReviews(cafe.getId(), user, pageable);

            // then
            assertThat(response.getSize()).isEqualTo(1);
            assertThat(response.getContent().get(0).reviewId()).isEqualTo(review.getId());
        }

        // 이런경우도 해야하나?
        // Pageable의 경우를 신뢰해야하는것아닌가?
//        @Test
//        @DisplayName("카페가 존재하지 않음")
//        void getReviews_withCafeId_실패() {
//            // given
//            var pageable = Pageable.unpaged();
//            var review = Review.builder().user(user).cafe(cafe).build();
//            var page = new PageImpl(List.of());
//
//            // when
//            when(reviewRepository.findByCafeId(eq(cafe.getId()), any(Pageable.class)))
//                    .thenReturn(page);
//            var response = reviewService.getReviews(cafe.getId(), pageable);
//
//            // then
//            assertThat(response.getSize())
//                    .isEqualTo(0);
//        }
    }


    @Nested
    @DisplayName("리뷰 상세 조회")
    class getReviewById {


        @Test
        @DisplayName("상세 조회 성공")
        void getReviewById_success() {
            // given
            var reviewId = 1L;
            var mockedReview = Review.builder().id(reviewId).user(user).cafe(cafe).build();

            // when
            when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(mockedReview));
            var result = reviewService.getReviewById(user, reviewId);

            // then
            assertThat(result.reviewId())
                    .isEqualTo(reviewId);
        }

        @Test
        @DisplayName("아이디 없음")
        void getReviewById_fail() {
            // given
            var reviewId = 1L;

            // when
            when(reviewRepository.findById(reviewId)).thenReturn(Optional.empty());

            // then
            assertThatThrownBy(() -> reviewService.getReviewById(user, reviewId))
                    .isInstanceOf(CustomException.class)
                    .extracting("errorCode")
                    .isEqualTo(ReviewErrorCode.NOT_FOUND);
        }
    }


    @Nested
    @DisplayName("리뷰 생성")
    class createReview {

        ReviewCreateRequest createRequest = ReviewCreateRequest.builder()
                .cafeId(cafe.getId())
                .build();

        @Test
        @DisplayName("생성 성공")
        void createReview_success() {
            // given
            var reviewId = 1L;
            var mockedReview = Review.builder()
                    .id(reviewId)
                    .user(user)
                    .cafe(cafe)
                    .build();

            // when
            doNothing().when(reviewValidationService).validateTodayLimit(anyLong(), anyLong());
            when(userService.getUserById(user.getId())).thenReturn(user); //TODO : Facade로 변경시 제거
            when(cafeService.getCafeById(cafe.getId())).thenReturn(cafe);
            when(reviewRepository.save(any(Review.class))).thenReturn(mockedReview);
            var result = reviewService.createReview(user.getId(), createRequest);

            // then
            assertThat(result)
                    .isEqualTo(reviewId);
        }

        @Test
        @DisplayName("오늘 이미 생성함")
        void createReview_already_post_today() {
            // given

            // when
            when(userService.getUserById(user.getId())).thenReturn(user); //TODO : Facade로 변경시 제거
            when(cafeService.getCafeById(cafe.getId())).thenReturn(cafe);
            doThrow(new CustomException(ReviewErrorCode.ALREADY_POST_TODAY))
                    .when(reviewValidationService).validateTodayLimit(eq(user.getId()), eq(cafe.getId()));

            // then
            assertThatThrownBy(() -> reviewService.createReview(user.getId(), createRequest))
                    .isInstanceOf(CustomException.class)
                    .extracting("errorCode")
                    .isEqualTo(ReviewErrorCode.ALREADY_POST_TODAY);
        }
    }

    @Nested
    @DisplayName("리뷰 삭제")
    class deleleReview {

        @Test
        @DisplayName("삭제 성공")
        void deleteReview_success() {
            // given
            var reviewId = 1L;
            var mockedReview = Review.builder().id(reviewId).build();

            // when
            when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(mockedReview));

            // then
            assertThatCode(() -> reviewService.deleteReview(mockedReview.getId()))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("삭제 실패")
        void deleteReview_fail() {
            // given
            var reviewId = 1L;

            // when & then
            assertThatThrownBy(() -> reviewService.deleteReview(reviewId))
                    .isInstanceOf(CustomException.class)
                    .extracting("errorCode")
                    .isEqualTo(ReviewErrorCode.NOT_FOUND);
        }
    }
}