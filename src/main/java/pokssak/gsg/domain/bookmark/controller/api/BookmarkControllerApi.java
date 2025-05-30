package pokssak.gsg.domain.bookmark.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import pokssak.gsg.domain.bookmark.dto.BookmarkResponse;
import pokssak.gsg.domain.user.entity.User;

import java.util.List;

@Tag(name = "Bookmark", description = "북마크 API")
@RequestMapping("/api/v1/bookmarks")
public interface BookmarkControllerApi {

    @Operation(summary = "북마크 추가", description = "사용자가 특정 카페를 북마크에 추가합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "북마크 추가 성공"),
            @ApiResponse(responseCode = "400", description = "이미 북마크한 경우"),
            @ApiResponse(responseCode = "404", description = "사용자 또는 카페가 존재하지 않음")
    })
    @PostMapping("/{cafeId}")
    ResponseEntity<Void> addBookmark(
            @Parameter(description = "북마크할 카페의 ID", example = "1") @PathVariable Long cafeId,
            @Parameter(hidden = true) @AuthenticationPrincipal User user
    );

    @Operation(summary = "북마크 제거", description = "사용자가 특정 카페를 북마크에서 제거합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "북마크 제거 성공"),
            @ApiResponse(responseCode = "404", description = "사용자 또는 카페가 존재하지 않음")
    })
    @DeleteMapping("/{cafeId}")
    ResponseEntity<Void> removeBookmark(
            @Parameter(description = "제거할 카페의 ID", example = "1") @PathVariable Long cafeId,
            @Parameter(hidden = true) @AuthenticationPrincipal User user
    );

    @Operation(summary = "내 북마크 목록 조회", description = "사용자의 모든 북마크된 카페 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "북마크 목록 조회 성공")
    })
    @GetMapping
    ResponseEntity<List<BookmarkResponse>> getBookmarks(
            @Parameter(hidden = true) @AuthenticationPrincipal User user
    );
}
