package pokssak.gsg.domain.cafe.dto;

import lombok.Builder;
import lombok.Getter;
import pokssak.gsg.domain.cafe.entity.CafeKeyword;

@Getter
@Builder
public class KeywordResponse {
    private Long id;
    private String keyword;
    private Integer count;

    public static KeywordResponse from(CafeKeyword cafeKeyword) {
        return KeywordResponse.builder()
                .id(cafeKeyword.getId())
                .keyword(cafeKeyword.getKeyword())
                .count(cafeKeyword.getCount())
                .build();
    }
}
