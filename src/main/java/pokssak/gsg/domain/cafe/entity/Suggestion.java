package pokssak.gsg.domain.cafe.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

import java.time.LocalDateTime;
import java.util.Set;

@RedisHash(value = "suggestions", timeToLive = 604800) // 7 days
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Suggestion {
    @Id
    private Long id;
    private Long userId;
    private Long oldCafeId;
    @JsonIgnore
    private NewCafeData newCafe;
    private LocalDateTime createdAt;


    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class NewCafeData {
        private String title;
        private String info;
        private String openTime;
        private String imageUrl;
        private String address;
        private String zipcode;
        private String phoneNumber;
        private Set<Menu> menuList;
        private Set<Keyword> keywordList;
    }
}
