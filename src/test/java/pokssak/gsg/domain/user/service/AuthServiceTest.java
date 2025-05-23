package pokssak.gsg.domain.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import pokssak.gsg.common.jwt.JwtTokenDto;
import pokssak.gsg.common.jwt.JwtTokenProvider;
import pokssak.gsg.domain.user.dto.LoginRequestDto;
import pokssak.gsg.domain.user.dto.SignupRequestDto;
import pokssak.gsg.domain.user.entity.User;
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


    @Test
    void localSignupTest() {
        String email = "example@google.com";
        when(userRepository.existsByEmail(email)).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("password");
        when(jwtTokenProvider.createToken(email)).thenReturn(new JwtTokenDto("accessToken", "refreshToken"));

        SignupRequestDto signupRequestDto = new SignupRequestDto(email, "password", "nickname");
        JwtTokenDto jwtTokenDto = authService.localSignup(signupRequestDto);

        verify(userRepository, times(1)).save(any());
        assertThat(jwtTokenDto.accessToken()).isNotNull();
        assertThat(jwtTokenDto.refreshToken()).isNotNull();
    }

    @Test
    void localLoginTest() {
        User user = User.builder()
            .email("example@google.com")
            .password("password")
            .build();

        String email = "example@google.com";

        LoginRequestDto loginRequestDto = new LoginRequestDto(email, "password");

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(loginRequestDto.password(), user.getPassword())).thenReturn(true);
        when(jwtTokenProvider.createToken(email)).thenReturn(new JwtTokenDto("accessToken", "refreshToken"));

        JwtTokenDto jwtTokenDto = authService.localLogin(loginRequestDto);
        assertThat(jwtTokenDto.accessToken()).isNotNull();
        assertThat(jwtTokenDto.refreshToken()).isNotNull();
    }

}