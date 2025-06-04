package pokssak.gsg.domain.cafe.repository;

import org.springframework.data.repository.CrudRepository;
import pokssak.gsg.domain.cafe.entity.Suggestion;

import java.util.List;

public interface SuggestionRedisRepository extends CrudRepository<Suggestion, Long> {
    List<Suggestion> findAll();
}
