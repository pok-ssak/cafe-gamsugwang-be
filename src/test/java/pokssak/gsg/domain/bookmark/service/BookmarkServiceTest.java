package pokssak.gsg.domain.bookmark.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pokssak.gsg.common.exception.CustomException;
import pokssak.gsg.domain.bookmark.dto.BookmarkResponse;
import pokssak.gsg.domain.bookmark.entity.Bookmark;
import pokssak.gsg.domain.bookmark.exception.BookmarkErrorCode;
import pokssak.gsg.domain.bookmark.repository.BookmarkRepository;
import pokssak.gsg.domain.cafe.entity.Cafe;
import pokssak.gsg.domain.cafe.exception.CafeErrorCode;
import pokssak.gsg.domain.cafe.repository.CafeRepository;
import pokssak.gsg.domain.user.entity.User;
import pokssak.gsg.domain.user.exception.UserErrorCode;
import pokssak.gsg.domain.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookmarkServiceTest {

    @Mock
    private BookmarkRepository bookmarkRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CafeRepository cafeRepository;

    @InjectMocks
    private BookmarkService bookmarkService;

    @Test
    @DisplayName("북마크 추가 성공")
    void addBookmark_success() {
        Long userId = 1L;
        Long cafeId = 100L;

        User user = User.builder().id(userId).email("test@example.com").build();
        Cafe cafe = Cafe.builder().id(cafeId).title("테스트 카페").build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(cafeRepository.findById(cafeId)).thenReturn(Optional.of(cafe));
        when(bookmarkRepository.existsByUserAndCafe(user, cafe)).thenReturn(false);

        bookmarkService.addBookmark(userId, cafeId);

        verify(bookmarkRepository, times(1)).save(any(Bookmark.class));
    }

    @Test
    @DisplayName("북마크 추가 실패 - 존재하지 않는 유저")
    void addBookmark_userNotFound() {
        Long userId = 1L;
        Long cafeId = 10L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        CustomException exception = assertThrows(CustomException.class, () ->
                bookmarkService.addBookmark(userId, cafeId)
        );

        assertThat(exception.getErrorCode()).isEqualTo(UserErrorCode.NOT_FOUND);
    }

    @Test
    @DisplayName("북마크 추가 실패 - 존재하지 않는 카페")
    void addBookmark_cafeNotFound() {
        // given
        Long userId = 1L;
        Long cafeId = 10L;

        User user = User.builder().id(userId).build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(cafeRepository.findById(cafeId)).thenReturn(Optional.empty());

        CustomException exception = assertThrows(CustomException.class, () ->
                bookmarkService.addBookmark(userId, cafeId)
        );

        assertThat(exception.getErrorCode()).isEqualTo(CafeErrorCode.CAFE_NOT_FOUND);
    }

    @Test
    @DisplayName("이미 북마크한 카페일 경우 예외 발생")
    void addBookmark_alreadyExists() {
        Long userId = 1L;
        Long cafeId = 100L;
        User user = User.builder().id(userId).build();
        Cafe cafe = Cafe.builder().id(cafeId).build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(cafeRepository.findById(cafeId)).thenReturn(Optional.of(cafe));
        when(bookmarkRepository.existsByUserAndCafe(user, cafe)).thenReturn(true);

        CustomException exception = assertThrows(CustomException.class,
                () -> bookmarkService.addBookmark(userId, cafeId));

        assertThat(exception.getErrorCode()).isEqualTo(BookmarkErrorCode.BOOKMARK_ALREADY_EXIST);
    }

    @Test
    @DisplayName("북마크 삭제 성공")
    void removeBookmark_success() {
        Long userId = 1L;
        Long cafeId = 200L;
        User user = User.builder().id(userId).build();
        Cafe cafe = Cafe.builder().id(cafeId).build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(cafeRepository.findById(cafeId)).thenReturn(Optional.of(cafe));

        bookmarkService.removeBookmark(userId, cafeId);

        verify(bookmarkRepository, times(1)).deleteByUserAndCafe(user, cafe);
    }

    @Test
    @DisplayName("북마크 삭제 실패 - 존재하지 않는 유저")
    void removeBookmark_userNotFound() {
        Long userId = 1L;
        Long cafeId = 100L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        CustomException exception = assertThrows(CustomException.class, () ->
                bookmarkService.removeBookmark(userId, cafeId)
        );

        assertThat(exception.getErrorCode()).isEqualTo(UserErrorCode.NOT_FOUND);
    }

    @Test
    @DisplayName("북마크 삭제 실패 - 존재하지 않는 카페")
    void removeBookmark_cafeNotFound() {
        Long userId = 1L;
        Long cafeId = 100L;
        User user = User.builder().id(userId).build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(cafeRepository.findById(cafeId)).thenReturn(Optional.empty());

        CustomException exception = assertThrows(CustomException.class, () ->
                bookmarkService.removeBookmark(userId, cafeId)
        );

        assertThat(exception.getErrorCode()).isEqualTo(CafeErrorCode.CAFE_NOT_FOUND);
    }


    @Test
    @DisplayName("유저의 북마크 조회 성공")
    void getUserBookmarks_success() {
        Long userId = 1L;
        User user = User.builder().id(userId).email("test@example.com").build();
        Cafe cafe1 = Cafe.builder().id(10L).title("카페1").build();
        Cafe cafe2 = Cafe.builder().id(20L).title("카페2").build();

        Bookmark bookmark1 = Bookmark.builder().user(user).cafe(cafe1).build();
        Bookmark bookmark2 = Bookmark.builder().user(user).cafe(cafe2).build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(bookmarkRepository.findAllByUser(user)).thenReturn(List.of(bookmark1, bookmark2));

        List<BookmarkResponse> responses = bookmarkService.getUserBookmarks(userId);

        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).title()).isEqualTo("카페1");
        assertThat(responses.get(1).id()).isEqualTo(20L);
    }

    @Test
    @DisplayName("존재하지 않는 유저일 경우 예외 발생")
    void getUserBookmarks_userNotFound() {
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        CustomException exception = assertThrows(CustomException.class, () ->
                bookmarkService.getUserBookmarks(userId)
        );

        assertThat(exception.getErrorCode()).isEqualTo(UserErrorCode.NOT_FOUND);
    }
}
