package pokssak.gsg.domain.user.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pokssak.gsg.common.vo.Keyword;
import pokssak.gsg.domain.user.dto.KeywordRequest;
import pokssak.gsg.domain.user.dto.UserKeywordResponse;
import pokssak.gsg.domain.user.entity.JoinType;
import pokssak.gsg.domain.user.entity.User;
import pokssak.gsg.domain.user.service.UserKeywordService;

import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserKeywordControllerTest {

    @Mock
    private UserKeywordService userKeywordService;

    @InjectMocks
    private UserKeywordController userKeywordController;

    private User testUser;
    private List<Keyword> testKeywords;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .email("test@example.com")
                .nickName("tester")
                .imageUrl("https://image.com/test.png")
                .joinType(JoinType.LOCAL)
                .userKeywords(List.of())
                .build();
        testKeywords = List.of(
                new Keyword("조용한", 0L),
                new Keyword("24시간", 0L)
        );
    }

    @Test
    @DisplayName("사용자 키워드 조회 - 성공")
    void getUserKeywords_success() {
        // given
        List<UserKeywordResponse> mockResponse = List.of(
                new UserKeywordResponse(1L, "조용한", 0),
                new UserKeywordResponse(2L, "24시간", 0)
        );

        when(userKeywordService.getUserKeywords(testUser.getId())).thenReturn(mockResponse);

        // when
        ResponseEntity<List<UserKeywordResponse>> response = userKeywordController.getUserKeywords(testUser);

        // then
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).hasSize(2);
        assertThat(response.getBody().get(0).word()).isEqualTo("조용한");
        verify(userKeywordService).getUserKeywords(testUser.getId());
    }

//    @Test
//    @DisplayName("사용자 키워드 수정 - 성공")
//    void updateUserKeywords_success() {
//        // given
//        KeywordRequest request = new KeywordRequest(testKeywords);
//
//        // when
//        ResponseEntity<Void> response = userKeywordController.updateUserKeywords(testUser, request);
//
//        // then
//        assertThat(response.getStatusCodeValue()).isEqualTo(204);
//        verify(userKeywordService).updateUserKeywords(testUser.getId(), testKeywords);
//    }
}
