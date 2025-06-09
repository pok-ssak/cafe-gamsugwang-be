package pokssak.gsg.domain.cafe.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;
import pokssak.gsg.common.exception.CustomException;
import pokssak.gsg.common.vo.Keyword;
import pokssak.gsg.domain.bookmark.repository.BookmarkRepository;
import pokssak.gsg.domain.bookmark.service.BookmarkService;
import pokssak.gsg.domain.cafe.dto.*;
import pokssak.gsg.domain.cafe.entity.*;
import pokssak.gsg.domain.cafe.repository.*;
import pokssak.gsg.domain.user.dto.UserKeywordResponse;
import pokssak.gsg.domain.user.entity.User;
import pokssak.gsg.domain.user.entity.UserKeyword;
import pokssak.gsg.domain.user.repository.UserRepository;
import pokssak.gsg.domain.user.service.UserKeywordService;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@SpringBootTest
@Transactional
class CafeServiceTest {
    @MockitoBean
    private CafeESClient cafeESClient;
    @Autowired
    private CafeESRepository cafeESRepository;
    @MockitoBean
    private CafeRepository cafeRepository;
    @Autowired
    private BookmarkRepository bookmarkRepository;
    @MockitoBean
    private SuggestionRedisRepository suggestionRedisRepository;
    @Autowired
    private UserRepository userRepository;
    @MockitoBean
    private KeywordRepository keywordRepository;
    @MockitoBean
    private UserKeywordService userKeywordService;
    @MockitoBean
    private BookmarkService bookmarkService;
    @Autowired
    private CafeService cafeService;

    private Cafe testCafe;

    @BeforeEach
    void setUp() {
        bookmarkRepository.deleteAllInBatch();
        cafeRepository.deleteAllInBatch();

    }

    @AfterEach
    void tearDown() {
        bookmarkRepository.deleteAllInBatch();
        cafeRepository.deleteAll();
    }



    @DisplayName("카페 ID로 카페 정보를 조회한다.")
    @Test
    void getCafeById() {
        // given
        Cafe cafe = Cafe.builder()
                .id(1L)
                .title("테스트 카페")
                .build();

        Mockito.when(cafeRepository.findById(cafe.getId()))
                .thenReturn(Optional.of(cafe));
        // when
        Cafe findCafe = cafeService.getCafeById(cafe.getId());

        // then
        Assertions.assertThat(findCafe)
                .extracting("id", "title")
                .containsExactly(cafe.getId(), cafe.getTitle());
    }

    @DisplayName("카페 제목을 자동완성한다.")
    @Test
    void autoComplete() {
        // given
        String keyword = "카페";
        int limit = 10;

        AutoCompleteResponse cafe1 = AutoCompleteResponse.from(1L, "카페1");
        AutoCompleteResponse cafe2 = AutoCompleteResponse.from(2L, "카페2");

        Mockito.when(cafeESClient.suggestTitleByKeyword(Mockito.anyString(), Mockito.anyInt()))
                .thenReturn(List.of(cafe1, cafe2));
        // when
        List<AutoCompleteResponse> autoCompleteResponses = cafeService.autoComplete(keyword, limit);
        // then
        Assertions.assertThat(autoCompleteResponses)
                .hasSize(2)
                .extracting("id", "title")
                .containsExactlyInAnyOrder(
                        Assertions.tuple(1L, "카페1"),
                        Assertions.tuple(2L, "카페2")
                );
    }

    @DisplayName("키워드 기반으로 카페를 추천한다.")
    @Test
    void recommendByKeyword() {
        // given
        String keyword = "카페";
        int limit = 10;

        User user = User.builder()
                .nickName("테스트유저")
                .email("test@example.com")
                .isDeleted(false)
                .build();

        user = userRepository.save(user);
        Long savedUserId = user.getId();

        List<CafeDocument> cafeDocuments = createCafeDocuments();

        RecommendResponse cafe1 = RecommendResponse.from(cafeDocuments.get(0));
        RecommendResponse cafe2 = RecommendResponse.from(cafeDocuments.get(1));

        Mockito.when(cafeESClient.recommendByKeyword(keyword, limit))
                .thenReturn(List.of(cafe1, cafe2));

        // BookmarkService mock 처리
        Mockito.when(bookmarkService.getUserBookmarks(savedUserId))
                .thenReturn(Collections.emptyList());

        Mockito.when(userKeywordService.getUserKeywords(savedUserId))
                .thenReturn(List.of(
                        UserKeywordResponse.from(
                                UserKeyword.builder()
                                        .id(1L)
                                        .user(user)
                                        .keyword(Keyword.builder().word("분위기 좋은").count(1L).build())
                                        .build()
                        )
                ));

        // when
        List<RecommendResponse> recommendResponses = cafeService.recommendByKeyword(savedUserId, keyword, limit);

        // then
        Assertions.assertThat(recommendResponses)
                .hasSize(2)
                .extracting("id", "title")
                .containsExactlyInAnyOrder(
                        Assertions.tuple(1L, "카페1"),
                        Assertions.tuple(2L, "카페2")
                );
    }


    @DisplayName("위치 기반으로 카페를 추천한다.")
    @Test
    void recommendByLocation() {
        Long userId = 1L;
        int limit = 10;
        double lat = 37.5665;
        double lon = 126.9780;

        List<CafeDocument> cafeDocuments = createCafeDocuments();

        RecommendResponse cafe1 = RecommendResponse.from(cafeDocuments.get(0));
        RecommendResponse cafe2 = RecommendResponse.from(cafeDocuments.get(1));

        Mockito.when(cafeESClient.recommendByLocation(Mockito.eq(lat), Mockito.eq(lon), Mockito.anyInt(), Mockito.eq(limit)))
                .thenReturn(List.of(cafe1, cafe2));

        Mockito.when(bookmarkService.getUserBookmarks(userId))
                .thenReturn(Collections.emptyList());

        // when
        List<RecommendResponse> recommendResponses = cafeService.recommendByLocation(userId, lat, lon, limit);

        // then
        Assertions.assertThat(recommendResponses)
                .hasSize(2)
                .extracting("id", "title", "isBookmarked")
                .containsExactlyInAnyOrder(
                        Assertions.tuple(1L, "카페1", false),
                        Assertions.tuple(2L, "카페2", false)
                );
    }

    @Test
    void getCafes() {
    }

    @DisplayName("유저 ID가 있을 때 북마크 여부를 포함한 카페 정보를 반환한다.")
    @Test
    void getCafeWithUserId() {
        // given
        Long userId = 1L;
        Long cafeId = 1L;
        Cafe cafe = Cafe.builder()
                .id(cafeId)
                .title("테스트 카페")
                .build();

        Mockito.when(cafeRepository.findByIdWithMenusAndCafeKeywords(cafeId))
                .thenReturn(Optional.of(cafe));


        // when
        GetCafeResponse response = cafeService.getCafe(userId, cafeId);

        // then
        Assertions.assertThat(response)
                .extracting("id", "title", "isBookmarked")
                .containsExactly(cafe.getId(), cafe.getTitle(), false);
    }

    @DisplayName("존재하지 않는 카페 ID로 조회 시 예외를 던진다.")
    @Test
    void getCafeWithNonExistentIdThrowsException() {
        // given
        Long cafeId = 999L;

        Mockito.when(cafeRepository.findByIdWithMenusAndCafeKeywords(Mockito.eq(cafeId)))
                .thenReturn(java.util.Optional.empty());

        // when & then
        Assertions.assertThatThrownBy(() -> cafeService.getCafe(1L, cafeId))
                .isInstanceOf(CustomException.class);
    }


    @DisplayName("유효한 요청으로 카페 제안을 저장한다.")
    @Test
    void suggestCafeWithValidRequest() {
        // given
        Long userId = 1L;
        Long cafeId = 1L;

        Menu menu = Menu.builder()
                .name("아메리카노")
                .menuImageUrl("https://example.com/menu.jpg")
                .price(4500)
                .modifier("ICE")
                .build();

        CafeKeyword cafeKeyword = CafeKeyword.builder()
                .id(1L)
                .keyword("조용한")
                .count(100)
                .build();

        SuggestRequest request = SuggestRequest.builder()
                .title("새 카페")
                .info("새로운 정보")
                .openTime("09:00-18:00")
                .imageUrl("https://example.com/new-image.jpg")
                .address("서울특별시 강남구")
                .phoneNumber("010-1234-5678")
                .menuList(Set.of(menu))
                .cafeKeywordList(Set.of(cafeKeyword))
                .build();

        Cafe oldCafe = Cafe.builder()
                .id(cafeId)
                .title("기존 카페")
                .build();

        Mockito.when(cafeRepository.findByIdWithMenusAndCafeKeywords(Mockito.eq(cafeId)))
                .thenReturn(Optional.of(oldCafe));

        Mockito.when(keywordRepository.findById(1L))
                .thenReturn(Optional.of(cafeKeyword));

        // when
        cafeService.suggestCafe(userId, cafeId, request);

        // then
        Mockito.verify(suggestionRedisRepository, Mockito.times(1)).save(Mockito.any(Suggestion.class));
    }

    @DisplayName("유효한 검색어로 카페를 검색하면 결과를 반환한다.")
    @Test
    void searchCafesWithValidQuery() {
        // given
        String query = "카페";
        int limit = 10;

        List<CafeDocument> cafeDocuments = createCafeDocuments();
        SearchCafeResponse cafe1 = SearchCafeResponse.from(cafeDocuments.get(0));
        SearchCafeResponse cafe2 = SearchCafeResponse.from(cafeDocuments.get(1));

        Mockito.when(cafeESClient.searchByTitle(Mockito.eq(query), Mockito.eq(limit)))
                .thenReturn(List.of(cafe1, cafe2));

        // when
        List<SearchCafeResponse> response = cafeService.searchCafes(query, limit);

        // then
        Assertions.assertThat(response)
                .hasSize(2)
                .extracting("id", "title")
                .containsExactlyInAnyOrder(
                        Assertions.tuple(1L, "카페1"),
                        Assertions.tuple(2L, "카페2")
                );
    }

    @DisplayName("유저가 인증된 경우 키워드 기반으로 카페를 추천한다.")
    @Test
    void recommendByUserInfoWithAuthenticatedUser() {
        // given
        User user = User.builder()
                .id(1L)
                .build();
        Double lat = 37.5665;
        Double lon = 126.9780;
        int limit = 10;

        UserKeyword.UserKeywordBuilder keyword1 = UserKeyword.builder()
                .id(1L)
                .user(user)
                .keyword(Keyword.builder().word("분위기 좋은").count(1L).build());

        UserKeyword.UserKeywordBuilder keyword2 = UserKeyword.builder()
                .id(2L)
                .user(user)
                .keyword(Keyword.builder().word("맛있는").count(1L).build());


        List<CafeDocument> cafeDocuments = createCafeDocuments();
        RecommendResponse cafe1 = RecommendResponse.from(cafeDocuments.get(0));
        RecommendResponse cafe2 = RecommendResponse.from(cafeDocuments.get(1));


        Mockito.when(userKeywordService.getUserKeywords(user.getId()))
                .thenReturn(List.of(
                        UserKeywordResponse.from(keyword1.build()),
                        UserKeywordResponse.from(keyword2.build())
                ));
        Mockito.when(cafeESClient.recommendByHybrid(Mockito.anyString(), Mockito.eq(lat), Mockito.eq(lon), Mockito.eq(limit)))
                .thenReturn(List.of(cafe1, cafe2));


        // when
        List<RecommendResponse> result = cafeService.recommendByUserInfo(user, lat, lon, limit);

        // then
        Assertions.assertThat(result)
                .hasSize(2)
                .extracting("id", "title", "isBookmarked")
                .containsExactlyInAnyOrder(
                        Assertions.tuple(1L, "카페1", false),
                        Assertions.tuple(2L, "카페2", false)
                );
    }

    @DisplayName("유저가 인증되지 않은 경우 기본 키워드로 카페를 추천한다.")
    @Test
    void recommendByUserInfoWithUnauthenticatedUser() {
        // given
        User user = null; // 인증되지 않은 유저
        Double lat = 37.5665;
        Double lon = 126.9780;
        int limit = 10;

        List<CafeDocument> cafeDocuments = createCafeDocuments();
        RecommendResponse cafe1 = RecommendResponse.from(cafeDocuments.get(0));
        RecommendResponse cafe2 = RecommendResponse.from(cafeDocuments.get(1));

        Mockito.when(cafeESClient.recommendByHybrid(Mockito.anyString(), Mockito.eq(lat), Mockito.eq(lon), Mockito.eq(limit)))
                .thenReturn(List.of(cafe1, cafe2));

        // when
        List<RecommendResponse> result = cafeService.recommendByUserInfo(user, lat, lon, limit);

        // then
        Assertions.assertThat(result)
                .hasSize(2)
                .extracting("id", "title", "isBookmarked")
                .containsExactlyInAnyOrder(
                        Assertions.tuple(1L, "카페1", false),
                        Assertions.tuple(2L, "카페2", false)
                );
    }



    private List<CafeDocument> createCafeDocuments() {
        CafeDocument cd1 = CafeDocument.builder()
                .id(1L)
                .title("카페1")
                .imgUrl("https://example.com/image.jpg")
                .address(CafeDocument.Address.builder()
                        .street("서울특별시 강남구")
                        .location(new GeoPoint(37.5665, 126.9780))
                        .build())
                .reviewCount(100)
                .rate(new BigDecimal("4.5"))
                .keywords(List.of(
                        CafeDocument.Keyword.builder().key("분위기 좋은").build(),
                        CafeDocument.Keyword.builder().key("커피 맛있는").build()
                ))
                .build();

        CafeDocument cd2 = CafeDocument.builder()
                .id(2L)
                .title("카페2")
                .imgUrl("https://example.com/image2.jpg")
                .address(CafeDocument.Address.builder()
                        .street("서울특별시 강남구")
                        .location(new GeoPoint(37.5665, 126.9780))
                        .build())
                .reviewCount(50)
                .rate(new BigDecimal("4.0"))
                .keywords(List.of(
                        CafeDocument.Keyword.builder().key("조용한").build(),
                        CafeDocument.Keyword.builder().key("디저트 맛있는").build()
                ))
                .build();

        return List.of(cd1, cd2);
    }
}