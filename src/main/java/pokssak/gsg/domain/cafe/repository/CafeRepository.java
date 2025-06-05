package pokssak.gsg.domain.cafe.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pokssak.gsg.domain.cafe.entity.Cafe;

import javax.swing.text.html.Option;
import java.util.Optional;


@Repository
public interface CafeRepository extends JpaRepository<Cafe, Long> {
    @Query("select c from Cafe c join fetch c.menuList m join fetch c.cafeKeywordList k where c.id = :cafeId")
    Optional<Cafe> findByIdWithMenusAndKeywords(Long cafeId);
}
