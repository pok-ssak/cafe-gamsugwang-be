package pokssak.gsg.domain.cafe.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;
import pokssak.gsg.common.entity.BaseEntity;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Table(name = "cafes")
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Builder
public class Cafe extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String info;

    @Column(length = 512)
    private String openTime;

    private BigDecimal rate;

    private Integer rateCount;

    @Column(columnDefinition = "TEXT")
    private String imageUrl;

    private String address;

    private String zipcode;

    @Column(precision = 18, scale = 15)
    private BigDecimal lat;
    @Column(precision = 18, scale = 15)
    private BigDecimal lon;

    private String phoneNumber;

    @Builder.Default
    @OneToMany(mappedBy = "cafe", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @BatchSize(size = 50)
    private Set<Menu> menuList = new HashSet<>();

    @Builder.Default
    @OneToMany(mappedBy = "cafe", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @BatchSize(size = 50)
    private Set<CafeKeyword> cafeKeywordList = new HashSet<>();


    public void addMenu(Menu menu) {
        menuList.add(menu);
        menu.updateCafe(this);
    }

    public void addKeyword(CafeKeyword cafeKeyword) {
        cafeKeywordList.add(cafeKeyword);
        cafeKeyword.updateCafe(this);
    }

    public void updateFromSuggestion(Suggestion.NewCafeData newCafeData, Set<CafeKeyword> cafeKeywords, Set<Menu> menus) {
        updateInfo(newCafeData);
        updateMenus(menus);
        updateKeywords(cafeKeywords);
    }

    private void updateInfo(Suggestion.NewCafeData data) {
        this.title = data.getTitle();
        this.info = data.getInfo();
        this.openTime = data.getOpenTime();
        this.imageUrl = data.getImageUrl();
        this.address = data.getAddress();
        this.zipcode = data.getZipcode();
        this.phoneNumber = data.getPhoneNumber();
    }

    public void updateMenus(Set<Menu> menus) {
        this.menuList.clear();
        for (Menu menu : menus) {
            menu.updateCafe(this);
            this.menuList.add(menu);
        }
    }

    public void updateKeywords(Set<CafeKeyword> cafeKeywords) {
        this.cafeKeywordList.clear();
        for (CafeKeyword cafeKeyword : cafeKeywords) {
            cafeKeyword.updateCafe(this);
            this.cafeKeywordList.add(cafeKeyword);
        }
    }

}
