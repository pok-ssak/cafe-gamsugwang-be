package pokssak.gsg.domain.bookmark.dto;

import lombok.Builder;
import pokssak.gsg.domain.bookmark.entity.Bookmark;
import pokssak.gsg.domain.cafe.entity.Cafe;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record BookmarkResponse (
        Long id,
        String title,
        BigDecimal rate,
        Integer reviewCount,
        String imageUrl,
        String address,
        String zipcode
){
    public static BookmarkResponse from(Bookmark bookmark) {
        var cafe = bookmark.getCafe();
        return BookmarkResponse.builder()
                .id(cafe.getId())
                .title(cafe.getTitle())
                .rate(cafe.getRate())
                .reviewCount(cafe.getRateCount())
                .imageUrl(cafe.getImageUrl())
                .address(cafe.getAddress())
                .zipcode(cafe.getZipcode())
                .build();
    }
}
