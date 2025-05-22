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
        User user = User.builder()
            .email(email)
            .build();

        Mockito.when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        User find = customUserDetailsService.loadUserByUsername(email);

        assertThat(find.getUsername()).isEqualTo(email);
    }

}