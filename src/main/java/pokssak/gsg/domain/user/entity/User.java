package pokssak.gsg.domain.user.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import pokssak.gsg.common.entity.BaseEntity;
import pokssak.gsg.domain.bookmark.entity.Bookmark;
import pokssak.gsg.domain.review.entity.Review;
import pokssak.gsg.domain.review.entity.ReviewLike;
import pokssak.gsg.domain.user.dto.OAuthSignUpRequestDto;
import pokssak.gsg.domain.user.dto.UserUpdateRequest;

@Table(name = "users")
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity

@SQLDelete(sql = "UPDATE users SET is_deleted = true WHERE id = ?")
@SQLRestriction("is_deleted = false")
public class User extends BaseEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nickName;
    private String email;
    private String password;
    private String imageUrl;
    private String oauthPlatformId;

    @OneToMany(mappedBy = "user")
    private List<UserKeyword> userKeywords;

    @Builder.Default
    @OneToMany(mappedBy = "user")
    private List<Bookmark> bookmarks = new ArrayList<>();

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

    public void updateProfile(UserUpdateRequest userUpdateRequest) {
        this.nickName = userUpdateRequest.nickname();
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

    public void updateOAuthUser(OAuthSignUpRequestDto oAuthSignUpRequestDto) {
        this.nickName = oAuthSignUpRequestDto.nickname();
    }

    public void updateProfileImage(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
