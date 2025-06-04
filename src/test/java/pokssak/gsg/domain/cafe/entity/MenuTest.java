package pokssak.gsg.domain.cafe.entity;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

class MenuTest {

    @DisplayName("메뉴 객체의 카페 속성을 업데이트한다.")
    @Test
    void updateCafe() {
        // Given
        Menu menu = Menu.builder()
                .name("Coffee")
                .price(5000)
                .build();

        Cafe cafe = Cafe.builder()
                .id(1L)
                .title("카페")
                .menuList(new HashSet<>())
                .build();

        // When
        menu.updateCafe(cafe);

        // Then
        Assertions.assertThat(menu.getCafe())
                .extracting("id", "title")
                .containsExactlyInAnyOrder(1L, "카페");
    }
}