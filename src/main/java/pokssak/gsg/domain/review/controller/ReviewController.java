package pokssak.gsg.domain.review.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import pokssak.gsg.common.dto.ApiResponse;
import pokssak.gsg.domain.review.controller.api.ReviewControllerApi;
import pokssak.gsg.domain.review.dto.ReviewCreateRequest;
import pokssak.gsg.domain.review.dto.ReviewResponse;
import pokssak.gsg.domain.review.service.ReviewLikeService;
import pokssak.gsg.domain.review.service.ReviewService;
import pokssak.gsg.domain.user.entity.User;
import pokssak.gsg.domain.user.service.UserService;

import java.net.URI;
import java.nio.file.Path;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/reviews")
@RestController
public class ReviewController implements ReviewControllerApi {

    private final ReviewService reviewService;
    private final ReviewLikeService reviewLikeService;
    private final UserService userService;

    /** 모든 리뷰 조회 */
    @GetMapping("")
    public ResponseEntity<ApiResponse<Page<ReviewResponse>>> getReviews(
            @AuthenticationPrincipal User user,
            Pageable pageable
    ){
        var page = reviewService.getReviews(user, pageable);
        return ResponseEntity.ok(ApiResponse.ok(page));
    }

    /** 리뷰 상세 조회 */
    @GetMapping("/{reviewId}")
    public ResponseEntity<ApiResponse<ReviewResponse>> getReview(
            @AuthenticationPrincipal User user,
            @PathVariable("reviewId") Long reviewId
    ){
        var response = reviewService.getReviewById(user, reviewId);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    /** 리뷰 추가 */
    @PostMapping("")
    public ResponseEntity<ApiResponse<Void>> createReview(
            @AuthenticationPrincipal User user,
            @RequestBody ReviewCreateRequest reviewCreateRequest
    ){
        var reviewId = reviewService.createReview(user.getId(), reviewCreateRequest);
        var uri = URI.create("/api/v1/reviews/" + reviewId);
        return ResponseEntity.created(uri)
                .body(ApiResponse.ok(null));
    }

    /** 리뷰 삭제 */
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<ApiResponse<Void>> deleteReview(
            @PathVariable("reviewId") Long reviewId
    ){
        reviewService.deleteReview(reviewId);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }
}
