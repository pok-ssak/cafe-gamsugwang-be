package pokssak.gsg.domain.feed.entity;

import jakarta.persistence.*;
import lombok.*;
import pokssak.gsg.common.entity.BaseEntity;
import pokssak.gsg.domain.user.entity.User;

@Table(name = "feeds")
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Feed extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    private String content;

    private String url;

    @Enumerated(EnumType.STRING)
    private FeedType type;

    @Builder.Default
    private boolean isRead = false;
}
