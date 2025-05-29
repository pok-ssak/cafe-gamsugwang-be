package pokssak.gsg.domain.bookmark.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import pokssak.gsg.domain.bookmark.dto.BookmarkResponse;
import pokssak.gsg.domain.bookmark.service.BookmarkService;
import pokssak.gsg.domain.user.entity.User;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookmarkControllerTest {

    @Mock
    private BookmarkService bookmarkService;

    @InjectMocks
    private BookmarkController bookmarkController;

    private User mockUser;

    @BeforeEach
    void setUp() {
        mockUser = User.builder()
                .id(1L)
                .email("user@example.com")
                .nickName("tester")
                .build();
    }

    @Test
    @DisplayName("addBookmark-성공")
    void addBookmark_success() {
        Long cafeId = 100L;

        ResponseEntity<Void> response = bookmarkController.addBookmark(cafeId, mockUser);

        verify(bookmarkService, times(1)).addBookmark(mockUser.getId(), cafeId);
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
    }

    @Test
    @DisplayName("removeBookmark-성공")
    void removeBookmark_success() {
        Long cafeId = 100L;

        ResponseEntity<Void> response = bookmarkController.removeBookmark(cafeId, mockUser);

        verify(bookmarkService, times(1)).removeBookmark(mockUser.getId(), cafeId);
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
    }

    @Test
    @DisplayName("getBookmarks-성공")
    void getBookmarks_success() {
        List<BookmarkResponse> mockResponses = List.of(
                new BookmarkResponse(1L, "Cafe A"),
                new BookmarkResponse(2L, "Cafe B")
        );

        when(bookmarkService.getUserBookmarks(mockUser.getId())).thenReturn(mockResponses);

        ResponseEntity<List<BookmarkResponse>> response = bookmarkController.getBookmarks(mockUser);

        verify(bookmarkService, times(1)).getUserBookmarks(mockUser.getId());
        assertThat(response.getBody()).isEqualTo(mockResponses);
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
    }
}
