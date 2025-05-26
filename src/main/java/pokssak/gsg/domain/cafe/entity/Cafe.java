package pokssak.gsg.domain.cafe.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pokssak.gsg.common.entity.BaseEntity;

import java.math.BigDecimal;

@Table(name = "cafes")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Cafe extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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


}
