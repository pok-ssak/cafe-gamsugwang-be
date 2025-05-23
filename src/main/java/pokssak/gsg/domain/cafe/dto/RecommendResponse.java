package pokssak.gsg.domain.cafe.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import pokssak.gsg.domain.cafe.entity.CafeDocument;

import java.util.List;


@Getter
@Setter
@Builder
public class RecommendResponse {
    private String title;
    private String imgUrl;
    private String address;
    private Integer reviewCount;
    private Double rate;
    private List<String> keywords;



    public static RecommendResponse from(CafeDocument cafeDocument) {
        return RecommendResponse.builder()
                .title(cafeDocument.getTitle())
                .imgUrl(cafeDocument.getImgUrl())
                .address(cafeDocument.getAddress().getStreet())
                .reviewCount(cafeDocument.getReviewCount())
                .rate(cafeDocument.getRate())
                .keywords(cafeDocument.getKeywords().stream().map(CafeDocument.Keyword::getKey).toList())
                .build();
    }
}
