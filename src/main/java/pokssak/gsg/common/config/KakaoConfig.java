package pokssak.gsg.common.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "oauth.kakao")
public class KakaoConfig {

    private String clientId;
    private String clientSecret;
    private String redirectUri;
    private String oAuthTokenUri;
    private String userInfoUri;
}
