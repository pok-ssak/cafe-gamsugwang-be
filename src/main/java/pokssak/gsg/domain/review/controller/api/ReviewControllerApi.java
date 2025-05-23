package pokssak.gsg.domain.review.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import pokssak.gsg.domain.review.dto.ReviewCreateRequest;
import pokssak.gsg.domain.review.dto.ReviewResponse;
import pokssak.gsg.domain.user.entity.User;

@Tag(name = "리뷰 관리", description = "리뷰 CRUD API")
public interface ReviewControllerApi {

    @GetMapping("")
    @Operation(summary = "리뷰 전체 조회", description = "모든 리뷰를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공")
    })
    ResponseEntity<pokssak.gsg.common.dto.ApiResponse<Page<ReviewResponse>>> getReviews(
            @AuthenticationPrincipal User user,
            Pageable pageable
    );

    @GetMapping("/{reviewId}")
    @Operation(summary = "리뷰 상세조회 조회", description = "모든 리뷰를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공")
    })
    ResponseEntity<pokssak.gsg.common.dto.ApiResponse<ReviewResponse>> getReview(
            @AuthenticationPrincipal User user,
            @PathVariable("reviewId") Long reviewId
    );

    @PostMapping("")
    @Operation(summary = "리뷰 생성", description = "리뷰를 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "생성 성공")
    })
    ResponseEntity<?> createReview(
            @AuthenticationPrincipal Long userId,
            @RequestBody ReviewCreateRequest reviewCreateRequest
    );

    @DeleteMapping("/{reviewId}")
    @Operation(summary = "리뷰 삭제", description = "리뷰를 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "삭제 성공")
    })
    ResponseEntity<?> deleteReview(@PathVariable("reviewId") Long reviewId);
}