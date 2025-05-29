package pokssak.gsg.domain.cafe.repository;

import org.springframework.data.repository.CrudRepository;
import pokssak.gsg.domain.cafe.entity.Suggestion;

public interface SuggestionRedisRepository extends CrudRepository<Suggestion, Long> {
}
