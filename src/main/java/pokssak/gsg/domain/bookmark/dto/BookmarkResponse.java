package pokssak.gsg.domain.bookmark.dto;

import lombok.Builder;
import pokssak.gsg.domain.bookmark.entity.Bookmark;
import pokssak.gsg.domain.cafe.entity.Cafe;

import java.util.List;

@Builder
public record BookmarkResponse (
        Long cafeId,
        String cafeName
){
    public static BookmarkResponse from(Bookmark bookmark) {
        return BookmarkResponse.builder()
                .cafeId(bookmark.getCafe().getId())
                .cafeName(bookmark.getCafe().getTitle())
                .build();
    }
}
