package pokssak.gsg.domain.admin.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import java.util.Optional;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import pokssak.gsg.common.exception.CustomException;
import pokssak.gsg.common.jwt.AdminJwtTokenProvider;
import pokssak.gsg.common.jwt.JwtTokenDto;
import pokssak.gsg.domain.admin.dto.AdminLoginRequest;
import pokssak.gsg.domain.admin.entity.Admin;
import pokssak.gsg.domain.admin.exception.AdminErrorCode;
import pokssak.gsg.domain.admin.repository.AdminRepository;

@ExtendWith(MockitoExtension.class)
class AdminAuthServiceTest {

    @InjectMocks
    AdminAuthService adminAuthService;

    @Mock
    AdminRepository adminRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    AdminJwtTokenProvider jwtTokenProvider;

    @Test
    void login_Success() {
        Admin admin = Admin.builder()
                .id(1L)
                .username("admin")
                .password("encodedPassword")
                .build();

        AdminLoginRequest request = new AdminLoginRequest("admin", "rawPassword");

        when(adminRepository.findByUsername("admin")).thenReturn(Optional.of(admin));
        when(passwordEncoder.matches("rawPassword", "encodedPassword")).thenReturn(true);
        when(jwtTokenProvider.createToken(admin.getId()))
                .thenReturn(new JwtTokenDto("accessToken", "refreshToken"));

        JwtTokenDto result = adminAuthService.login(request);

        assertThat(result.accessToken()).isEqualTo("accessToken");
        assertThat(result.refreshToken()).isEqualTo("refreshToken");

        verify(adminRepository).findByUsername("admin");
        verify(passwordEncoder).matches("rawPassword", "encodedPassword");
        verify(jwtTokenProvider).createToken(1L);
    }

    @Test
    void loginFail_AdminNotFound() {
        AdminLoginRequest request = new AdminLoginRequest("admin", "rawPassword");
        when(adminRepository.findByUsername("admin")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> adminAuthService.login(request))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", AdminErrorCode.NOT_FOUND);
    }

    @Test
    void loginFail_IncorrectPassword() {
        Admin admin = Admin.builder()
                .id(1L)
                .username("admin")
                .password("encodedPassword")
                .build();

        AdminLoginRequest request = new AdminLoginRequest("admin", "wrongPassword");

        when(adminRepository.findByUsername("admin")).thenReturn(Optional.of(admin));
        when(passwordEncoder.matches("wrongPassword", "encodedPassword")).thenReturn(false);

        assertThatThrownBy(() -> adminAuthService.login(request))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", AdminErrorCode.INCORRECT_PASSWORD);
    }

    @Test
    void logout_Success() {
        String accessToken = "accessToken";
        Claims claims = mock(Claims.class);

        when(jwtTokenProvider.getClaims(accessToken)).thenReturn(claims);
        when(claims.getSubject()).thenReturn("1");

        adminAuthService.logout(accessToken);

        verify(jwtTokenProvider).deleteRefreshToken(1L);
    }
}
