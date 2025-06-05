package pokssak.gsg.domain.cafe.service;

import co.elastic.clients.elasticsearch._types.query_dsl.Operator;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.FieldCollapse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.StringQuery;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pokssak.gsg.domain.cafe.entity.Keyword;
import pokssak.gsg.domain.cafe.entity.KeywordDocument;
import pokssak.gsg.domain.cafe.repository.KeywordESRepository;
import pokssak.gsg.domain.cafe.repository.KeywordRepository;

import java.util.*;

@EnableScheduling
@Slf4j
@Service
@RequiredArgsConstructor
public class KeywordService {

    private final KeywordRepository keywordRepository;
    private final KeywordESRepository keywordESRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ElasticsearchOperations operations;

    private static final int BATCH_SIZE = 1000;

    @Scheduled(cron = "0 0 * * * *") // 매시 정각
    public void syncDataToES() {
        log.info("Elasticsearch 동기화 시작");

        int page = 0;
        Page<Keyword> pageData;

        do {
            pageData = keywordRepository.findAll(PageRequest.of(page++, BATCH_SIZE));
            List<KeywordDocument> documents = pageData.getContent().stream()
                    .map(entity -> {
                        List<Float> vector = new ArrayList<>();
                        try {
                            vector = objectMapper.readValue(entity.getVectors(),new TypeReference<List<Float>>() {});
                        } catch (JsonProcessingException e) {
                            log.error("Keyword ID {} 벡터 파싱 실패: {}", entity.getId(), e.getMessage());
                        }

                        return KeywordDocument.builder()
                                .id(entity.getId())
                                .keyword(entity.getKeyword())
                                .keywordVector(vector)
                                .createdAt(entity.getCreatedAt())
                                .modifiedAt(entity.getModifiedAt())
                                .build();
                    })
                    .toList();

            keywordESRepository.saveAll(documents);
        } while (!pageData.isLast());

        log.info("동기화 완료");
    }

    @Transactional(readOnly = true)
    public List<String> getSimilarKeywords(String query, int size) {
        // 1. 정확히 일치하는 keyword 문서 조회
        NativeQuery matchQuery = NativeQuery.builder()
                .withQuery(q -> q.match(m -> m
                        .field("keyword")
                        .query(query)
                        .operator(Operator.And)))
                .withPageable(PageRequest.of(0, 1))
                .build();

        SearchHits<KeywordDocument> matchHits = operations.search(matchQuery, KeywordDocument.class);

        if (matchHits.isEmpty()) {
            throw new NoSuchElementException("해당 키워드가 ES에 존재하지 않습니다: " + query);
        }

        List<Float> queryVector = matchHits.getSearchHit(0).getContent().getKeywordVector();

        // 2. KNN 검색 쿼리 실행 (fieldCollapse 추가)
        NativeQuery knnQuery = NativeQuery.builder()
                .withKnnSearches(knn -> knn
                        .field("keywordVector")
                        .queryVector(queryVector)
                        .k(size + 5)           // 중복 제거 고려해 좀 더 많이 요청
                        .numCandidates(1000))
                .withPageable(PageRequest.of(0, size + 5))
                .withSourceFilter(new FetchSourceFilter(new String[]{"keyword", "keywordVector"}, null))
                .withFieldCollapse(FieldCollapse.of(fc -> fc.field("keyword"))) // 중복 키워드 묶기
                .build();

        SearchHits<KeywordDocument> knnHits = operations.search(knnQuery, KeywordDocument.class);

        return knnHits.stream()
                .map(hit -> hit.getContent().getKeyword())
                .filter(k -> !k.equals(query)) // 자기 자신 제외
                .distinct()                      // 혹시 모를 중복 제거
                .limit(size)
                .toList();
    }


}
