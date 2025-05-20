package pokssak.gsg.domain.review.dto;

import lombok.Builder;
import pokssak.gsg.domain.review.entity.Review;

@Builder
public record ReviewResponse(
        Long reviewId,
        String title,
        String content,
        String userNickName,
        String imageUrl,
        int rate
){
    public static ReviewResponse from(Review review) {
        return ReviewResponse.builder()
                .reviewId(review.getId())
                .title(review.getTitle())
                .content(review.getContent())
                .userNickName(review.getUser().getNickName())
                .imageUrl(review.getImageUrl())
                .rate(review.getRate())
                .build();
    }
}
