package pokssak.gsg.domain.review.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import pokssak.gsg.domain.user.entity.User;

@Tag(name = "리뷰 좋아요", description = "리뷰 좋아요 관련 API")
public interface ReviewLikeControllerApi {

    @PostMapping("/{reviewId}/like-toggle")
    @Operation(summary = "리뷰 좋아요 토글", description = "리뷰 좋아요를 토글 조회합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "좋아요")
    })
    ResponseEntity<pokssak.gsg.common.dto.ApiResponse<?>> toggleLike(
            @AuthenticationPrincipal User user,
            @PathVariable("reviewId") Long reviewId
    );
}
