package pokssak.gsg.domain.cafe.entity;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CafeDocumentTest {

    @DisplayName("CafeDocument 객체의 KeywordVector를 업데이트 한다.")
    @Test
    void updateKeywordVector() {
        // Given
        float[] keywordVector1 = {0.1f, 0.2f, 0.3f};
        float[] keywordVector2 = {0.4f, 0.5f, 0.6f};

        CafeDocument cafeDocument = CafeDocument.builder()
                .id(1L)
                .title("CafeA")
                .keywordVector(keywordVector1)
                .build();

        // When
        cafeDocument.updateKeywordVector(keywordVector2);

        // Then
        Assertions.assertThat(cafeDocument.getKeywordVector())
                .isEqualTo(keywordVector2);
    }

}