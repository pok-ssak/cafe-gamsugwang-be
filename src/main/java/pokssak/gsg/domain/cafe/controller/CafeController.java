package pokssak.gsg.domain.cafe.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import pokssak.gsg.common.dto.ApiResponse;
import pokssak.gsg.domain.cafe.dto.RecommendResponse;
import pokssak.gsg.domain.cafe.dto.GetCafeResponse;
import pokssak.gsg.domain.cafe.service.CafeService;
import pokssak.gsg.domain.review.service.ReviewService;
import pokssak.gsg.domain.user.entity.User;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/cafes")
public class CafeController {
    private final CafeService cafeService;
    private final ReviewService reviewService;


    /**
     * 카페 상세 조회
     * @param cafeId
     * @return
     */
    @GetMapping("/{cafeId}")
    public ResponseEntity<?> getCafe(@PathVariable Long cafeId) {
        GetCafeResponse cafe = cafeService.getCafe(cafeId);
        return ResponseEntity.ok(ApiResponse.ok(cafe));
    }

    @GetMapping("/auto-complete")
    public ResponseEntity<?> autoComplete(@RequestParam String keyword, @RequestParam(required = false, defaultValue = "10") int limit) {
        List<String> titles = cafeService.autoComplete(keyword, limit);
        ApiResponse.ok(titles);

        return ResponseEntity.ok(titles);
    }

    @GetMapping("/recommend")
    public ResponseEntity<?> recommend(
            @RequestParam String option,
            @RequestParam String keyword,
            @RequestParam(required = false) Double lat,
            @RequestParam(required = false) Double lon,
            @RequestParam(defaultValue = "10") int limit){

        log.info("keyword: {}, lat: {}, lon: {}, limit: {}", keyword, lat, lon, limit);
        List<RecommendResponse> results;

        switch (option) {
            case "location":
                results = cafeService.recommendByLocation(lat, lon, limit);
                break;
            case "keyword":
                results = cafeService.recommendByKeyword(keyword, limit);
                break;
            case "hybrid":
                results = cafeService.recommendByKeyword(keyword, limit);
                break;
            default:
                return ResponseEntity.badRequest().body("invalid option");
        }
        return ResponseEntity.ok(ApiResponse.ok(results));
    }

    @GetMapping("/{cafeId}/reviews")
    public ResponseEntity<?> getCafeReviews(
            @AuthenticationPrincipal User user, // 리뷰별 좋아요 확인을 위함.
            @PathVariable Long cafeId,
            Pageable pageable
    ) {
        var result = reviewService.getReviews(cafeId, user, pageable);
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    @GetMapping("")
    public ResponseEntity<?> getCafes(
            Pageable pageable
    ) {
        var result = cafeService.getCafes(pageable);
        return ResponseEntity.ok(ApiResponse.ok(result));
    }
}
