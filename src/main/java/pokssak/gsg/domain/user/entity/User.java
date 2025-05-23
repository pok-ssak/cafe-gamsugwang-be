package pokssak.gsg.domain.user.entity;

import jakarta.persistence.*;
import lombok.*;
import pokssak.gsg.common.entity.BaseEntity;
import pokssak.gsg.domain.bookmark.entity.Bookmark;
import pokssak.gsg.domain.review.entity.Review;
import pokssak.gsg.domain.review.entity.ReviewLike;

import java.util.ArrayList;
import java.util.List;

@Table(name = "users")
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nickName;
    private String email;
    private String password;
    private String imageUrl;

//    @OneToMany
//    private List<UserKeyword> userKeywords;
//    @OneToMany
//    private List<Bookmark> bookmarks;
    @OneToMany(mappedBy = "user")
    private List<Review> reviews;

    @Builder.Default
    @OneToMany(mappedBy = "user")
    private List<ReviewLike> likedReviews = new ArrayList<>();

    @Enumerated
    private JoinType joinType;

    public boolean hasLiked(Long reviewId) {
        return likedReviews.stream().anyMatch(reviewLike -> reviewLike.getReview().getId().equals(reviewId));
    }
}
