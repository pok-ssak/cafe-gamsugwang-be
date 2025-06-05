package pokssak.gsg.domain.bookmark.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import pokssak.gsg.domain.admin.entity.Admin;
import pokssak.gsg.domain.admin.repository.AdminRepository;
import pokssak.gsg.domain.admin.service.AdminDetailsService;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class AdminDetailsServiceTest {

    @InjectMocks
    AdminDetailsService adminDetailsService;

    @Mock
    AdminRepository adminRepository;

    @Test
    void testLoadUserByUsername() {
        String username = "Orange";

        Long id = 1L;

        Admin admin = Admin.builder()
                .id(1L)
                .username(username)
                .build();

        Mockito.when(adminRepository.findById(id)).thenReturn(Optional.of(admin));

        Admin find = adminDetailsService.loadUserByUsername(id.toString());

        assertThat(find.getUsername()).isEqualTo(username);
    }
}
