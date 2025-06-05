package pokssak.gsg.domain.cafe.entity;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;

class CafeTest {

    @DisplayName("카페 메뉴리스트에 메뉴를 추가한다.")
    @Test
    void addMenu() {
        // Given
        Cafe cafe = Cafe.builder()
                .id(1L)
                .title("카페")
                .menuList(new HashSet<>())
                .build();

        Menu menu = Menu.builder()
                .name("Coffee")
                .price(5000)
                .cafe(cafe)
                .build();

        // When
        cafe.addMenu(menu);

        // Then
        Assertions.assertThat(cafe.getMenuList()).hasSize(1)
                        .extracting(Menu::getName, Menu::getPrice)
                        .containsExactlyInAnyOrder(
                                Assertions.tuple("Coffee", 5000)
                        );
    }

    @DisplayName("카페 키워드 리스트에 키워드를 추가한다.")
    @Test
    void addKeyword() {
        // Given
        Cafe cafe = Cafe.builder()
                .id(1L)
                .title("CafeA")
                .imageUrl("img.jpg")
                .menuList(new HashSet<>())
                .build();

        CafeKeyword cafeKeyword1 = CafeKeyword.builder()
                .keyword("편안한")
                .count(1)
                .cafe(cafe)
                .build();

        CafeKeyword cafeKeyword2 = CafeKeyword.builder()
                .keyword("아늑한")
                .count(2)
                .cafe(cafe)
                .build();
        // When
        cafe.addKeyword(cafeKeyword1);
        cafe.addKeyword(cafeKeyword2);

        // Then
        Assertions.assertThat(cafe.getCafeKeywordList()).hasSize(2)
                .extracting(CafeKeyword::getKeyword, CafeKeyword::getCount)
                .containsExactlyInAnyOrder(
                        Assertions.tuple("편안한", 1),
                        Assertions.tuple("아늑한", 2)
                );
    }
}