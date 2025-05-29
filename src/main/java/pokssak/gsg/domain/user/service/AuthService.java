package pokssak.gsg.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pokssak.gsg.common.exception.CustomException;
import pokssak.gsg.common.jwt.JwtTokenDto;
import pokssak.gsg.common.jwt.JwtTokenProvider;
import pokssak.gsg.domain.user.dto.ConflictEmailCheckRequestDto;
import pokssak.gsg.domain.user.dto.LoginRequestDto;
import pokssak.gsg.domain.user.dto.OAuthCodeDto;
import pokssak.gsg.domain.user.dto.OAuthSignUpRequestDto;
import pokssak.gsg.domain.user.dto.OAuthTokenResponseDto;
import pokssak.gsg.domain.user.dto.SignupRequestDto;
import pokssak.gsg.domain.user.dto.UserInfoResponseDto;
import pokssak.gsg.domain.user.entity.JoinType;
import pokssak.gsg.domain.user.entity.User;
import pokssak.gsg.domain.user.exception.UserErrorCode;
import pokssak.gsg.domain.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserKeywordService userKeywordService;
    private final OAuthPlatformService oAuthPlatformService;

    public void conflictEmailCheck(ConflictEmailCheckRequestDto conflictEmailCheckRequestDto) {
        if (userRepository.existsByEmail(conflictEmailCheckRequestDto.email())) {
            throw new CustomException(UserErrorCode.USER_EMAIL_ALREADY_EXIST);
        }
    }

    public JwtTokenDto localSignup(SignupRequestDto signupRequestDto) {

        if (userRepository.existsByEmail(signupRequestDto.email())) {
            throw new CustomException(UserErrorCode.USER_EMAIL_ALREADY_EXIST);
        }

        User user = User.builder()
            .password(passwordEncoder.encode(signupRequestDto.password()))
            .nickName(signupRequestDto.nickname())
            .email(signupRequestDto.email())
            .joinType(JoinType.LOCAL)
            .build();

        userRepository.save(user);

        userKeywordService.addUserKeywords(user.getId(), signupRequestDto.keywords().stream().toList());

        JwtTokenDto token = jwtTokenProvider.createToken(user.getId());

        // TODO : refresh token store in redis
        return token;
    }

    public JwtTokenDto localLogin(LoginRequestDto loginRequestDto) {
        User user = userRepository.findByEmail(loginRequestDto.email())
            .orElseThrow(() -> new CustomException(UserErrorCode.NOT_FOUND));

        if (!passwordEncoder.matches(loginRequestDto.password(), user.getPassword())) {
            throw new CustomException(UserErrorCode.INCORRECT_PASSWORD);
        }

        JwtTokenDto token = jwtTokenProvider.createToken(user.getId());

        // TODO : refresh token store in redis
        return token;
    }

    @Transactional
    public OAuthTokenResponseDto oAuthSignUp(OAuthCodeDto codeDto, JoinType oAuthPlatform) {
        String accessToken = oAuthPlatformService.getAccessToken(codeDto, oAuthPlatform);
        UserInfoResponseDto userInfo = oAuthPlatformService.getUserInfo(accessToken, oAuthPlatform);

        User user = oAuthPlatformService.getOAuthUser(userInfo.id(), oAuthPlatform);

        boolean isRegister = user.getNickName() != null; // false 일때 추가 정보 입력

        return new OAuthTokenResponseDto(isRegister, jwtTokenProvider.createToken(user.getId()));
    }

    @Transactional
    public void oAuthRegister(OAuthSignUpRequestDto oAuthSignUpRequestDto, User user) {
        User find = userRepository.findById(user.getId())
            .orElseThrow(() -> new CustomException(UserErrorCode.NOT_FOUND));

        userKeywordService.addUserKeywords(user.getId(), oAuthSignUpRequestDto.keywords().stream().toList());

        find.updateOAuthUser(oAuthSignUpRequestDto);
    }
}
