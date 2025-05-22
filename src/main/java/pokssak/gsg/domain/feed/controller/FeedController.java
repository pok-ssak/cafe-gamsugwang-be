package pokssak.gsg.domain.feed.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pokssak.gsg.domain.feed.dto.FeedRequest;
import pokssak.gsg.domain.feed.dto.FeedResponse;
import pokssak.gsg.domain.feed.service.FeedService;

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
    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<FeedResponse>> getUserFeeds(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Page<FeedResponse> feeds = feedService.getUserFeeds(userId, page, size);
        return ResponseEntity.ok(feeds);
    }

    // 피드 읽음 처리
    @PutMapping("/{feedId}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long feedId) {
        feedService.markAsRead(feedId);
        return ResponseEntity.ok().build();
    }

    // 안 읽은 피드 카운트 조회
    @GetMapping("/user/{userId}/unread-count")
    public ResponseEntity<Long> getUnreadCount(@PathVariable Long userId) {
        long count = feedService.getUnreadCount(userId);
        return ResponseEntity.ok(count);
    }
}
