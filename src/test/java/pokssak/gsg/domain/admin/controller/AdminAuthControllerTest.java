package pokssak.gsg.domain.admin.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import pokssak.gsg.common.jwt.JwtTokenDto;
import pokssak.gsg.domain.admin.dto.AdminLoginRequest;
import pokssak.gsg.domain.admin.service.AdminAuthService;

@ExtendWith(MockitoExtension.class)
class AdminAuthControllerTest {

    @InjectMocks
    private AdminAuthController adminAuthController;

    @Mock
    private AdminAuthService adminAuthService;

    @Test
    void login_should_return_access_token() {
        // given
        AdminLoginRequest request = new AdminLoginRequest("admin", "password");
        JwtTokenDto tokenDto = JwtTokenDto.of("access-token-123", "refresh-token-abc");
        when(adminAuthService.login(request)).thenReturn(tokenDto);

        // when
        ResponseEntity<String> response = adminAuthController.login(request);

        // then
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("access-token-123", response.getBody());
        verify(adminAuthService).login(request);
    }

    @Test
    void logout_should_call_service_and_return_no_content() {
        // given
        String bearerToken = "Bearer some.jwt.token";

        // when
        ResponseEntity<Void> response = adminAuthController.logout(bearerToken);

        // then
        verify(adminAuthService).logout("some.jwt.token");
        assertEquals(204, response.getStatusCodeValue());
        assertNull(response.getBody());
    }
}
