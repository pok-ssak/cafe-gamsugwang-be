package pokssak.gsg.domain.user.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.Collection;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import pokssak.gsg.common.entity.BaseEntity;
import pokssak.gsg.common.exception.CustomException;
import pokssak.gsg.domain.bookmark.entity.Bookmark;
import pokssak.gsg.domain.review.entity.Review;
import pokssak.gsg.domain.user.exception.UserErrorCode;

@Table(name = "users")
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class User extends BaseEntity implements UserDetails {

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
    @OneToMany
    private List<Review> reviews;

    @Enumerated(EnumType.STRING)
    private JoinType joinType;

    public void validatePassword(String password) {
        if (!password.equals(this.password)) {
            throw new CustomException(UserErrorCode.INCORRECT_PASSWORD);
        }
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getUsername() {
        return email;
    }
}
