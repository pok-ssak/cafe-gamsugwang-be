package pokssak.gsg.domain.bookmark.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import pokssak.gsg.domain.bookmark.controller.api.BookmarkControllerApi;
import pokssak.gsg.domain.bookmark.dto.BookmarkResponse;
import pokssak.gsg.domain.bookmark.service.BookmarkService;
import pokssak.gsg.domain.user.entity.User;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/bookmarks")
public class BookmarkController implements BookmarkControllerApi {

    private final BookmarkService bookmarkService;

    @PostMapping("/{cafeId}")
    public ResponseEntity<Void> addBookmark(@PathVariable Long cafeId,
                                            @AuthenticationPrincipal User user) {
        bookmarkService.addBookmark(user.getId(), cafeId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{cafeId}")
    public ResponseEntity<Void> removeBookmark(@PathVariable Long cafeId,
                                               @AuthenticationPrincipal User user) {
        bookmarkService.removeBookmark(user.getId(), cafeId);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<BookmarkResponse>> getBookmarks(@AuthenticationPrincipal User user) {
        List<BookmarkResponse> response = bookmarkService.getUserBookmarks(user.getId());
        return ResponseEntity.ok(response);
    }
}
