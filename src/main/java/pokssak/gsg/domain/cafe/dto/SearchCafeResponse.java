package pokssak.gsg.domain.cafe.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pokssak.gsg.domain.cafe.entity.CafeDocument;

import java.math.BigDecimal;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SearchCafeResponse {
    private Long id;
    private String title;
    private BigDecimal rate;
    private Integer reviewCount;
    private String imageUrl;
    private String address;
    private String zipcode;
    private Double lat;
    private Double lon;
    private List<KeywordDTO> keywordList;
    private Boolean isBookmarked;

    public static SearchCafeResponse from(CafeDocument cafeDocument) {
        return SearchCafeResponse.builder()
                .id(cafeDocument.getId())
                .title(cafeDocument.getTitle())
                .rate(cafeDocument.getRate())
                .reviewCount(cafeDocument.getReviewCount())
                .imageUrl(cafeDocument.getImgUrl())
                .address(cafeDocument.getAddress().getStreet())
                .zipcode(cafeDocument.getAddress().getZipCode())
                .lat(cafeDocument.getAddress().getLocation().getLat())
                .lon(cafeDocument.getAddress().getLocation().getLon())
                .keywordList(cafeDocument.getKeywords().stream()
                        .map(keyword -> new KeywordDTO(keyword.getKey()))
                        .toList())
                .isBookmarked(false)
                .build();
    }

    public static SearchCafeResponse from(CafeDocument cafeDocument, Boolean isBookmarked) {
        return SearchCafeResponse.builder()
                .id(cafeDocument.getId())
                .title(cafeDocument.getTitle())
                .rate(cafeDocument.getRate())
                .reviewCount(cafeDocument.getReviewCount())
                .imageUrl(cafeDocument.getImgUrl())
                .address(cafeDocument.getAddress().getStreet())
                .zipcode(cafeDocument.getAddress().getZipCode())
                .lat(cafeDocument.getAddress().getLocation().getLat())
                .lon(cafeDocument.getAddress().getLocation().getLon())
                .keywordList(cafeDocument.getKeywords().stream()
                        .map(keyword -> new KeywordDTO(keyword.getKey()))
                        .toList())
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
