package pokssak.gsg.domain.review.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pokssak.gsg.common.exception.CustomException;
import pokssak.gsg.domain.cafe.service.CafeService;
import pokssak.gsg.domain.review.dto.ReviewCreateRequest;
import pokssak.gsg.domain.review.dto.ReviewResponse;
import pokssak.gsg.domain.review.entity.Review;
import pokssak.gsg.domain.review.exception.ReviewErrorCode;
import pokssak.gsg.domain.review.repository.ReviewRepository;
import pokssak.gsg.domain.user.service.UserService;

@Slf4j
@RequiredArgsConstructor
@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewValidationService reviewValidationService;
    private final UserService userService; // Facade 고려
    private final CafeService cafeService;


    /** 리뷰 전체 조회 */
    public Page<ReviewResponse> getReviews(Pageable pageable) {
        log.info("리뷰 조회 pageable={}", pageable);
        return reviewRepository.findAll(pageable)
                .map(ReviewResponse::from);
    }

    /** 카페별 리뷰 전체 조회 */
    public Page<ReviewResponse> getReviews(Long cafeId, Pageable pageable) {
        log.info("리뷰 카페별 조회 cafeId={}, pageable={}", cafeId, pageable);
        return reviewRepository.findByCafeId(cafeId, pageable)
                .map(ReviewResponse::from);
    }

    /** 리뷰 상세 조회 */
    public ReviewResponse getReviewById(Long reviewId) {
        log.info("리뷰 상세 조회 reviewId={}", reviewId);
        var review = reviewRepository.findById(reviewId)
                .orElseThrow(()-> new CustomException(ReviewErrorCode.NOT_FOUND));
        return ReviewResponse.from(review);
    }

    /** 리뷰 생성 */
    // userId로만 전달해야할까?
    // Facade패턴을 적용한다고 하더라도 user를 전달하면? 트랜젝션 범위 커짐...
    // user로 전달안한다면 결국 여기서 조회가 발생하니 커플링 상승
    @Transactional
    public Long createReview(Long userId, ReviewCreateRequest reviewCreateRequest) {

        log.info("리뷰 추가 id={}, body={}", userId, reviewCreateRequest);

        var user = userService.getUserById(userId);
        var cafe = cafeService.getCafeById(reviewCreateRequest.cafeId());

        // 카페당 24시간 당 1 리뷰 제한 검증
        reviewValidationService.validateTodayLimit(user.getId(), cafe.getId());

        var review = Review.builder()
                .content(reviewCreateRequest.content())
                .rate(reviewCreateRequest.rate())
                .imageUrl(reviewCreateRequest.imageUrl())
                .cafe(cafe)
                .user(user)
                .build();

        var saved = reviewRepository.save(review);
        return saved.getId();
    }

    /** 삭제 */
    public void deleteReview(Long reviewId) {
        log.info("리뷰 삭제 reviewId={}", reviewId);
        var review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new CustomException(ReviewErrorCode.NOT_FOUND));
        reviewRepository.delete(review);
    }
}
