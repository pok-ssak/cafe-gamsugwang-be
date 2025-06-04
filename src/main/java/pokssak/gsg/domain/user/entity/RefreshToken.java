package pokssak.gsg.domain.user.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter
@AllArgsConstructor
@RedisHash(value = "refresh", timeToLive = 14440)
public class RefreshToken {

    @Id
    private String refreshToken;

    private Long userId;


    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
