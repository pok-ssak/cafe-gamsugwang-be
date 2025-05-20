package pokssak.gsg.domain.review.controller.api;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import pokssak.gsg.domain.review.dto.ReviewCreateRequest;

public interface ReviewControllerApi {

    /**
     * 모든 리뷰 조회
     */
    @GetMapping("")
    ResponseEntity<Page<?>> getReviews(Pageable pageable);

    /**
     * 리뷰 상세 조회
     */
    @GetMapping("/{reviewId}")
    ResponseEntity<?> getReview(@PathVariable("reviewId") Long reviewId);

    /**
     * 리뷰 추가
     */
    @PostMapping("")
    ResponseEntity<?> getReview(
            @AuthenticationPrincipal Long userId,
            @RequestBody ReviewCreateRequest reviewCreateRequest
    );

    /**
     * 리뷰 삭제
     */
    @DeleteMapping("/{reviewId}")
    ResponseEntity<?> deleteReviews(@PathVariable("reviewId") Long reviewId);
}