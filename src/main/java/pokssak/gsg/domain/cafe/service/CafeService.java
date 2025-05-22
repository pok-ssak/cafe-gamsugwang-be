package pokssak.gsg.domain.cafe.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pokssak.gsg.domain.cafe.dto.RecommendResponse;
import pokssak.gsg.domain.cafe.repository.CafeESRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CafeService {
    private final CafeSearchService cafeSearchService;
    private final CafeESRepository cafeESRepository;


    /**
     * 카페 제목 자동완성
     * @param keyword
     * @param limit
     * @return
     */
    public List<String> autoComplete(String keyword, int limit) {
        log.info("keyword: {}, limit: {}", keyword, limit);
        List<String> titles = cafeSearchService.suggestTitleByKeyword(keyword, limit);
        log.info("titleByPrefix = {}", titles);

        return titles;
    }

    /**
     * 카페 추천 키워드 기반
     * @param keyword
     * @param limit
     * @return
     */
    public List<RecommendResponse> recommendByKeyword(String keyword, int limit) {
        log.info("keyword: {}, limit: {}", keyword, limit);
        List<RecommendResponse> cafeDocuments = cafeSearchService.recommendByKeyword(keyword, limit);
        log.info("cafeDocuments = {}", cafeDocuments);
        return cafeDocuments;
    }

    /**
     * 카페 추천 위치 기반
     * @param lat
     * @param lon
     * @param limit
     * @return
     */
    public List<RecommendResponse> recommendByLocation(Double lat, Double lon, int limit) {
        log.info("lat: {}, lon: {}, limit: {}", lat, lon, limit);
        List<RecommendResponse> cafeDocuments = cafeSearchService.recommendByLocation(lat, lon, 20, limit);
        log.info("cafeDocuments = {}", cafeDocuments);
        return cafeDocuments;
    }
}
