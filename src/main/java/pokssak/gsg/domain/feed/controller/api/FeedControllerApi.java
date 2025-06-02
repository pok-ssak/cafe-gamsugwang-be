package pokssak.gsg.domain.feed.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import pokssak.gsg.domain.feed.dto.FeedRequest;
import pokssak.gsg.domain.feed.dto.FeedResponse;
import pokssak.gsg.domain.user.entity.User;

@Tag(name = "Feed", description = "피드 관련 API")
@RequestMapping("/api/v1/feeds")
public interface FeedControllerApi {

    @Operation(
            summary = "피드 생성",
            description = "새로운 피드를 생성합니다.",
            requestBody = @RequestBody(
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = FeedRequest.class),
                            examples = @ExampleObject(value = """
                    {
                        "title": "알림 제목",
                        "content": "내용입니다.",
                        "type": "NOTICE"
                    }
                """)
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "피드 생성 성공")
            }
    )
    @PostMapping
    ResponseEntity<Void> createFeed(@RequestBody FeedRequest feedRequest);

    @Operation(
            summary = "사용자 피드 조회 (페이징)",
            description = "인증된 사용자의 피드를 페이지 단위로 조회합니다.",
            parameters = {
                    @Parameter(name = "page", description = "페이지 번호", example = "0"),
                    @Parameter(name = "size", description = "페이지 크기", example = "20")
            },
            responses = @ApiResponse(responseCode = "200", description = "피드 목록 반환")
    )
    @GetMapping
    ResponseEntity<Page<FeedResponse>> getUserFeeds(
            @Parameter(hidden = true) @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    );

    @Operation(
            summary = "피드 읽음 처리",
            description = "지정한 피드를 읽음 상태로 변경합니다.",
            responses = @ApiResponse(responseCode = "200", description = "읽음 처리 완료")
    )
    @PutMapping("/{feedId}/read")
    ResponseEntity<Void> markAsRead(@PathVariable Long feedId);

    @Operation(
            summary = "모든 피드 읽음 처리",
            description = "해당 사용자의 모든 피드를 읽음 상태로 변경합니다.",
            responses = @ApiResponse(responseCode = "200", description = "일괄 읽음 처리 완료")
    )
    @PutMapping("/read-all")
    ResponseEntity<Void> markAllAsRead(@Parameter(hidden = true) @AuthenticationPrincipal User user);

    @Operation(
            summary = "안 읽은 피드 수 조회",
            description = "사용자의 읽지 않은 피드 개수를 반환합니다.",
            responses = @ApiResponse(responseCode = "200", description = "미확인 피드 수 반환")
    )
    @GetMapping("/unread-count")
    ResponseEntity<Long> getUnreadCount(@Parameter(hidden = true) @AuthenticationPrincipal User user);
}