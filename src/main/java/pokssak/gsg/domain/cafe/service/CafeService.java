package pokssak.gsg.domain.cafe.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pokssak.gsg.common.exception.CustomException;
import pokssak.gsg.domain.cafe.dto.RecommendResponse;
import pokssak.gsg.domain.cafe.dto.GetCafeResponse;
import pokssak.gsg.domain.cafe.dto.SuggestRequest;
import pokssak.gsg.domain.cafe.entity.Cafe;
import pokssak.gsg.domain.cafe.entity.CafeDocument;
import pokssak.gsg.domain.cafe.entity.Suggestion;
import pokssak.gsg.domain.cafe.exception.CafeErrorCode;
import pokssak.gsg.domain.cafe.repository.CafeESRepository;
import pokssak.gsg.domain.cafe.repository.CafeRepository;
import pokssak.gsg.domain.cafe.repository.SuggestionRedisRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CafeService {
    private final CafeSearchService cafeSearchService;
    private final CafeESRepository cafeESRepository;
    private final CafeRepository cafeRepository;
    private final SuggestionRedisRepository suggestionRedisRepository;

    public Cafe getCafeById(Long cafeId) {
        return cafeRepository.findById(cafeId).get();
    }

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

    public Page<GetCafeResponse> getCafes(Pageable pageable) {
        var result = cafeRepository.findAll(pageable).map(GetCafeResponse::from);
        return result;
    }


    public GetCafeResponse getCafe(Long cafeId) {
        log.info("cafeId: {}", cafeId);
        Cafe cafe = cafeRepository.findByIdWithMenusAndKeywords(cafeId).orElseThrow(() -> new CustomException(CafeErrorCode.CAFE_NOT_FOUND));

        return GetCafeResponse.from(cafe);
        //return getCafeResponse.from(cafe);
    }

    public CafeDocument getCafeDocument(Long cafeId) {
        log.info("cafeId: {}", cafeId);
        CafeDocument cafeDocument = cafeESRepository.findById(cafeId).orElseThrow(() -> new CustomException(CafeErrorCode.CAFE_NOT_FOUND));
        return cafeDocument;
    }

    public void suggestCafe(Long id, Long cafeId, SuggestRequest request) {
        log.info("id: {}, cafeId: {}, suggestion: {}", id, cafeId, request);
        Cafe oldCafe = cafeRepository.findByIdWithMenusAndKeywords(cafeId)
                .orElseThrow(() -> new CustomException(CafeErrorCode.CAFE_NOT_FOUND));

        Cafe newCafe = SuggestRequest.toEntity(request);
        Suggestion suggestion = Suggestion.builder()
                .userId(id)
                .oldCafe(oldCafe)
                .cafe(newCafe)
                .build();

        suggestionRedisRepository.save(suggestion);
    }
}

