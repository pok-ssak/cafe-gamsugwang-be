package pokssak.gsg.domain.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pokssak.gsg.domain.user.dto.KeywordRequest;
import pokssak.gsg.domain.user.dto.UserKeywordResponse;
import pokssak.gsg.domain.user.service.UserKeywordService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users/{userId}/keywords")
public class UserKeywordController {

    private final UserKeywordService userKeywordService;

    // 사용자 키워드 조회
    @GetMapping
    public ResponseEntity<List<UserKeywordResponse>> getUserKeywords(@PathVariable Long userId) {
        List<UserKeywordResponse> responses = userKeywordService.getUserKeywords(userId);
        return ResponseEntity.ok(responses);
    }

    // 사용자 키워드 수정
    @PutMapping
    public ResponseEntity<Void> updateUserKeywords(
            @PathVariable Long userId,
            @RequestBody KeywordRequest request
    ) {
        userKeywordService.updateUserKeywords(userId, request.keywords());
        return ResponseEntity.noContent().build();
    }
}
