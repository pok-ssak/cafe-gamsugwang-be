package pokssak.gsg.domain.cafe.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pokssak.gsg.domain.cafe.entity.Cafe;


@Repository
public interface CafeRepository extends JpaRepository<Cafe, Long> {
}
