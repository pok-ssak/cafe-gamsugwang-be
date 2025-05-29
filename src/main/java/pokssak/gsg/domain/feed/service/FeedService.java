package pokssak.gsg.domain.feed.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pokssak.gsg.common.exception.CustomException;
import pokssak.gsg.domain.feed.dto.FeedRequest;
import pokssak.gsg.domain.feed.dto.FeedResponse;
import pokssak.gsg.domain.feed.entity.Feed;
import pokssak.gsg.domain.feed.exception.FeedErrorCode;
import pokssak.gsg.domain.feed.repository.FeedRepository;
import pokssak.gsg.domain.user.entity.User;
import pokssak.gsg.domain.user.exception.UserErrorCode;
import pokssak.gsg.domain.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class FeedService {

    private final FeedRepository feedRepository;
    private final UserRepository userRepository;

    public void createFeed(FeedRequest feedRequest) {
        User user = userRepository.findById(feedRequest.userId())
                .orElseThrow(() -> new CustomException(UserErrorCode.NOT_FOUND));

        Feed feed = Feed.builder()
                .user(user)
                .content(feedRequest.content())
                .url(feedRequest.url())
                .type(feedRequest.type())
                .isRead(false)
                .build();

        feedRepository.save(feed);
    }

    public Page<FeedResponse> getUserFeeds(Long userId, Pageable pageable) {
        return feedRepository.findByUserId(userId, pageable)
                .map(FeedResponse::from);
    }


    @Transactional
    public void markAsRead(Long feedId) {
        feedRepository.findById(feedId)
                .orElseThrow(() -> new CustomException(FeedErrorCode.FEED_NOT_FOUND));

        feedRepository.markAsReadById(feedId);
    }

    @Transactional
    public void markAllAsRead(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(UserErrorCode.NOT_FOUND));

        feedRepository.markAllAsReadByUserId(userId);
    }

    public long getUnreadCount(Long userId) {
        return feedRepository.countByUserIdAndIsReadFalse(userId);
    }
}
