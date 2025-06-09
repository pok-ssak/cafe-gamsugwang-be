package pokssak.gsg.domain.review.dto;

import lombok.Builder;
import pokssak.gsg.domain.review.entity.Review;

@Builder
public record ReviewResponse(
        Long id,
        String content,
        String nickname,
        String imageUrl,
        String profileImageUrl,
        float rating,
        boolean likedByUser,
        long likeCount

){
    public static ReviewResponse from(Review review, boolean likedByUser) {
        return ReviewResponse.builder()
                .id(review.getId())
                .content(review.getContent())
                .nickname(review.getUser().getNickName())
                .imageUrl(review.getImageUrl())
                .profileImageUrl(review.getUser().getImageUrl())
                .rating(review.getRating())
                .likedByUser(likedByUser) // 엔티티에서 직접 가져오는 값이 아님.
                .likeCount(review.getLikeCount())
                .build();
    }
}
