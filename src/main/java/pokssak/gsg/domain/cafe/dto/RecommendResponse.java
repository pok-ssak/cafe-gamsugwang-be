package pokssak.gsg.domain.cafe.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import pokssak.gsg.domain.cafe.entity.CafeDocument;

import java.math.BigDecimal;
import java.util.List;


@Getter
@Setter
@Builder
public class RecommendResponse {
    private Long id;
    private String title;
    private String imageUrl;
    private String address;
    private Integer reviewCount;
    private BigDecimal rate;
    private Double lat;
    private Double lon;
    private List<KeywordDTO> keywordList;
    private Boolean isBookmarked = false;

    public static RecommendResponse from(CafeDocument cafeDocument) {
        return RecommendResponse.builder()
                .id(cafeDocument.getId())
                .title(cafeDocument.getTitle())
                .imageUrl(cafeDocument.getImgUrl())
                .address(cafeDocument.getAddress().getStreet())
                .reviewCount(cafeDocument.getReviewCount())
                .rate(cafeDocument.getRate())
                .keywordList(cafeDocument.getKeywords().stream()
                        .map(keyword -> new KeywordDTO(keyword.getKey()))
                        .toList())
                .lat(cafeDocument.getAddress().getLocation().getLat())
                .lon(cafeDocument.getAddress().getLocation().getLon())
                .isBookmarked(false)
                .build();
    }

    public static RecommendResponse from(CafeDocument cafeDocument, Boolean isBookmarked) {
        return RecommendResponse.builder()
                .id(cafeDocument.getId())
                .title(cafeDocument.getTitle())
                .imageUrl(cafeDocument.getImgUrl())
                .address(cafeDocument.getAddress().getStreet())
                .reviewCount(cafeDocument.getReviewCount())
                .rate(cafeDocument.getRate())
                .keywordList(cafeDocument.getKeywords().stream()
                        .map(keyword -> new KeywordDTO(keyword.getKey()))
                        .toList())
                .lat(cafeDocument.getAddress().getLocation().getLat())
                .lon(cafeDocument.getAddress().getLocation().getLon())
                .isBookmarked(isBookmarked)
                .build();
    }
    @Getter
    public static class KeywordDTO {
        private String keyword;

        public KeywordDTO(String keyword) {
            this.keyword = keyword;
        }
    }

}
