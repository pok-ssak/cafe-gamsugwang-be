package pokssak.gsg.domain.cafe.entity;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;
import pokssak.gsg.domain.cafe.dto.KeywordDto;
import pokssak.gsg.domain.cafe.dto.MenuDto;

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
        private Set<MenuDto> menuList;
        private Set<KeywordDto> keywordList;
    }
}
