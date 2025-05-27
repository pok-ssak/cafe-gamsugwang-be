package pokssak.gsg.domain.cafe.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pokssak.gsg.domain.cafe.entity.Cafe;
import pokssak.gsg.domain.cafe.entity.Keyword;
import pokssak.gsg.domain.cafe.entity.Menu;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SuggestRequest {
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
    private Set<Menu> menuList = new HashSet<>();
    private Set<Keyword> keywordList = new HashSet<>();


    public static Cafe toEntity(SuggestRequest request) {
        return Cafe.builder()
                .title(request.getTitle())
                .info(request.getInfo())
                .openTime(request.getOpenTime())
                .rate(request.getRate())
                .rateCount(request.getRateCount())
                .imageUrl(request.getImageUrl())
                .address(request.getAddress())
                .zipcode(request.getZipcode())
                .lat(request.getLat())
                .lon(request.getLon())
                .phoneNumber(request.getPhoneNumber())
                .menuList(request.getMenuList())
                .keywordList(request.getKeywordList())
                .build();
    }
}
