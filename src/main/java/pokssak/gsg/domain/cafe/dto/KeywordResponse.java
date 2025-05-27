package pokssak.gsg.domain.cafe.dto;

import lombok.Builder;
import lombok.Getter;
import pokssak.gsg.domain.cafe.entity.Keyword;

@Getter
@Builder
public class KeywordResponse {
    private Long id;
    private String keyword;
    private Integer count;

    public static KeywordResponse from(Keyword keyword) {
        return KeywordResponse.builder()
                .id(keyword.getId())
                .keyword(keyword.getKeyword())
                .count(keyword.getCount())
                .build();
    }
}
