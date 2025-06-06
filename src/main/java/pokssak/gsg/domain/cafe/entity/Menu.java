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
@Table(name = "menus")
public class Menu extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    @Column(columnDefinition = "TEXT")
    private String menuImageUrl;
    private Integer price;
    private String modifier;

    @ManyToOne
    private Cafe cafe;

    public void updateCafe(Cafe cafe) {
        this.cafe = cafe;
        if (!cafe.getMenuList().contains(this)) {
            cafe.addMenu(this);
        }
    }
}
