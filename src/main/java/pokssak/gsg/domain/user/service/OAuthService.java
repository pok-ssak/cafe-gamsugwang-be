package pokssak.gsg.domain.user.service;

import pokssak.gsg.domain.user.dto.OAuthCodeDto;
import pokssak.gsg.domain.user.dto.UserInfoResponseDto;
import pokssak.gsg.domain.user.entity.User;

public interface OAuthService {

    String getAccessToken(OAuthCodeDto oAuthCodeDto);

    UserInfoResponseDto getUserInfo(String oAuthAccessToken);

    User getOAuthUser(String oauthPlatformId);
}
