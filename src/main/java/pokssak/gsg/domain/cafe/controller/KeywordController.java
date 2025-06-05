package pokssak.gsg.domain.cafe.controller;

import jakarta.annotation.security.PermitAll;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pokssak.gsg.domain.cafe.service.KeywordService;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/keywords")
@RestController
public class KeywordController {

    private final KeywordService keywordService;

    @PostMapping("/run")
    public ResponseEntity<?> keywordEmbedding() {
        log.info("test");
        keywordService.syncDataToES();
        return ResponseEntity.ok(null);
    }

    @GetMapping()
    public ResponseEntity<?> getSimilarKeywords(
            @RequestParam("query") String query,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size
    ) {
        log.info("test");
        return ResponseEntity.ok(keywordService.getSimilarKeywords(query, size));
    }

}
