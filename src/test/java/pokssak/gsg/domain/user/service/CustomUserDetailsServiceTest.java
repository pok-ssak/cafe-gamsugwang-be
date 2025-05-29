package pokssak.gsg.domain.user.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import pokssak.gsg.domain.user.entity.User;
import pokssak.gsg.domain.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @InjectMocks
    CustomUserDetailsService customUserDetailsService;

    @Mock
    UserRepository userRepository;

    @Test
    void testLoadUserByUsername() {
        String email = "example@google.com";

        Long id = 1L;

        User user = User.builder()
            .id(1L)
            .email(email)
            .build();

        Mockito.when(userRepository.findById(id)).thenReturn(Optional.of(user));

        User find = customUserDetailsService.loadUserByUsername(id.toString());

        assertThat(find.getUsername()).isEqualTo(email);
    }

}