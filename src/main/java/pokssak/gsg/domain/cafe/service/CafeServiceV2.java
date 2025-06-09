package pokssak.gsg.domain.cafe.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pokssak.gsg.common.exception.CustomException;
import pokssak.gsg.domain.bookmark.dto.BookmarkResponse;
import pokssak.gsg.domain.bookmark.service.BookmarkService;
import pokssak.gsg.domain.cafe.dto.*;
import pokssak.gsg.domain.cafe.entity.*;
import pokssak.gsg.domain.cafe.exception.CafeErrorCode;
import pokssak.gsg.domain.cafe.repository.CafeESRepository;
import pokssak.gsg.domain.cafe.repository.CafeRepository;
import pokssak.gsg.domain.cafe.repository.KeywordRepository;
import pokssak.gsg.domain.cafe.repository.SuggestionRedisRepository;
import pokssak.gsg.domain.user.dto.UserKeywordResponse;
import pokssak.gsg.domain.user.entity.User;
import pokssak.gsg.domain.user.service.UserKeywordService;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CafeServiceV2 {
    private final CafeESClient cafeESClient;
    private final CafeRepository cafeRepository;
    private final SuggestionRedisRepository suggestionRedisRepository;
    private final KeywordRepository keywordRepository;
    private final UserKeywordService userKeywordService;
    private final BookmarkService bookmarkService;
    private final SearchLogRepository logRepository;

    public Cafe getCafeById(Long cafeId) {
        return cafeRepository.findById(cafeId)
                .orElseThrow(() -> new CustomException(CafeErrorCode.CAFE_NOT_FOUND));
    }

    /**
     * 카페 제목 자동완성
     * @param keyword
     * @param limit
     * @return
     */
    public List<AutoCompleteResponse> autoComplete(String keyword, int limit) {
        log.info("keyword: {}, limit: {}", keyword, limit);
        List<AutoCompleteResponse> response = cafeESClient.suggestTitleByKeyword(keyword, limit);

        return response;
    }

    /**
     * 카페 추천 키워드 기반
     * @param keyword
     * @param limit
     * @return
     */
    public Page<RecommendResponse> recommendByKeyword(Long userId, String keyword, int limit, Pageable pageable) {
        log.info("keyword: {}, limit: {}", keyword, limit);

        Page<RecommendResponse> esPage = cafeESClient.recommendByKeyword(keyword, limit, pageable);
        List<RecommendResponse> recommendResponses = updateBookmarkStatus(userId, esPage.getContent());
        log.info("cafeDocuments = {}", recommendResponses);
        return new PageImpl<>(recommendResponses, pageable, esPage.getTotalElements());
    }

    private List<RecommendResponse> updateBookmarkStatus(Long userId, List<RecommendResponse> cafeDocuments) {
        if( userId == null || cafeDocuments.isEmpty()) {
            return cafeDocuments;
        }
        List<BookmarkResponse> userBookmarks = bookmarkService.getUserBookmarks(userId);
        return cafeDocuments.stream()
                .peek(cafe -> cafe.setIsBookmarked(userBookmarks.stream()
                        .anyMatch(bookmark -> bookmark.id().equals(cafe.getId()))))
                .toList();
    }

    /**
     * 카페 추천 위치 기반
     * @param lat
     * @param lon
     * @param limit
     * @return
     */
    public Page<RecommendResponse> recommendByLocation(Long userId, Double lat, Double lon, int limit, Pageable pageable) {
        log.info("lat: {}, lon: {}, limit: {}", lat, lon, limit);

        Page<RecommendResponse> esPage = cafeESClient.recommendByLocation(lat, lon, 20, limit, pageable);

        List<RecommendResponse> recommendResponses = updateBookmarkStatus(userId, esPage.getContent());
        log.info("recommendResponse = {}", recommendResponses);
        return new PageImpl<>(recommendResponses, pageable, esPage.getTotalElements());
    }

    public Page<GetCafeResponse> getCafes(Pageable pageable) {
        var result = cafeRepository.findAll(pageable).map(GetCafeResponse::from);
        return result;
    }


    public GetCafeResponse getCafe(Long userId, Long cafeId) {
        log.info("cafeId: {}", cafeId);
        Cafe cafe = cafeRepository.findByIdWithMenusAndKeywords(cafeId).orElseThrow(() -> new CustomException(CafeErrorCode.CAFE_NOT_FOUND));
        if (userId == null) {
            return GetCafeResponse.from(cafe);
        }
        List<BookmarkResponse> userBookmarks = bookmarkService.getUserBookmarks(userId);
        return GetCafeResponse.from(cafe, userBookmarks.stream()
                .anyMatch(bookmark -> bookmark.id().equals(cafe.getId())));

    }

//    public CafeDocument getCafeDocument(Long cafeId) {
//        log.info("cafeId: {}", cafeId);
//        CafeDocument cafeDocument = cafeESRepository.findById(cafeId).orElseThrow(() -> new CustomException(CafeErrorCode.CAFE_NOT_FOUND));
//        return cafeDocument;
//    }

    public void suggestCafe(Long id, Long cafeId, SuggestRequest request) {
        log.info("id: {}, cafeId: {}, suggestion: {}", id, cafeId, request);
        cafeRepository.findByIdWithMenusAndKeywords(cafeId)
                .orElseThrow(() -> new CustomException(CafeErrorCode.CAFE_NOT_FOUND));

        Set<MenuDto> menuDtos = request.getMenuList().stream()
                .map(m -> new MenuDto(m.getName(), m.getMenuImageUrl(), m.getPrice(), m.getModifier()))
                .collect(Collectors.toSet());

        Set<KeywordDto> keywordDtos = request.getCafeKeywordList().stream()
                .map(k -> {
                    CafeKeyword cafeKeyword = keywordRepository.findById(k.getId())
                            .orElseThrow(() -> new CustomException(CafeErrorCode.KEYWORD_NOT_FOUND));
                    return new KeywordDto(cafeKeyword.getId(), cafeKeyword.getKeyword(), cafeKeyword.getCount());
                })
                .collect(Collectors.toSet());

        Suggestion suggestion = Suggestion.builder()
                .userId(id)
                .oldCafeId(cafeId)
                .createdAt(LocalDateTime.now())
                .newCafe(Suggestion.NewCafeData.builder()
                        .title(request.getTitle())
                        .info(request.getInfo())
                        .openTime(request.getOpenTime())
                        .imageUrl(request.getImageUrl())
                        .address(request.getAddress())
                        .zipcode(request.getZipcode())
                        .phoneNumber(request.getPhoneNumber())
                        .menuList(menuDtos)
                        .keywordList(keywordDtos)
                        .build())
                .build();

        suggestionRedisRepository.save(suggestion);
    }

    public Page<SearchCafeResponse> searchCafes(String query, Pageable pageable) {
        log.info("search query: {}", query);
        Page<SearchCafeResponse> cafes = cafeESClient.searchByTitle(query, pageable);
//        if (cafes.isEmpty()) {
//            throw new CustomException(CafeErrorCode.CAFE_NOT_FOUND);
//        }
        SearchLog log = SearchLog.builder()
                .query(query)
                .type("title")
                .timestamp(new Date())
                .build();
        logRepository.save(log);
        return cafes;
    }

    public Page<RecommendResponse> recommendByUserInfo(User user, Double lat, Double lon, Pageable pageable) {
        String keywords = "카페";
        if(user != null){
            List<UserKeywordResponse> userKeywords = userKeywordService.getUserKeywords(user.getId());

            if (!userKeywords.isEmpty()) {
                keywords = userKeywords.stream()
                        .map(UserKeywordResponse::word)
                        .collect(Collectors.joining(" "));
            }
            log.info("user keywords: {}", keywords);
            Page<RecommendResponse> recommendResponses = cafeESClient.recommendByHybrid(keywords, lat, lon, pageable);
            List<BookmarkResponse> userBookmarks = bookmarkService.getUserBookmarks(user.getId());
            // 북마크 여부 추가
            return recommendResponses.map(cafe -> {
                cafe.setIsBookmarked(userBookmarks.stream()
                        .anyMatch(bookmark -> bookmark.id().equals(cafe.getId())));
                return cafe;
            });
//            return recommendResponses.stream()
//                    .peek(cafe -> cafe.setIsBookmarked(userBookmarks.stream()
//                            .anyMatch(bookmark -> bookmark.cafeId().equals(cafe.getId()))))
//                    .toList();

        }else{
            log.info("User is not authenticated, using default keywords.");
            return cafeESClient.recommendByHybrid(keywords, lat, lon, pageable);
        }
    }
}

