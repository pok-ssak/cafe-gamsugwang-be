package pokssak.gsg.domain.review.entity;

import jakarta.persistence.*;
import lombok.*;
import pokssak.gsg.common.entity.BaseEntity;

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
}
