package pokssak.gsg.domain.cafe.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import pokssak.gsg.domain.cafe.dto.SuggestRequest;
import pokssak.gsg.domain.cafe.service.CafeService;
import pokssak.gsg.domain.review.service.ReviewService;
import pokssak.gsg.domain.user.entity.User;

import java.math.BigDecimal;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CafeController.class)
class CafeControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CafeService cafeService;

    @MockitoBean
    private ReviewService reviewService;

    @MockitoBean
    private EmbeddingModel embeddingModel;

    @Autowired
    private CafeController cafeController;

    @DisplayName("카페 정보를 조회한다.")
    @WithMockUser(username = "testUser", roles = "USER")
    @Test
    void getCafe() throws Exception {
        // given
        Long cafeId = 1L;
        // when // then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/cafes/{cafeId}", cafeId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @DisplayName("카페 정보 수정 제안 요청을 처리한다.")
    @Test
    void suggestCafe() throws Exception{
        // given
        Long cafeId = 1L;
        User mockUser = createMockUser();

        SuggestRequest request = SuggestRequest.builder()
                .title("제목")
                .info("내용")
                .openTime("09:00")
                .rate(new BigDecimal("4.5"))
                .reviewCount(10)
                .imageUrl("https://example.com/image.jpg")
                .address("서울시 강남구")
                .zipcode("12345")
                .lat(new BigDecimal("37.5665"))
                .lon(new BigDecimal("126.9780"))
                .phoneNumber("010-1234-5678")
                .menuList(new HashSet<>())
                .keywordList(new HashSet<>())
                .build();

        // when // then
        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/cafes/{cafeId}/suggest", cafeId)
                        .with(user(mockUser))
                        .with(csrf())
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk());
    }

    @DisplayName("검색어가 파라미터로 전달되면 자동완성 기능을 수행한다.")
    @Test
    void autoComplete() throws Exception {
        // given
        String keyword = "제주";
        int limit = 10;

        User mockUser = createMockUser();

        // when // then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/cafes/auto-complete")
                        .with(user(mockUser))
                .param("keyword", keyword)
                .param("limit", String.valueOf(limit))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @DisplayName("카페 검색 기능을 수행한다.")
    @Test
    void searchCafes() throws Exception {
        // given
        String query = "제주";
        int limit = 50;

        User mockUser = createMockUser();

        // when // then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/cafes/search")
                        .with(user(mockUser))
                .param("query", query)
                .param("limit", String.valueOf(limit))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @DisplayName("사용자 위치 정보를 기반으로 카페 추천 기능을 수행한다.")
    @Test
    void recommendWithLocation() throws Exception {
        // given
        String option = "location";
        String keyword = "제주";
        Double lat = 37.5665;
        Double lon = 126.9780;
        int limit = 10;

        User mockUser = createMockUser();

        // when // then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/cafes/recommend")
                        .with(user(mockUser))
                .param("option", option)
                .param("keyword", keyword)
                .param("lat", String.valueOf(lat))
                .param("lon", String.valueOf(lon))
                .param("limit", String.valueOf(limit))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @DisplayName("입력된 키워드 정보를 기반으로 카페 추천 기능을 수행한다.")
    @Test
    void recommendWithKeyword() throws Exception {
        // given
        String option = "keyword";
        String keyword = "제주";
        Double lat = 37.5665;
        Double lon = 126.9780;
        int limit = 10;

        User mockUser = createMockUser();

        // when // then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/cafes/recommend")
                        .with(user(mockUser))
                        .param("option", option)
                        .param("keyword", keyword)
                        .param("lat", String.valueOf(lat))
                        .param("lon", String.valueOf(lon))
                        .param("limit", String.valueOf(limit))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @DisplayName("입력된 키워드, 위치정보를 기반으로 카페 추천 기능을 수행한다.")
    @Test
    void recommendWithHybrid() throws Exception {
        // given
        String option = "hybrid";
        String keyword = "제주";
        Double lat = 37.5665;
        Double lon = 126.9780;
        int limit = 10;

        User mockUser = createMockUser();

        // when // then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/cafes/recommend")
                        .with(user(mockUser))
                        .param("option", option)
                        .param("keyword", keyword)
                        .param("lat", String.valueOf(lat))
                        .param("lon", String.valueOf(lon))
                        .param("limit", String.valueOf(limit))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @DisplayName("카페 추천 시 잘못된 옵션이 입력된 경우, Bad Request를 반환한다.")
    @Test
    void recommendWithInvalidOption() throws Exception {
        // given
        String option = "invalidOption"; // 잘못된 옵션
        String keyword = "제주";
        Double lat = 37.5665;
        Double lon = 126.9780;
        int limit = 10;

        User mockUser = createMockUser();

        // when // then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/cafes/recommend")
                        .with(user(mockUser))
                        .param("option", option)
                        .param("keyword", keyword)
                        .param("lat", String.valueOf(lat))
                        .param("lon", String.valueOf(lon))
                        .param("limit", String.valueOf(limit))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("사용자의 정보를 기반으로 카페 추천 기능을 수행한다.")
    @Test
    void customRecommend() throws Exception {
        // given
        User mockUser = createMockUser();

        // when // then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/cafes/self-recommend")
                        .with(user(mockUser))
                .param("lat", "37.5665")
                .param("lon", "126.9780")
                .param("limit", "10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @DisplayName("카페 리뷰 조회 시 잘못된 카페 ID가 전달되면 Bad Request를 반환한다.")
    @WithMockUser(username = "testUser", roles = "USER")
    @Test
    void getCafeReviewsWithInvalidCafeId() throws Exception {
        String invalidCafeId = "invalid";

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/cafes/{cafeId}/reviews", invalidCafeId)
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("카페 목록을 페이지네이션으로 조회한다.")
    @WithMockUser(username = "testUser", roles = "USER")
    @Test
    void getCafesWithPagination() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/cafes")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @DisplayName("카페 목록 조회 시 페이지네이션 파라미터가 누락되면 기본값으로 처리한다.")
    @WithMockUser(username = "testUser", roles = "USER")
    @Test
    void getCafesWithDefaultPagination() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/cafes")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    void getCafeReviewsWithPagination() throws Exception {
        Long cafeId = 1L;

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/cafes/{cafeId}/reviews", cafeId)
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }




    @DisplayName("유저의 ID가 null이 아닌 경우 유저id를 반환한다.")
    @Test
    void getUserIdWithNonNullUser() throws Exception {
        // given
        Long cafeId = 1L;

        User mockUser = createMockUser();

        // when // then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/cafes/{cafeId}", cafeId)
                        .with(user(mockUser))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        Mockito.verify(cafeService).getCafe(1L, cafeId);
    }

    @DisplayName("유저의 ID가 null인 경우 null을 반환한다.")
    @Test
    void getUserIdWithNullUser() throws Exception {
        // given
        Long cafeId = 1L;

        User mockUser = User.builder()
                .id(null) // User ID is null
                .build();


        // when // then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/cafes/{cafeId}", cafeId)
                        .with(user(mockUser))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        Mockito.verify(cafeService).getCafe(null, cafeId);
    }

    private User createMockUser() {
        return User.builder()
                .id(1L)
                .password("password")
                .build();
    }
}