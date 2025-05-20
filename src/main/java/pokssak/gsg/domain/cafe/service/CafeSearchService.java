package pokssak.gsg.domain.cafe.service;

import co.elastic.clients.elasticsearch._types.query_dsl.Operator;
import co.elastic.clients.elasticsearch.core.search.FieldCollapse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.stereotype.Service;
import pokssak.gsg.domain.cafe.entity.CafeDocument;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CafeSearchService {
    private final ElasticsearchOperations operations;

    /**
     * 카페 제목 자동완성
     * @param keyword
     * @param size
     * @return
     */
    public List<String> suggestTitleByKeyword(String keyword, int size) {
        NativeQuery query = NativeQuery.builder()
                // 필드 선택
                .withQuery(q -> q.match(m -> m
                        .field("title")
                        .query(keyword)
                        .operator(Operator.And)))
                // 페이지 크기
                .withPageable(PageRequest.of(0, size))

                // 정렬
                .withSort(Sort.by(Sort.Order.desc("_score")))
                .withSourceFilter(new FetchSourceFilter(new String[]{"title"}, null))
                .withFieldCollapse(FieldCollapse.of(fc -> fc.field("title.keyword")))
                .build();

        SearchHits<CafeDocument> hits = operations.search(query, CafeDocument.class);

        return hits.stream()
                .map(SearchHit::getContent)
                .map(CafeDocument::getTitle)
                .toList();
    }


}
