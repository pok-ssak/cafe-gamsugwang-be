package pokssak.gsg.domain.cafe.entity;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

class KeywordTest {

    @DisplayName("키워드 객체의 카페 속성을 업데이트한다.")
    @Test
    void updateCafe() {
        // Given
        Keyword keyword = Keyword.builder()
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
        keyword.updateCafe(cafe);

        // Then
        Assertions.assertThat(keyword.getCafe())
                .extracting("id", "title")
                .containsExactlyInAnyOrder(1L, "CafeA");
    }
}