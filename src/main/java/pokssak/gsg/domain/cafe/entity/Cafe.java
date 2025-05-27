package pokssak.gsg.domain.cafe.entity;

import jakarta.persistence.*;
import lombok.*;
import pokssak.gsg.common.entity.BaseEntity;

import java.math.BigDecimal;
import java.util.List;

@Table(name = "cafes")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
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

    @OneToMany(mappedBy = "cafe", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Menu> menus;


}
