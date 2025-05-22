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
import pokssak.gsg.domain.review.service.ReviewService;
import pokssak.gsg.domain.user.entity.User;
import pokssak.gsg.domain.user.service.UserService;

import java.net.URI;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/reviews")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")

@RestController
public class ReviewController implements ReviewControllerApi {

    private final ReviewService reviewService;
    private final UserService userService;

    /** 모든 리뷰 조회 */
    @GetMapping("")
    public ResponseEntity<ApiResponse<Page<ReviewResponse>>> getReviews(Pageable pageable){
        var page = reviewService.getReviews(pageable);
        return ResponseEntity.ok(ApiResponse.ok(page));
    }

    /** 리뷰 상세 조회 */
    @GetMapping("/{reviewId}")
    public ResponseEntity<ApiResponse<ReviewResponse>> getReview(@PathVariable("reviewId") Long reviewId){
        var response = reviewService.getReviewById(reviewId);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    /** 리뷰 추가 */
    @PostMapping("")
    public ResponseEntity<ApiResponse<Void>> createReview(
            @AuthenticationPrincipal Long userId,
            @RequestBody ReviewCreateRequest reviewCreateRequest
    ){
        userId = 1L; //TODO : 테스트용
        var reviewId = reviewService.createReview(userId, reviewCreateRequest);
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
