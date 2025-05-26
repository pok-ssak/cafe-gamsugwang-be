package pokssak.gsg.domain.review.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pokssak.gsg.common.exception.CustomException;
import pokssak.gsg.domain.review.exception.ReviewErrorCode;
import pokssak.gsg.domain.review.repository.ReviewRepository;

import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
@Service
public class ReviewValidationService {

    private final ReviewRepository reviewRepository;

    public void validateTodayLimit(Long userId, Long cafeId) {
        log.info("리뷰 일일 경과 검증 userId={}, cafeId={}", userId, cafeId);
//        var latest = user.getReviews().stream()
//                .filter(r -> r.getCafe() == cafe)
//                .sorted(Comparator.comparing(Review::getModifiedAt).reversed()) // 최신순 정렬
//                .findFirst();
        var latest = reviewRepository.findTopByUserIdAndCafeIdOrderByModifiedAtDesc(userId, cafeId);

        if (latest.isPresent() && latest.get().getModifiedAt().isAfter(LocalDateTime.now().minusDays(1))) {
            log.warn("리뷰 일일 결과 검증 실패 userId={}, cafeId={}", userId, cafeId);
            throw new CustomException(ReviewErrorCode.ALREADY_POST_TODAY);
        }
    }
}
