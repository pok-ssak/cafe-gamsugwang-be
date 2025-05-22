package pokssak.gsg.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pokssak.gsg.common.exception.CustomException;
import pokssak.gsg.common.jwt.JwtTokenDto;
import pokssak.gsg.common.jwt.JwtTokenProvider;
import pokssak.gsg.domain.user.dto.LoginRequestDto;
import pokssak.gsg.domain.user.dto.SignupRequestDto;
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

        JwtTokenDto token = jwtTokenProvider.createToken(user.getEmail());

        // TODO : refresh token store in redis
        return token;
    }

    public JwtTokenDto localLogin(LoginRequestDto loginRequestDto) {
        User user = userRepository.findByEmail(loginRequestDto.email())
            .orElseThrow(() -> new CustomException(UserErrorCode.NOT_FOUND));

        if (!passwordEncoder.matches(loginRequestDto.password(), user.getPassword())) {
            throw new CustomException(UserErrorCode.INCORRECT_PASSWORD);
        }
        
        JwtTokenDto token = jwtTokenProvider.createToken(user.getEmail());

        // TODO : refresh token store in redis
        return token;
    }
}
