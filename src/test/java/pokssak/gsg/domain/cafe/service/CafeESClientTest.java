package pokssak.gsg.domain.cafe.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.embedding.Embedding;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.*;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;
import pokssak.gsg.domain.cafe.dto.AutoCompleteResponse;
import pokssak.gsg.domain.cafe.dto.RecommendResponse;
import pokssak.gsg.domain.cafe.dto.SearchCafeResponse;
import pokssak.gsg.domain.cafe.entity.CafeDocument;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CafeESClientTest {
    @Mock
    ElasticsearchOperations operations;
    @Mock
    org.springframework.ai.embedding.EmbeddingModel embeddingModel;

    @InjectMocks
    CafeESClient cafeESClient;

    private CafeDocument sampleDocument() {
        return CafeDocument.builder()
                .id(1L)
                .title("CafeA")
                .imgUrl("img.jpg")
                .reviewCount(10)
                .rate(new BigDecimal("4.5"))
                .address(CafeDocument.Address.builder()
                        .street("street")
                        .zipCode("12345")
                        .location(new GeoPoint(37.5, 126.9))
                        .build())
                .keywords(List.of(CafeDocument.Keyword.builder().key("cozy").count(1).build()))
                .menus(Collections.emptyList())
                .build();
    }

    private SearchHits<CafeDocument> createHits(CafeDocument doc) {
        SearchHit<CafeDocument> hit = new SearchHit<>(
                "cafe", doc.getId().toString(), null, 1.0f,
                new Object[0], Map.of(), Map.of(), null, null, List.of(), doc);
        return new SearchHitsImpl<>(1L, TotalHitsRelation.EQUAL_TO, 1.0f, Duration.ZERO,
                null, null, List.of(hit), null, null, null);
    }

    @Test
    @DisplayName("keyword suggestion maps search results")
    void suggestTitleByKeyword() {
        CafeDocument doc = sampleDocument();
        when(operations.search(Mockito.any(NativeQuery.class), Mockito.eq(CafeDocument.class)))
                .thenReturn(createHits(doc));

        List<AutoCompleteResponse> result = cafeESClient.suggestTitleByKeyword("caf", 10);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo(doc.getTitle());
        verify(operations).search(Mockito.any(NativeQuery.class), Mockito.eq(CafeDocument.class));
    }

    @Test
    @DisplayName("embedding extraction returns float list")
    void getEmbedding() {
        float[] output = new float[]{0.1f, 0.2f};
        Embedding embedding = new Embedding(output, 0);
        EmbeddingResponse response = new EmbeddingResponse(List.of(embedding));
        when(embeddingModel.embedForResponse(any())).thenReturn(response);

        List<Float> list = cafeESClient.getEmbedding("hello");

        assertThat(list).containsExactly(0.1f, 0.2f);
    }

    @Test
    @DisplayName("recommend by location converts hits")
    void recommendByLocation() {
        CafeDocument doc = sampleDocument();
        when(operations.search(any(NativeQuery.class), eq(CafeDocument.class)))
                .thenReturn(createHits(doc));

        List<RecommendResponse> list = cafeESClient.recommendByLocation(37.5, 126.9, 5, 1);

        assertThat(list).hasSize(1);
        assertThat(list.get(0).getId()).isEqualTo(doc.getId());
    }

    @Test
    @DisplayName("recommend by keyword returns valid recommendations")
    void recommendByKeywordReturnsValidRecommendations() {
        CafeDocument doc = sampleDocument();
        when(operations.search(any(NativeQuery.class), eq(CafeDocument.class)))
                .thenReturn(createHits(doc));
        float[] embeddingOutput = new float[]{0.1f, 0.2f};
        Embedding embedding = new Embedding(embeddingOutput, 0);
        EmbeddingResponse response = new EmbeddingResponse(List.of(embedding));
        when(embeddingModel.embedForResponse(any())).thenReturn(response);

        List<RecommendResponse> result = cafeESClient.recommendByKeyword("cozy", 10);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(doc.getId());
        assertThat(result.get(0).getTitle()).isEqualTo(doc.getTitle());
        verify(operations).search(any(NativeQuery.class), eq(CafeDocument.class));
        verify(embeddingModel).embedForResponse(any());
    }

    @Test
    @DisplayName("recommend by hybrid converts hits")
    void recommendByHybrid() {
        // Given
        CafeDocument doc = sampleDocument();
        when(operations.search(any(NativeQuery.class), eq(CafeDocument.class)))
                .thenReturn(createHits(doc));
        float[] embeddingOutput = new float[]{0.1f, 0.2f};
        Embedding embedding = new Embedding(embeddingOutput, 0);
        EmbeddingResponse response = new EmbeddingResponse(List.of(embedding));
        when(embeddingModel.embedForResponse(any())).thenReturn(response);

        // When
        List<RecommendResponse> list = cafeESClient.recommendByHybrid("cozy", 37.5, 126.9, 10);

        // Then
        assertThat(list).hasSize(1);
        assertThat(list.get(0).getId()).isEqualTo(doc.getId());
        assertThat(list.get(0).getTitle()).isEqualTo(doc.getTitle());
        verify(operations).search(any(NativeQuery.class), eq(CafeDocument.class));
        verify(embeddingModel).embedForResponse(any());
    }


    @Test
    @DisplayName("search by title converts hits")
    void searchByTitle() {
        CafeDocument doc = sampleDocument();
        when(operations.search(any(NativeQuery.class), eq(CafeDocument.class)))
                .thenReturn(createHits(doc));

        List<SearchCafeResponse> list = cafeESClient.searchByTitle("cafe", 10);

        assertThat(list).hasSize(1);
        assertThat(list.get(0).getTitle()).isEqualTo(doc.getTitle());
    }
}