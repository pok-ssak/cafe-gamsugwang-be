package pokssak.gsg.domain.feed.dto;

import lombok.Builder;
import pokssak.gsg.domain.feed.entity.Feed;
import pokssak.gsg.domain.feed.entity.FeedType;

import java.time.LocalDateTime;

@Builder
public record FeedResponse (
        Long feedId,
        String content,
        String url,
        FeedType type,
        boolean isRead,
        LocalDateTime createdAt
){
    public static FeedResponse from(Feed feed) {
        return FeedResponse.builder()
                .feedId(feed.getId())
                .content(feed.getContent())
                .url(feed.getUrl())
                .type(feed.getType())
                .isRead(feed.isRead())
                .createdAt(feed.getCreatedAt())
                .build();
    }
}
