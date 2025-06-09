package pokssak.gsg.domain.cafe.entity;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface SearchLogRepository extends ElasticsearchRepository<SearchLog, Long> {
}
