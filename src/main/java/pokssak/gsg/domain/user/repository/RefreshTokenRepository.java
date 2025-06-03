package pokssak.gsg.domain.user.repository;

import org.springframework.data.repository.CrudRepository;
import pokssak.gsg.domain.user.entity.RefreshToken;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {

}
