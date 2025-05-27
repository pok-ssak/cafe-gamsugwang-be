package pokssak.gsg.domain.feed.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import pokssak.gsg.domain.feed.dto.FeedRequest;
import pokssak.gsg.domain.feed.dto.FeedResponse;
import pokssak.gsg.domain.feed.dto.PageRequestDto;
import pokssak.gsg.domain.feed.service.FeedService;
import pokssak.gsg.domain.user.entity.User;

@RestController
@RequestMapping("/api/v1/feeds")
@RequiredArgsConstructor
public class FeedController {

    private final FeedService feedService;

    // 피드 생성
    @PostMapping
    public ResponseEntity<Void> createFeed(@RequestBody FeedRequest feedRequest) {
        feedService.createFeed(feedRequest);
        return ResponseEntity.ok().build();
    }

    // 특정 유저 피드 페이징 조회
    @GetMapping
    public ResponseEntity<Page<FeedResponse>> getUserFeeds(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        PageRequestDto pageRequestDto = new PageRequestDto(page, size);
        Pageable pageable = pageRequestDto.toPageable();
        Page<FeedResponse> feeds = feedService.getUserFeeds(user.getId(), pageable);
        return ResponseEntity.ok(feeds);
    }


    // 피드 읽음 처리
    @PutMapping("/{feedId}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long feedId) {
        feedService.markAsRead(feedId);
        return ResponseEntity.ok().build();
    }

    // 피드 일괄 읽음 처리
    @PutMapping("/read-all")
    public ResponseEntity<Void> markAllAsRead(@AuthenticationPrincipal User user) {
        feedService.markAllAsRead(user.getId());
        return ResponseEntity.ok().build();
    }

    // 안 읽은 피드 카운트 조회
    @GetMapping("/unread-count")
    public ResponseEntity<Long> getUnreadCount(@AuthenticationPrincipal User user) {
        long count = feedService.getUnreadCount(user.getId());
        return ResponseEntity.ok(count);
    }
}
