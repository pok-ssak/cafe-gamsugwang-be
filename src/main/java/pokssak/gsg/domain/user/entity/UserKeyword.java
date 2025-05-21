package pokssak.gsg.domain.user.entity;

import jakarta.persistence.*;
import lombok.*;
import pokssak.gsg.common.entity.BaseEntity;
import pokssak.gsg.common.vo.Keyword;

@Table(name = "user_keywords")
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class UserKeyword extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @Embedded
    private Keyword keyword;
}
