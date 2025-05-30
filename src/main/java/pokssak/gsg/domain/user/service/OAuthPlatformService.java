package pokssak.gsg.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pokssak.gsg.common.exception.CustomException;
import pokssak.gsg.domain.user.dto.OAuthCodeDto;
import pokssak.gsg.domain.user.dto.UserInfoResponseDto;
import pokssak.gsg.domain.user.entity.JoinType;
import pokssak.gsg.domain.user.entity.User;
import pokssak.gsg.domain.user.exception.AuthErrorCode;

@Service
@RequiredArgsConstructor
public class OAuthPlatformService {

    private final KakaoService kakaoService;
    private final NaverService naverService;
    private final GoogleService googleService;

    public String getAccessToken(OAuthCodeDto codeDto, JoinType platform) {
        return getPlatformService(platform).getAccessToken(codeDto);
    }

    public UserInfoResponseDto getUserInfo(String accessToken, JoinType platform) {
        return getPlatformService(platform).getUserInfo(accessToken);
    }

    public User getOAuthUser(String platformId, JoinType platform) {
        return getPlatformService(platform).getOAuthUser(platformId);
    }

    private OAuthService getPlatformService(JoinType platform) {
        return switch (platform) {
            case KAKAO -> kakaoService;
            case NAVER -> naverService;
            case GOOGLE -> googleService;
            default -> throw new CustomException(AuthErrorCode.UNSUPPORTED_SOCIAL_LOGIN);
        };
    }
}
