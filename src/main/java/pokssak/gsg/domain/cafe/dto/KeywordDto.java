package pokssak.gsg.domain.cafe.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class KeywordDto {
    private Long id;
    private String keyword;
    private Integer count;
}