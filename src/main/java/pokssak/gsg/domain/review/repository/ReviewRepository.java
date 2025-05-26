package pokssak.gsg.domain.review.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pokssak.gsg.domain.review.entity.Review;

import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    Page<Review> findByCafeId(Long cafeId, Pageable pageable);

    Optional<Review> findTopByUserIdAndCafeIdOrderByModifiedAtDesc(Long userId, Long cafeId);

//    @Query("""
//            SELECT r
//            FROM Review r
//            WHERE r.user.id = :userId AND r.cafe.id = :cafeId
//            ORDER BY r.modifiedAt DESC
//            """)
//    Optional<Review> findLatestByUserIdAndCafeId(@Param("userId") Long userId, @Param("cafeId")Long cafeId, Pageable pageable); //TODO : QueryDSL로
}
