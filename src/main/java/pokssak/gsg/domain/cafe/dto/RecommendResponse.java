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
    private List<String> keywords;
    private Double lat;
    private Double lon;

    public static RecommendResponse from(CafeDocument cafeDocument) {
        return RecommendResponse.builder()
                .id(cafeDocument.getId())
                .title(cafeDocument.getTitle())
                .imageUrl(cafeDocument.getImgUrl())
                .address(cafeDocument.getAddress().getStreet())
                .reviewCount(cafeDocument.getReviewCount())
                .rate(cafeDocument.getRate())
                .keywords(cafeDocument.getKeywords().stream().map(CafeDocument.Keyword::getKey).toList())
                .lat(cafeDocument.getAddress().getLocation().getLat())
                .lon(cafeDocument.getAddress().getLocation().getLon())
                .build();
    }
}
