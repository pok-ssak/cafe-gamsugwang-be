package pokssak.gsg.domain.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import pokssak.gsg.domain.user.controller.api.UserKeywordControllerApi;
import pokssak.gsg.domain.user.dto.KeywordRequest;
import pokssak.gsg.domain.user.dto.UserKeywordResponse;
import pokssak.gsg.domain.user.entity.User;
import pokssak.gsg.domain.user.service.UserKeywordService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users/{userId}/keywords")
public class UserKeywordController implements UserKeywordControllerApi {

    private final UserKeywordService userKeywordService;

    // 사용자 키워드 조회
    @GetMapping
    public ResponseEntity<List<UserKeywordResponse>> getUserKeywords(@AuthenticationPrincipal User user) {
        List<UserKeywordResponse> responses = userKeywordService.getUserKeywords(user.getId());
        return ResponseEntity.ok(responses);
    }

    // 사용자 키워드 조회
    @GetMapping("/recommend")
    public ResponseEntity<List<UserKeywordResponse>> getRecommendKeywords(
            @AuthenticationPrincipal User user,
            @RequestParam String keyword
    ) {
        List<UserKeywordResponse> responses = userKeywordService.getRecommendKeywords(user.getId(), keyword);
        return ResponseEntity.ok(responses);
    }
//    // 사용자 키워드 수정
//    @PutMapping
//    public ResponseEntity<Void> updateUserKeywords(
//            @AuthenticationPrincipal User user,
//            @RequestBody KeywordRequest request
//    ) {
//        userKeywordService.updateUserKeywords(user.getId(), request.keywords());
//        return ResponseEntity.noContent().build();
//    }


}
