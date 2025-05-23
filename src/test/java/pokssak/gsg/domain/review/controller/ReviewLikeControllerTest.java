package pokssak.gsg.domain.review.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithMockUser;
import pokssak.gsg.domain.review.service.ReviewLikeService;
import pokssak.gsg.domain.user.entity.User;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)

class ReviewLikeControllerTest {

    @InjectMocks
    ReviewLikeController reviewLikeController;

    @Mock
    ReviewLikeService reviewLikeService;

    @Test
    @DisplayName("리뷰 좋아요 토글 - 성공")
    void toggleLike_AuthenticatedUser_success() {
        // given
        Long reviewId = 1L;
        User user = User.builder().id(1L).build();

        // when
        var response = reviewLikeController.toggleLike(user, reviewId);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().success()).isTrue();
        verify(reviewLikeService).toggle(user.getId(), reviewId);
    }
}