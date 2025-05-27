package pokssak.gsg.domain.cafe.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pokssak.gsg.domain.cafe.entity.Menu;


@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MenuResponse {
    private Long id;
    private String name;
    private String menuImageUrl;
    private Integer price;

    @Builder
    public MenuResponse(Long id, String name, String menuImageUrl, Integer price) {
        this.id = id;
        this.name = name;
        this.menuImageUrl = menuImageUrl;
        this.price = price;
    }

    public static MenuResponse from(Menu menu) {
        return MenuResponse.builder()
                .id(menu.getId())
                .name(menu.getName())
                .menuImageUrl(menu.getMenuImageUrl())
                .price(menu.getPrice())
                .build();
    }
}
