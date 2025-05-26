package pokssak.gsg.domain.feed.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import pokssak.gsg.common.exception.CustomException;
import pokssak.gsg.domain.feed.dto.FeedRequest;
import pokssak.gsg.domain.feed.dto.FeedResponse;
import pokssak.gsg.domain.feed.entity.FeedType;
import pokssak.gsg.domain.feed.exception.FeedErrorCode;
import pokssak.gsg.domain.feed.service.FeedService;
import pokssak.gsg.domain.user.entity.JoinType;
import pokssak.gsg.domain.user.entity.User;
import pokssak.gsg.domain.user.exception.UserErrorCode;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class FeedControllerTest {

    @InjectMocks
    private FeedController feedController;

    @Mock
    private FeedService feedService;

    private FeedRequest feedRequest;
    private FeedResponse feedResponse;
    private User testUser;

    @BeforeEach
    void setUp() {
        feedRequest = FeedRequest.builder()
                .userId(1L)
                .content("피드 내용")
                .url("/reviews/123")
                .type(FeedType.LIKE)
                .build();

        feedResponse = FeedResponse.builder()
                .content("좋아요가 눌렸습니다")
                .url("/reviews/123")
                .type(FeedType.LIKE)
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();

        testUser = User.builder()
                .id(1L)
                .email("test@example.com")
                .nickName("tester")
                .imageUrl("https://image.com/test.png")
                .joinType(JoinType.LOCAL)
                .userKeywords(List.of())
                .build();
    }

    @Test
    void createFeed_성공() {
        ResponseEntity<Void> response = feedController.createFeed(feedRequest);

        verify(feedService).createFeed(feedRequest);
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
    }

    @Test
    void createFeed_유저없음_예외() {
        doThrow(new CustomException(UserErrorCode.NOT_FOUND))
                .when(feedService).createFeed(any(FeedRequest.class));

        assertThrows(CustomException.class, () -> {
            feedController.createFeed(feedRequest);
        });
    }

    @Test
    void getUserFeeds_성공() {
        Page<FeedResponse> page = new PageImpl<>(List.of(feedResponse), PageRequest.of(0, 20), 1);

        when(feedService.getUserFeeds(eq(1L), any(Pageable.class))).thenReturn(page);

        ResponseEntity<Page<FeedResponse>> response = feedController.getUserFeeds(testUser, 0, 20);

        verify(feedService).getUserFeeds(eq(1L), any(Pageable.class));
        assertThat(response.getBody()).isEqualTo(page);
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
    }

    @Test
    void getUserFeeds_예외() {
        when(feedService.getUserFeeds(eq(1L), any(Pageable.class)))
                .thenThrow(new CustomException(UserErrorCode.NOT_FOUND));

        assertThrows(CustomException.class, () -> {
            feedController.getUserFeeds(testUser, 0, 20);
        });
    }



    @Test
    void markAsRead_성공() {
        ResponseEntity<Void> response = feedController.markAsRead(5L);

        verify(feedService).markAsRead(5L);
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
    }

    @Test
    void markAsRead_피드없음_예외() {
        doThrow(new CustomException(FeedErrorCode.FEED_NOT_FOUND))
                .when(feedService).markAsRead(999L);

        assertThrows(CustomException.class, () -> {
            feedController.markAsRead(999L);
        });
    }

    @Test
    void getUnreadCount_성공() {
        when(feedService.getUnreadCount(1L)).thenReturn(3L);

        ResponseEntity<Long> response = feedController.getUnreadCount(testUser);

        verify(feedService).getUnreadCount(1L);
        assertThat(response.getBody()).isEqualTo(3L);
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
    }

    @Test
    void getUnreadCount_예외() {
        when(feedService.getUnreadCount(1L))
                .thenThrow(new CustomException(UserErrorCode.NOT_FOUND));

        assertThrows(CustomException.class, () -> {
            feedController.getUnreadCount(testUser);
        });
    }
}
