package pokssak.gsg.domain.cafe.entity;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

import java.time.LocalDateTime;

@RedisHash(value = "suggestions", timeToLive = 604800) // 7 days
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Suggestion {
    @Id
    private Long id;
    private Long userId;
    private Cafe oldCafe;
    private Cafe cafe;
    private LocalDateTime createdAt;
}
