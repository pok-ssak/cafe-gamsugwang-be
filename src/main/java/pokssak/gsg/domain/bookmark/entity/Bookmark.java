package pokssak.gsg.domain.bookmark.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pokssak.gsg.common.entity.BaseEntity;
import pokssak.gsg.domain.user.entity.User;
import pokssak.gsg.domain.cafe.entity.Cafe;

@Table(name = "bookmarks")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Bookmark extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    private Cafe cafe;
}
