package pokssak.gsg.domain.admin.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import pokssak.gsg.common.dto.ApiResponse;
import pokssak.gsg.domain.admin.entity.Admin;
import pokssak.gsg.domain.admin.service.AdminService;
import pokssak.gsg.domain.cafe.entity.Suggestion;
import pokssak.gsg.domain.feed.dto.PageRequestDto;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;

    @PutMapping("/suggestions/{suggestionId}/accept")
    public ResponseEntity<?> acceptSuggestion(@PathVariable Long suggestionId,
                                              @AuthenticationPrincipal Admin admin) {
        adminService.acceptSuggestion(suggestionId, admin.getId());
        return ResponseEntity.ok(ApiResponse.ok("수정 제안을 수락했습니다."));
    }

    @GetMapping("/suggestions")
    public ResponseEntity<?> getAllSuggestions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        PageRequestDto pageRequestDto = new PageRequestDto(page, size);
        Pageable pageable = pageRequestDto.toPageable();
        Page<Suggestion> suggestionPage = adminService.getAllSuggestions(pageable);

        return ResponseEntity.ok(ApiResponse.ok(suggestionPage));
    }

}
