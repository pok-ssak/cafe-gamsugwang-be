package pokssak.gsg.domain.feed.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import pokssak.gsg.common.exception.CustomException;
import pokssak.gsg.domain.feed.dto.FeedRequest;
import pokssak.gsg.domain.feed.entity.Feed;
import pokssak.gsg.domain.feed.entity.FeedType;
import pokssak.gsg.domain.feed.exception.FeedErrorCode;
import pokssak.gsg.domain.feed.repository.FeedRepository;
import pokssak.gsg.domain.user.entity.User;
import pokssak.gsg.domain.user.exception.UserErrorCode;
import pokssak.gsg.domain.user.repository.UserRepository;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FeedServiceTest {

    @InjectMocks
    private FeedService feedService;

    @Mock
    private FeedRepository feedRepository;

    @Mock
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder().id(1L).nickName("tester").build();
    }

    @Test
    void createFeed_성공() {
        FeedRequest request = FeedRequest.builder()
                .userId(1L)
                .content("피드 내용")
                .url("/reviews/123")
                .type(FeedType.LIKE)
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        feedService.createFeed(request);

        verify(feedRepository).save(any(Feed.class));
    }

    @Test
    void createFeed_유저없으면_예외() {
        FeedRequest request = FeedRequest.builder()
                .userId(999L)
                .content("피드 내용")
                .url("/reviews/123")
                .type(FeedType.LIKE)
                .build();

        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        CustomException exception = assertThrows(CustomException.class, () -> feedService.createFeed(request));

        assertThat(exception.getErrorCode()).isEqualTo(UserErrorCode.NOT_FOUND);
    }

    @Test
    void getUserFeeds_성공() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        Feed feed = Feed.builder().user(user).content("테스트").type(FeedType.LIKE).isRead(false).build();
        Page<Feed> page = new PageImpl<>(List.of(feed));

        when(feedRepository.findByUserId(eq(1L), any(Pageable.class)))
                .thenReturn(page);

        Page<?> result = feedService.getUserFeeds(1L, pageRequest);

        assertThat(result.getContent()).hasSize(1);
        verify(feedRepository).findByUserId(eq(1L), any(Pageable.class));
    }

    @Test
    void markAsRead_성공() {
        Feed feed = Feed.builder()
                .id(3L)
                .user(user)
                .content("test")
                .url("some-url")
                .type(FeedType.LIKE)
                .isRead(false)
                .build();

        when(feedRepository.findById(3L)).thenReturn(Optional.of(feed));
        doNothing().when(feedRepository).markAsReadById(3L);

        feedService.markAsRead(3L);

        verify(feedRepository).findById(3L);
        verify(feedRepository).markAsReadById(3L);
    }


    @Test
    void markAsRead_피드없으면_예외() {
        when(feedRepository.findById(999L)).thenReturn(Optional.empty());

        CustomException exception = assertThrows(CustomException.class, () -> feedService.markAsRead(999L));

        assertThat(exception.getErrorCode()).isEqualTo(FeedErrorCode.FEED_NOT_FOUND);
    }

    @Test
    void getUnreadCount_정상작동() {
        when(feedRepository.countByUserIdAndIsReadFalse(1L)).thenReturn(3L);

        long count = feedService.getUnreadCount(1L);

        assertThat(count).isEqualTo(3);
    }
}
