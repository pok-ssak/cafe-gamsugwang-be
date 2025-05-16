package pokssak.gsg.domain.user.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pokssak.gsg.domain.user.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

}
