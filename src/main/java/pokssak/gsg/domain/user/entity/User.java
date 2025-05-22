package pokssak.gsg.domain.user.entity;


import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import pokssak.gsg.common.entity.BaseEntity;
import pokssak.gsg.common.exception.CustomException;
import pokssak.gsg.domain.bookmark.entity.Bookmark;
import pokssak.gsg.domain.review.entity.Review;
import pokssak.gsg.domain.user.exception.UserErrorCode;

import java.util.List;

@Table(name = "users")
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity

@SQLDelete(sql = "UPDATE users SET is_deleted = true WHERE id = ?")
@Where(clause = "is_deleted = false")
public class User extends BaseEntity {

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

    private boolean isDeleted = false;

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
}
