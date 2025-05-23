package pokssak.gsg.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pokssak.gsg.domain.user.entity.User;
import pokssak.gsg.domain.user.entity.UserKeyword;

import java.util.List;

@Repository
public interface UserKeywordRepository extends JpaRepository<UserKeyword, Long> {

    List<UserKeyword> findByUserId(Long userId);

    @Modifying
    @Query("DELETE FROM UserKeyword uk WHERE uk.user = :user")
    void deleteByUser(@Param("user") User user);

}
