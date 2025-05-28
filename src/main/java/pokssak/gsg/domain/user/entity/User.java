package pokssak.gsg.domain.user.entity;

import java.util.*;

import org.hibernate.annotations.SQLRestriction;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
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

@SQLDelete(sql = "UPDATE users SET is_deleted = true WHERE id = ?")
@SQLRestriction("is_deleted = false")
public class User extends BaseEntity implements UserDetails{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nickName;
    private String email;
    private String password;
    private String imageUrl;

    @OneToMany
    private List<UserKeyword> userKeywords;
    @OneToMany
    private List<Bookmark> bookmarks;
    @OneToMany(mappedBy = "user")
    private List<Review> reviews;

    @Builder.Default
    @OneToMany(mappedBy = "user")
    private List<ReviewLike> likedReviews = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private JoinType joinType;


    @Builder.Default
    private Boolean isDeleted = false;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getUsername() {
        return email;
    }

    public void updateProfile(String nickName, String imageUrl) {
        this.nickName = nickName;
        this.imageUrl = imageUrl;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void restore() {
        this.isDeleted = false;
    }

    public boolean hasLiked(Long reviewId) {
        return likedReviews.stream().anyMatch(reviewLike -> reviewLike.getReview().getId().equals(reviewId));
    }

    public void updateProfileImage(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
