package pokssak.gsg.domain.cafe.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pokssak.gsg.common.dto.ApiResponse;
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

        ApiResponse<Object> response = ApiResponse.builder()
                .success(true)
                .data(titles)
                .build();

        return ResponseEntity.ok(response);
    }
}
