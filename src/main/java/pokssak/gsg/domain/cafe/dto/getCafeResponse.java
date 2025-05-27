package pokssak.gsg.domain.cafe.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import pokssak.gsg.domain.cafe.entity.Cafe;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class getCafeResponse {
    private Long Id;
    private String title;
    private String info;
    private String openTime;
    private BigDecimal rate;
    private Integer rateCount;
    private String imgUrl;
    private String address;
    private String zipcode;
    private BigDecimal x;
    private BigDecimal y;
    private String phone_number;


    public static getCafeResponse from(Cafe cafe) {
        return getCafeResponse.builder()
                .Id(cafe.getId())
                .title(cafe.getTitle())
                .info(cafe.getInfo())
                .openTime(cafe.getOpenTime())
                .rate(cafe.getRate())
                .rateCount(cafe.getRateCount())
                .imgUrl(cafe.getImageUrl())
                .address(cafe.getAddress())
                .zipcode(cafe.getZipcode())
                .x(cafe.getLat())
                .y(cafe.getLon())
                .phone_number(cafe.getPhoneNumber())
                .build();
    }
}
