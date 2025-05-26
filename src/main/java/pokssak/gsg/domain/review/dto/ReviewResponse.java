package pokssak.gsg.domain.review.dto;

import lombok.Builder;
import pokssak.gsg.domain.review.entity.Review;

@Builder
public record ReviewResponse(
        Long reviewId,
        String content,
        String userNickName,
        String imageUrl,
        int rate,
        boolean likedByUser,
        long likeCount

){
    public static ReviewResponse from(Review review, boolean likedByUser) {
        return ReviewResponse.builder()
                .reviewId(review.getId())
                .content(review.getContent())
                .userNickName(review.getUser().getNickName())
                .imageUrl(review.getImageUrl())
                .rate(review.getRate())
                .likedByUser(likedByUser) // 엔티티에서 직접 가져오는 값이 아님.
                .likeCount(review.getLikeCount())
                .build();
    }
}
