package pokssak.gsg.domain.review.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pokssak.gsg.common.exception.CustomException;
import pokssak.gsg.domain.review.dto.ReviewCreateRequest;
import pokssak.gsg.domain.review.dto.ReviewResponse;
import pokssak.gsg.domain.review.entity.Review;
import pokssak.gsg.domain.review.exception.ReviewErrorCode;
import pokssak.gsg.domain.review.repository.ReviewRepository;
import pokssak.gsg.domain.user.entity.User;
import pokssak.gsg.domain.user.service.UserService;

@Slf4j
@RequiredArgsConstructor
@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserService userService; // Facade 고려

    /** 리뷰 전체 조회 */
    public Page<ReviewResponse> getAllReviews(Pageable pageable) {
        return reviewRepository.findAll(pageable)
                .map(ReviewResponse::from);
    }



    /** 리뷰 상세 조회 */
    public ReviewResponse getReviewById(Long reviewId) {
        var review = reviewRepository.findById(reviewId)
                .orElseThrow(()-> new CustomException(ReviewErrorCode.NOT_FOUND));
        return ReviewResponse.from(review);
    }

    /** 리뷰 생성 */
    // userId로만 전달해야할까?
    // Facade패턴을 적용한다고 하더라도 user를 전달하면? 트랜젝션 범위 커짐...
    // user로 전달안한다면 결국 여기서 조회가 발생하니 커플링 상승
    public Long createReview(Long userId, ReviewCreateRequest reviewCreateRequest) {
//        var user = userService.getUserById(userId);
        var user = User.builder().build();
        var review = Review.builder()
                .title(reviewCreateRequest.title())
                .content(reviewCreateRequest.content())
                .user(user)
                .build();
        var saved = reviewRepository.save(review);
        return saved.getId();
    }

    /** 삭제 */
    public void deleteReview(Long reviewId) {
        reviewRepository.deleteById(reviewId);
    }
}
