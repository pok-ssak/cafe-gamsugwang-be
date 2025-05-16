package pokssak.gsg.common.vo;

import lombok.Builder;

@Builder
public record Keyword (
        String word,
        long count
) {
}
