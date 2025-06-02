package pokssak.gsg.domain.user.repository;


import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pokssak.gsg.domain.user.entity.JoinType;
import pokssak.gsg.domain.user.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.id = :userId")
    Optional<User> findByIdIncludeDeleted(@Param("userId") Long userId);

    Optional<User> findByOauthPlatformIdAndJoinType(String platformId, JoinType joinType);

    @Query("select u from User u left join fetch u.userKeywords where u.id = :userid")
    Optional<User> findByIdWithKeywords(Long userid);


}
