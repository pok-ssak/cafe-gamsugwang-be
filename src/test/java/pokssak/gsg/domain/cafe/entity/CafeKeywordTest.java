package pokssak.gsg.domain.cafe.entity;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;

class CafeKeywordTest {

    @DisplayName("키워드 객체의 카페 속성을 업데이트한다.")
    @Test
    void updateCafe() {
        // Given
        CafeKeyword cafeKeyword = CafeKeyword.builder()
                .id(1L)
                .keyword("편안한")
                .count(1)
                .build();

        Cafe cafe = Cafe.builder()
                .id(1L)
                .title("CafeA")
                .menuList(new HashSet<>())
                .build();

        // When
        cafeKeyword.updateCafe(cafe);

        // Then
        Assertions.assertThat(cafeKeyword.getCafe())
                .extracting("id", "title")
                .containsExactlyInAnyOrder(1L, "CafeA");
    }
}