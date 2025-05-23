package pokssak.gsg.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pokssak.gsg.domain.user.entity.User;
import pokssak.gsg.domain.user.repository.UserRepository;

@RequiredArgsConstructor
@Service
public class UserService{

    private final UserRepository userRepository;

    public User getUserById(Long userId) {
        return userRepository.findById(userId).get();
    }

}
