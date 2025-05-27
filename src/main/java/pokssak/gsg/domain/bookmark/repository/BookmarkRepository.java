package pokssak.gsg.domain.bookmark.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pokssak.gsg.domain.bookmark.entity.Bookmark;
import pokssak.gsg.domain.cafe.entity.Cafe;
import pokssak.gsg.domain.user.entity.User;

import java.util.List;

@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
    List<Bookmark> findAllByUser(User user);
    boolean existsByUserAndCafe(User user, Cafe cafe);
    void deleteByUserAndCafe(User user, Cafe cafe);
}
