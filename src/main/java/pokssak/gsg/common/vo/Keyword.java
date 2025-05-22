package pokssak.gsg.common.vo;

import jakarta.persistence.Embeddable;
import lombok.Builder;

@Embeddable
@Builder
public record Keyword (
        String word,
        long count
) {
}
