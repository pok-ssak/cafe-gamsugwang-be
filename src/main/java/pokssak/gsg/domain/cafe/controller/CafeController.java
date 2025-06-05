package pokssak.gsg.domain.cafe.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import pokssak.gsg.common.dto.ApiResponse;
import pokssak.gsg.domain.cafe.dto.*;
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
    private final EmbeddingModel embeddingModel;


    /**
     * 카페 상세 조회
     * @param cafeId
     * @return
     */
    @GetMapping("/{cafeId}")
    public ResponseEntity<?> getCafe(
            @AuthenticationPrincipal User user, // 리뷰별 좋아요 확인을 위함.
            @PathVariable Long cafeId) {

        Long userId = getUserId(user);
        GetCafeResponse cafe = cafeService.getCafe(userId, cafeId);
        return ResponseEntity.ok(ApiResponse.ok(cafe));
    }

    // 카페 수정제안
    @PutMapping("/{cafeId}/suggest")
    public ResponseEntity<?> suggestCafe(
            @AuthenticationPrincipal User user,
            @PathVariable Long cafeId,
            @RequestBody SuggestRequest request) {
        log.info("수정 제안 요청 userId: {}, cafeId: {}", user.getId(), cafeId);
        cafeService.suggestCafe(user.getId(), cafeId, request);
        return ResponseEntity.ok(ApiResponse.ok("수정 제안 완료"));
    }

    @GetMapping("/auto-complete")
    public ResponseEntity<List<AutoCompleteResponse>> autoComplete(@RequestParam String keyword, @RequestParam(required = false, defaultValue = "10") int limit) {
        List<AutoCompleteResponse> response = cafeService.autoComplete(keyword, limit);
        ApiResponse.ok(response);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchCafes(@RequestParam String query, @RequestParam(required = false, defaultValue = "50") int limit) {
        log.info("search query: {}, limit: {}", query, limit);
        List<SearchCafeResponse> searchCafeResponses = cafeService.searchCafes(query, limit);
        return ResponseEntity.ok(ApiResponse.ok(searchCafeResponses));

    }

    @GetMapping("/recommend")
    public ResponseEntity<?> recommend(
            @AuthenticationPrincipal User user,
            @RequestParam String option,
            @RequestParam String keyword,
            @RequestParam(required = false) Double lat,
            @RequestParam(required = false) Double lon,
            @RequestParam(defaultValue = "10") int limit){

        log.info("keyword: {}, lat: {}, lon: {}, limit: {}", keyword, lat, lon, limit);
        List<RecommendResponse> results;

        Long userId = getUserId(user);

        switch (option) {
            case "location":
                results = cafeService.recommendByLocation(userId, lat, lon, limit);
            case "keyword":
                results = cafeService.recommendByKeyword(userId, keyword, limit);
                break;
            case "hybrid":
                results = cafeService.recommendByKeyword(userId, keyword, limit); // 가중치 기능구현
                break;
            default:
                return ResponseEntity.badRequest().body("invalid option");
        }
        return ResponseEntity.ok(ApiResponse.ok(results));
    }

    @GetMapping("/self-recommend")
    public ResponseEntity<?> customRecommend(
            @AuthenticationPrincipal User user,
            @RequestParam(required = false) Double lat,
            @RequestParam(required = false) Double lon,
            @RequestParam(defaultValue = "10") int limit) {

        log.info("lat: {}, lon: {}, limit: {}",lat, lon, limit);
        log.info("User: {}", user);
        // 임베딩을 위해 String 으로 변환
//        String keywords = user.getUserKeywords().stream()
//                .map(u -> u.getKeyword().word())
//                .collect(Collectors.joining(" "));
//        log.info("user keywords: {}", userKeywordService);

        List<RecommendResponse> recommendResponses = cafeService.recommendByUserInfo(user, lat, lon, limit);
        return ResponseEntity.ok(ApiResponse.ok(recommendResponses));
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

    @GetMapping
    public ResponseEntity<?> getCafes(
            Pageable pageable
    ) {
        var result = cafeService.getCafes(pageable);
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

//    @GetMapping("/embedding")
//    public ResponseEntity<?> getEmbeddingVector(@RequestParam String keyword) {
//        log.info("keyword: {}", keyword);
//        float[] embed = embeddingModel.embed(keyword);
//        return ResponseEntity.ok(ApiResponse.ok(embed));
//    }
//
    private Long getUserId(User user) {
        return user != null ? user.getId() : null;
    }
}
