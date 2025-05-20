package pokssak.gsg.domain.review.dto;

import lombok.Builder;
import pokssak.gsg.domain.review.entity.Review;

@Builder
public record ReviewCreateRequest(
        String title,
        String content,
        String imageUrl,
        int rate
){
}
