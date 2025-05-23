package pokssak.gsg.domain.review.entity;

import jakarta.persistence.*;
import lombok.*;
import pokssak.gsg.common.entity.BaseEntity;
import pokssak.gsg.domain.cafe.entity.Cafe;
import pokssak.gsg.domain.user.entity.User;

import java.util.ArrayList;
import java.util.List;

@Table(name = "reviews")
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Review extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Builder.Default
    private int rate = 0;

    private String content;

    @Builder.Default
    private String imageUrl = "";

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    //, foreignKey = @ForeignKey(NO_CONSTRAINT)
    @JoinColumn(name = "cafe_id", nullable = false)
    private Cafe cafe;

    @Builder.Default
    @OneToMany(mappedBy = "review")
    private List<ReviewLike> likedUser = new ArrayList<>();

    @Builder.Default
    private Long likeCount = 0L;
}
