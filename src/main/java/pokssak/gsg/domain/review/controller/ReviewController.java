package pokssak.gsg.domain.review.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import pokssak.gsg.domain.review.dto.ReviewCreateRequest;
import pokssak.gsg.domain.review.service.ReviewService;
import pokssak.gsg.domain.user.entity.User;
import pokssak.gsg.domain.user.service.UserService;

import java.net.URI;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/reviews")
@RestController
public class ReviewController {

    private final ReviewService reviewService;
    private final UserService userService;

    /** 모든 리뷰 조회 */
    @GetMapping("")
    public ResponseEntity<Page<?>> getReviews(Pageable pageable){
        var response = reviewService.getAllReviews(pageable);
        return ResponseEntity.ok(response);
    }

    /** 리뷰 상세 조회 */
    @GetMapping("/{reviewId}")
    public ResponseEntity<?> getReview(@PathVariable("reviewId") Long reviewId){
        var response = reviewService.getReviewById(reviewId);
        return ResponseEntity.ok(response);
    }

    /** 리뷰 추가 */
    @PostMapping("")
    public ResponseEntity<?> getReview(
            @AuthenticationPrincipal Long userId,
            @RequestBody ReviewCreateRequest reviewCreateRequest
    ){
        var reviewId = reviewService.createReview(userId, reviewCreateRequest);
        var uri = URI.create("/api/v1/reviews/" + reviewId);
        return ResponseEntity.created(uri).build();
    }

    /** 리뷰 삭제 */
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<?> deleteReviews(
            @PathVariable("reviewId") Long reviewId
    ){
        reviewService.deleteReview(reviewId);
        return ResponseEntity.ok().build();
    }
}
