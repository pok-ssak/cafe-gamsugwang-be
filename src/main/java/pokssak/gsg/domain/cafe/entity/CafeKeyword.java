package pokssak.gsg.domain.cafe.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pokssak.gsg.common.entity.BaseEntity;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "keywords")
public class CafeKeyword extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String keyword;
    private Integer count;

    @ManyToOne
    private Cafe cafe;

    public void updateCafe(Cafe cafe) {
        this.cafe = cafe;
        if (!cafe.getCafeKeywordList().contains(this)) {
            cafe.addKeyword(this);
        }
    }
}
