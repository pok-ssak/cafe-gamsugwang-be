package pokssak.gsg.domain.bookmark.dto;

import lombok.Builder;
import pokssak.gsg.domain.bookmark.entity.Bookmark;

import java.math.BigDecimal;

@Builder
public record BookmarkResponse (
        Long cafeId,
        String cafeName,
        String address,
        BigDecimal rate,
        String imageUrl
){
    public static BookmarkResponse from(Bookmark bookmark) {
        return BookmarkResponse.builder()
                .cafeId(bookmark.getCafe().getId())
                .cafeName(bookmark.getCafe().getTitle())
                .address(bookmark.getCafe().getAddress())
                .rate(bookmark.getCafe().getRate())
                .imageUrl(bookmark.getCafe().getImageUrl())
                .build();
    }
}
