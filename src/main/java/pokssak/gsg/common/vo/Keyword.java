package pokssak.gsg.common.vo;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Embeddable
@Builder
public record Keyword (
        String word,
        Long count
) {
}
