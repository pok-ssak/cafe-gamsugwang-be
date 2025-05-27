package pokssak.gsg.domain.review.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import pokssak.gsg.common.dto.ApiResponse;
import pokssak.gsg.domain.review.controller.api.ReviewLikeControllerApi;
import pokssak.gsg.domain.review.service.ReviewLikeService;
import pokssak.gsg.domain.user.entity.User;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/reviews")
@RestController
public class ReviewLikeController implements ReviewLikeControllerApi {

    private final ReviewLikeService reviewLikeService;

    /** 리뷰 좋아요 */
    @PostMapping("/{reviewId}/like-toggle")
    public ResponseEntity<ApiResponse<?>> toggleLike(
            @AuthenticationPrincipal User user,
            @PathVariable("reviewId") Long reviewId
    ) {
        var result = reviewLikeService.toggle(user.getId(), reviewId);
        return ResponseEntity.ok(ApiResponse.ok(result));
    }
}
