package pokssak.gsg.domain.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import pokssak.gsg.common.exception.CustomException;
import pokssak.gsg.common.jwt.JwtTokenDto;
import pokssak.gsg.common.jwt.JwtTokenProvider;
import pokssak.gsg.domain.user.dto.ConflictEmailCheckRequestDto;
import pokssak.gsg.domain.user.dto.LoginRequestDto;
import pokssak.gsg.domain.user.dto.SignupRequestDto;
import pokssak.gsg.domain.user.entity.User;
import pokssak.gsg.domain.user.exception.UserErrorCode;
import pokssak.gsg.domain.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {


    @InjectMocks
    AuthService authService;

    @Mock
    UserRepository userRepository;

    @Mock
    JwtTokenProvider jwtTokenProvider;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    UserKeywordService userKeywordService;


    @Test
    void checkEmailTest() {
        String email = "example@google.com";
        when(userRepository.existsByEmail(email)).thenReturn(true);

        assertThatThrownBy(() -> authService.conflictEmailCheck(new ConflictEmailCheckRequestDto(email)))
            .isInstanceOf(CustomException.class)
            .hasFieldOrPropertyWithValue("errorCode", UserErrorCode.USER_EMAIL_ALREADY_EXIST);
    }

    @Test
    void localSignupTest() {
        String email = "example@google.com";
        when(userRepository.existsByEmail(email)).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("password");
        when(jwtTokenProvider.createToken(any())).thenReturn(new JwtTokenDto("accessToken", "refreshToken"));

        SignupRequestDto signupRequestDto = new SignupRequestDto(email, "password", "nickname", new HashSet<>());
        JwtTokenDto jwtTokenDto = authService.localSignup(signupRequestDto);

        verify(userRepository, times(1)).save(any());
        assertThat(jwtTokenDto.accessToken()).isNotNull();
        assertThat(jwtTokenDto.refreshToken()).isNotNull();
    }

    @Test
    void localLoginTest() {
        User user = User.builder()
            .id(1L)
            .email("example@google.com")
            .password("password")
            .build();

        String email = "example@google.com";

        LoginRequestDto loginRequestDto = new LoginRequestDto(email, "password");

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(loginRequestDto.password(), user.getPassword())).thenReturn(true);
        when(jwtTokenProvider.createToken(user.getId())).thenReturn(new JwtTokenDto("accessToken", "refreshToken"));

        JwtTokenDto jwtTokenDto = authService.localLogin(loginRequestDto);
        assertThat(jwtTokenDto.accessToken()).isNotNull();
        assertThat(jwtTokenDto.refreshToken()).isNotNull();
    }
}