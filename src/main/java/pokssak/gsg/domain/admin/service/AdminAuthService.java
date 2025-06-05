package pokssak.gsg.domain.admin.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pokssak.gsg.common.exception.CustomException;
import pokssak.gsg.common.jwt.JwtTokenDto;
import pokssak.gsg.domain.admin.dto.AdminLoginRequest;
import pokssak.gsg.domain.admin.entity.Admin;
import pokssak.gsg.domain.admin.exception.AdminErrorCode;
import pokssak.gsg.domain.admin.repository.AdminRepository;
import pokssak.gsg.common.jwt.AdminJwtTokenProvider;

@Service
@RequiredArgsConstructor
public class AdminAuthService {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final AdminJwtTokenProvider jwtTokenProvider;

    public JwtTokenDto login(AdminLoginRequest request) {
        Admin admin = adminRepository.findByUsername(request.username())
                .orElseThrow(() -> new CustomException(AdminErrorCode.NOT_FOUND));

        if (!passwordEncoder.matches(request.password(), admin.getPassword())) {
            throw new CustomException(AdminErrorCode.INCORRECT_PASSWORD);
        }

        return jwtTokenProvider.createToken(admin.getId());
    }

    public void logout(String accessToken) {
        Long adminId = Long.parseLong(jwtTokenProvider.getClaims(accessToken).getSubject());

        jwtTokenProvider.deleteRefreshToken(adminId);
    }
}
