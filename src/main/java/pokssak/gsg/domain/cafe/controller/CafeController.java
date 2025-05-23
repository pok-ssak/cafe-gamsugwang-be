package pokssak.gsg.domain.cafe.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pokssak.gsg.common.dto.ApiResponse;
import pokssak.gsg.domain.cafe.dto.RecommendResponse;
import pokssak.gsg.domain.cafe.service.CafeService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/cafes")
public class CafeController {
    private final CafeService cafeService;

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
}
