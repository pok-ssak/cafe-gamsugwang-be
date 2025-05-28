package pokssak.gsg.domain.cafe.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import pokssak.gsg.domain.cafe.entity.Cafe;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Builder
public class GetCafeResponse {
    private Long Id;
    private String title;
    private String info;
    private String openTime;
    private BigDecimal rate;
    private Integer rateCount;
    private String imageUrl;
    private String address;
    private String zipcode;
    private BigDecimal lat;
    private BigDecimal lon;
    private String phoneNumber;
    private List<MenuResponse> menuList;
    private List<KeywordResponse> keywordList;


    public static GetCafeResponse from(Cafe cafe) {
        return GetCafeResponse.builder()
                .Id(cafe.getId())
                .title(cafe.getTitle())
                .info(cafe.getInfo())
                .openTime(cafe.getOpenTime())
                .rate(cafe.getRate())
                .rateCount(cafe.getRateCount())
                .imageUrl(cafe.getImageUrl())
                .address(cafe.getAddress())
                .zipcode(cafe.getZipcode())
                .lat(cafe.getLat())
                .lon(cafe.getLon())
                .phoneNumber(cafe.getPhoneNumber())
                .menuList(cafe.getMenuList().stream()
                        .map(MenuResponse::from)
                        .toList())
                .keywordList(cafe.getKeywordList().stream()
                        .map(KeywordResponse::from)
                        .toList())
                .build();
    }
}
