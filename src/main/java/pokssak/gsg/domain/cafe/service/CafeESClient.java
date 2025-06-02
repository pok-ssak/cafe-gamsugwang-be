package pokssak.gsg.domain.cafe.service;

import co.elastic.clients.elasticsearch._types.LatLonGeoLocation;
import co.elastic.clients.elasticsearch._types.query_dsl.*;
import co.elastic.clients.elasticsearch.core.search.FieldCollapse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.embedding.Embedding;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.stereotype.Service;
import pokssak.gsg.domain.cafe.dto.AutoCompleteResponse;
import pokssak.gsg.domain.cafe.dto.RecommendResponse;
import pokssak.gsg.domain.cafe.dto.SearchCafeResponse;
import pokssak.gsg.domain.cafe.entity.CafeDocument;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CafeESClient {
    private final ElasticsearchOperations operations;
    private final EmbeddingModel embeddingModel;

    /**
     * 카페 제목 자동완성
     * @param keyword
     * @param size
     * @return
     */
    public List<AutoCompleteResponse> suggestTitleByKeyword(String keyword, int size) {
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
                .withSourceFilter(new FetchSourceFilter(new String[]{"title","_id"}, null))
                .withFieldCollapse(FieldCollapse.of(fc -> fc.field("title.keyword")))
                .build();

        log.info("Suggesting titles for keyword: {}", keyword);
        SearchHits<CafeDocument> hits = operations.search(query, CafeDocument.class);

        return hits.stream()
                .map(SearchHit::getContent)
                .map(c -> AutoCompleteResponse.from(c.getId(), c.getTitle()))
                .toList();
    }

    public List<Float> getEmbedding(String inputText){
        log.info("keyword: {}", inputText);
        EmbeddingResponse embeddingResponse = embeddingModel.embedForResponse(List.of(inputText));
        log.info("metaData: {}", embeddingResponse.getMetadata());

        Embedding result = embeddingResponse.getResult();
        float[] output = result.getOutput();

        //to List<Float>
        ArrayList<Float> outputList = new ArrayList<>(output.length);
        for (float v : output) {
            outputList.add(v);
        }

        return outputList;
    }

    //{
    //  "_source": [
    //    "title",
    //    "keywords"
    //  ],
    //  "size": 10,
    //  "knn": {
    //    "field": "keywordVector",
    //    "query_vector": [
    //                -0.04419745,
    //                -0.028461313,
    //                ...
    //                0.038790904,
    //                0.00996146,
    //                0.014802081,
    //                0.021714114,
    //                -0.004120846
    //    ],
    //    "k": 10,
    //    "num_candidates": 1000
    //  }
    //}
    public List<RecommendResponse> recommendByKeyword(String keyword, int size) {
        List<Float> embedding = getEmbedding(keyword);


        NativeQuery query = NativeQuery.builder()
                .withKnnSearches(k -> k
                        .field("keywordVector")
                        .queryVector(embedding)
                        .k(size)
                        .numCandidates(1000))
                .withPageable(PageRequest.of(0, size))
                .withSort(Sort.by(Sort.Order.desc("_score")))
                .withSourceFilter(new FetchSourceFilter(new String[]{"title","keywords","rate","address","imgUrl","reviewCount"}, null))
                .build();
        SearchHits<CafeDocument> hits = operations.search(query, CafeDocument.class);
        log.info("hits: {}", hits);
        return hits.stream()
                .map(SearchHit::getContent)
                .map(RecommendResponse::from)
                .toList();

    }

    public List<RecommendResponse> recommendByHybrid(String keyword, Double lat, Double lon, int limit) {
        List<Float> embedding = getEmbedding(keyword);

        NativeQuery query = NativeQuery.builder()
                .withSourceFilter(new FetchSourceFilter(new String[]{"title","keywords","rate","address","imgUrl","reviewCount"}, null))
                .withKnnSearches(k -> k
                        .field("keywordVector")
                        .queryVector(embedding)
                        .k(limit)
                        .numCandidates(1000))
                .withQuery(q -> q
                        .functionScore(fs -> fs
                                .scoreMode(FunctionScoreMode.Sum)
                                .boostMode(FunctionBoostMode.Replace)
                        )
                )
                .withQuery(Query.of(q -> q.functionScore(fs -> fs
                        .scoreMode(FunctionScoreMode.Sum)
                        .boostMode(FunctionBoostMode.Replace)
                        .functions(List.of(
                                FunctionScore.of(fn -> fn
                                        .fieldValueFactor(fvf -> fvf
                                                .field("_score")
                                                .factor(0.8)
                                                .missing(0.0)))
                                ,
                                FunctionScore.of(fn -> fn
                                        .filter(f -> f.nested(n -> n
                                                .path("address")
                                                .query(nq -> nq.geoDistance(g -> g
                                                        .field("address.location")
                                                        .distance("5km")
                                                        .location(loc -> loc
                                                                .latlon(LatLonGeoLocation.of(l -> l
                                                                        .lat(lat)
                                                                        .lon(lon)
                                                                ))
                                                        )))))
                                        .weight(0.2)
                                ))
                        ))))
                .withPageable(PageRequest.of(0, limit))
                .build();

        SearchHits<CafeDocument> hits = operations.search(query, CafeDocument.class);
        log.info("hits: {}", hits);
        return hits.stream()
                .map(SearchHit::getContent)
                .map(RecommendResponse::from)
                .toList();

    }

    public List<RecommendResponse> recommendByLocation(Double lat, Double lon, int radius, int size) {
        NativeQuery nativeQuery = NativeQuery.builder()
                .withQuery(q -> q.bool(b -> b
                        .filter(f -> f.nested(n -> n
                                .path("address")
                                .query(nq -> nq.geoDistance(g -> g
                                        .field("address.location")
                                        .distance(radius+ "km")
                                                .location(loc -> loc
                                                        .latlon(LatLonGeoLocation.of(l -> l
                                                                .lat(lat)
                                                                .lon(lon)
                                                        ))
                                        )))))))
                .withPageable(PageRequest.of(0, size))
                .withSourceFilter(new FetchSourceFilter(new String[]{"title","keywords","rate","address","reviewCount","imgUrl"}, null))
                .build();

        SearchHits<CafeDocument> hits = operations.search(nativeQuery, CafeDocument.class);
        log.info("hits: {}", hits);
        return hits.stream()
                .map(SearchHit::getContent)
                .map(RecommendResponse::from)
                .toList();
    }

    public List<SearchCafeResponse> searchByTitle(String query, int limit) {
        NativeQuery nativeQuery = NativeQuery.builder()
                .withQuery(q -> q.match(m -> m
                        .field("title")
                        .query(query)
                        .operator(Operator.And)))
                .withPageable(PageRequest.of(0, 50))
                .withSort(Sort.by(Sort.Order.desc("reviewCount")))
                .withSourceFilter(new FetchSourceFilter(new String[]{"title", "keywords", "rate", "address", "reviewCount","imgUrl"}, null))
                .build();

        SearchHits<CafeDocument> hits = operations.search(nativeQuery, CafeDocument.class);
        log.info("hits: {}", hits);
        return hits.stream()
                .map(SearchHit::getContent)
                .map(SearchCafeResponse::from)
                .toList();
    }


}
