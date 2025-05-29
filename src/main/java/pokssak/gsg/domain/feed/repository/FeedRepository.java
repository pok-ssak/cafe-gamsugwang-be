package pokssak.gsg.domain.feed.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pokssak.gsg.domain.feed.entity.Feed;

public interface FeedRepository extends JpaRepository<Feed, Long> {

    @EntityGraph(attributePaths = "user")
    Page<Feed> findByUserId(Long userId, Pageable pageable);

    long countByUserIdAndIsReadFalse(Long userId);

    @Modifying
    @Query("UPDATE Feed f SET f.isRead = true WHERE f.id = :feedId")
    void markAsReadById(@Param("feedId") Long feedId);

    @Modifying
    @Query("UPDATE Feed f SET f.isRead = true WHERE f.user.id = :userId AND f.isRead = false")
    void markAllAsReadByUserId(@Param("userId") Long userId);

}
