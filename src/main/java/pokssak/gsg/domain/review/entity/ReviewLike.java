package pokssak.gsg.domain.review.entity;

import jakarta.persistence.*;
import lombok.*;
import pokssak.gsg.common.entity.BaseEntity;
import pokssak.gsg.domain.user.entity.User;

@Table(name = "review_likes")
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class ReviewLike extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    @ManyToOne
    private Review review;
}
