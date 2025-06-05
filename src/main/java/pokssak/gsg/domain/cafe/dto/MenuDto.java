package pokssak.gsg.domain.cafe.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MenuDto {
    private String name;
    private String menuImageUrl;
    private Integer price;
    private String modifier;
}