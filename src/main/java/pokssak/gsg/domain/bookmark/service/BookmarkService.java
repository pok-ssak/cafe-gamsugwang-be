package pokssak.gsg.domain.bookmark.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final UserRepository userRepository;
    private final CafeRepository cafeRepository;

    @Transactional
    public void addBookmark(Long userId, Long cafeId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(UserErrorCode.NOT_FOUND));
        Cafe cafe = cafeRepository.findById(cafeId)
                .orElseThrow(() -> new CustomException(CafeErrorCode.CAFE_NOT_FOUND));

        if (bookmarkRepository.existsByUserAndCafe(user, cafe)) {
            throw new CustomException(BookmarkErrorCode.BOOKMARK_ALREADY_EXIST);
        }

        Bookmark bookmark = Bookmark.builder()
                .user(user)
                .cafe(cafe)
                .build();

        user.getBookmarks().add(bookmark);
        bookmarkRepository.save(bookmark);
        log.info("관심목록 추가 성공 - userId={} cafeId={}", userId, cafeId);
    }

    @Transactional
    public void removeBookmark(Long userId, Long cafeId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(UserErrorCode.NOT_FOUND));
        Cafe cafe = cafeRepository.findById(cafeId)
                .orElseThrow(() -> new CustomException(CafeErrorCode.CAFE_NOT_FOUND));

        bookmarkRepository.deleteByUserAndCafe(user, cafe);
        log.info("관심목록 제거 성공 - userId={} cafeId={}", userId, cafeId);
    }

    @Transactional(readOnly = true)
    public List<BookmarkResponse> getUserBookmarks(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(UserErrorCode.NOT_FOUND));

        return bookmarkRepository.findAllByUser(user).stream()
                .map(BookmarkResponse::from)
                .toList();
    }
}

