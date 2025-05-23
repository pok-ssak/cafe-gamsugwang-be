package pokssak.gsg.domain.feed.dto;

import lombok.Builder;
import pokssak.gsg.domain.feed.entity.Feed;
import pokssak.gsg.domain.feed.entity.FeedType;
import pokssak.gsg.domain.user.entity.User;

import java.time.LocalDateTime;

@Builder
public record FeedRequest(
        Long userId,
        String content,
        String url,
        FeedType type
){}
