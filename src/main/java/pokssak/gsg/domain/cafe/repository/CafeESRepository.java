package pokssak.gsg.domain.cafe.repository;


import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import pokssak.gsg.domain.cafe.entity.CafeDocument;


public interface CafeESRepository extends ElasticsearchRepository<CafeDocument, Long> {
}
