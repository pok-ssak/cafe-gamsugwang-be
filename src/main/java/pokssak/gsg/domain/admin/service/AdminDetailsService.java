package pokssak.gsg.domain.admin.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pokssak.gsg.common.exception.CustomException;
import pokssak.gsg.domain.admin.entity.Admin;
import pokssak.gsg.domain.admin.exception.AdminErrorCode;
import pokssak.gsg.domain.admin.repository.AdminRepository;

@Service
@RequiredArgsConstructor
public class AdminDetailsService implements UserDetailsService {

    private final AdminRepository adminRepository;

    @Override
    public Admin loadUserByUsername(String username) throws UsernameNotFoundException {
        return adminRepository.findById(Long.valueOf(username))
                .orElseThrow(() -> new CustomException(AdminErrorCode.NOT_FOUND));
    }
}
