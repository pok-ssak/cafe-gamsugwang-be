package pokssak.gsg.domain.cafe.repository;


import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import pokssak.gsg.domain.cafe.entity.CafeDocument;
import pokssak.gsg.domain.cafe.entity.KeywordDocument;


public interface KeywordESRepository extends ElasticsearchRepository<KeywordDocument, Long> {
}
