package pokssak.gsg.domain.cafe.service;

import co.elastic.clients.elasticsearch._types.aggregations.Aggregation;
import co.elastic.clients.elasticsearch._types.aggregations.TermsAggregation;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import co.elastic.clients.json.JsonData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.SearchTemplateQuery;
import org.springframework.data.elasticsearch.core.query.StringQuery;
import org.springframework.stereotype.Service;
import pokssak.gsg.domain.cafe.entity.SearchLog;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SearchLogESClient {
    private final ElasticsearchOperations operations;

    public List<String> getTopSearches(int topN) {
        String jsonDsl = """
{
  "size": 0,
  "query": {
    "range": {
      "timestamp": { "gte": "now-24h" }
    }
  },
  "aggs": {
    "top_queries": {
      "terms": {
        "field": "query",
        "size": %d,
        "order": { "_count": "desc" }
      }
    }
  }
}
""".formatted(topN);

// 2) SearchTemplateQuery 생성 (원본 JSON을 source로)
        SearchTemplateQuery query = SearchTemplateQuery.builder()
                .withSource(jsonDsl)    // 전체 바디를 inline source 로 전달 :contentReference[oaicite:2]{index=2}
                .build();


        // 2.1) 쿼리 실행
        //SearchHits<SearchLog> hits = operations.search(query, SearchLog.class);
        // 2.2) 결과에서 aggregation 추출
        // 4) aggregation 결과 추출

        List<String> result = List.of(
                "커피",
                "카페",
                "디저트",
                "아메리카노",
                "라떼",
                "케이크");

        // 더미데이터 반환
        return result;
    }
}
