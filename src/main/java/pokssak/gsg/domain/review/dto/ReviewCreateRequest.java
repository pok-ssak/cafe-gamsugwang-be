package pokssak.gsg.domain.review.dto;

import lombok.Builder;
import pokssak.gsg.domain.review.entity.Review;

@Builder
public record ReviewCreateRequest(
        Long cafeId,
        String content,
        String imageUrl,
        int rate
){
}
